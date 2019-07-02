package com.dragon.sdk.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;

import com.clover.common.annotation.AccessLimit;
import com.clover.common.annotation.Pass;
import com.clover.common.config.web.http.ResponseHelper;
import com.clover.common.config.web.http.ResponseModel;
import com.clover.common.config.web.http.ResponsePageHelper;
import com.clover.common.config.web.http.ResponsePageModel;
import com.clover.common.controller.BaseController;
import com.clover.common.util.MD5Utils;
import com.clover.common.util.excel.ImportExcel;
import com.dragon.sdk.componment.CronWork;

import com.dragon.sdk.entity.GamePlayerMsg;
import com.dragon.sdk.service.IGamePlayerMsgService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 前端控制器
 *
 * @author liugh
 * @since 2019-05-09
 */
@RestController
@RequestMapping("/gamePlayerMsg")
public class GamePlayerMsgController extends BaseController {
  private static final Logger logger = LoggerFactory.getLogger(GamePlayerMsgController.class);
  @Resource IGamePlayerMsgService gamePlayerMsgService;
  @Resource CronWork cronWork;

  @GetMapping(value = "/pageList")
  @AccessLimit(perSecond = 1, timeOut = 500) // 5秒钟生成一个令牌
  /**
   * 分页列表
   *
   * @author dingpengfei
   * @date 2019-05-20 21:28
   * @param pageIndex 第几页
   * @param pageSize 每页多少条数据
   * @param imei imei
   * @param deviceId 设备码
   * @param ip ip
   * @param createTimeRange 注册时间范围
   * @return com.dragon.sdk.config.web.http.ResponsePageModel<com.dragon.sdk.entity.GamePlayerMsg>
   *     数据
   */
  public ResponsePageModel<GamePlayerMsg> findList(
      @RequestParam(name = "page", defaultValue = "1", required = false) Integer pageIndex,
      @RequestParam(name = "limit", defaultValue = "10", required = false) Integer pageSize,
      @RequestParam(value = "imei", defaultValue = "", required = false) String imei,
      @RequestParam(value = "deviceId", defaultValue = "", required = false) String deviceId,
      @RequestParam(value = "ip", defaultValue = "", required = false) String ip,
      @RequestParam(value = "createTime", defaultValue = "", required = false)
          String createTimeRange,
      @RequestParam(value = "field", defaultValue = "createTime", required = false) String field,
      @RequestParam(value = "order", defaultValue = "desc", required = false) String order) {
    Wrapper<GamePlayerMsg> wrapper;
    wrapper = new EntityWrapper<>();
    if (StringUtils.isNoneBlank(imei)) {
      wrapper = wrapper.like("imei", imei);
    }
    if (StringUtils.isNoneBlank(ip)) {
      wrapper = wrapper.like("ip", ip);
    }
    if (StringUtils.isNoneBlank(deviceId)) {
      wrapper = wrapper.like("device_id", deviceId);
    }

    wrapper =
        createTimeRange((EntityWrapper) wrapper, createTimeRange)
            .orderBy(underscoreName(field), order.equalsIgnoreCase("asc"));

    return ResponsePageHelper.buildResponseModel(
        gamePlayerMsgService.selectPage(new Page<>(pageIndex, pageSize), wrapper));
  }

  @RequestMapping(value = "/import")
  @Pass
  public ResponseModel<String> insert(@RequestParam(name = "file") MultipartFile file) {

    try {
      ImportExcel importExcel = new ImportExcel(file, 0, 0);
      List<GamePlayerMsg> gamePlayerMsgs = importExcel.getDataList(GamePlayerMsg.class);
      logger.info(JSONObject.toJSONString(gamePlayerMsgs));
      if (CollectionUtils.isEmpty(gamePlayerMsgs)) {
        throw new Exception("没有读取到数据");
      }
      List<GamePlayerMsg> gamePlayerMsgs2 = new ArrayList<>(10);
      for (GamePlayerMsg msg : gamePlayerMsgs) {
        if (StringUtils.isBlank(msg.getIp())) {
          continue;
        }
        int count =
            gamePlayerMsgService.selectCount(
                new EntityWrapper<GamePlayerMsg>().eq("ip", msg.getIp().trim()));
        if (count > 0) {
          continue;
        }
        msg.setStatus(0);
        msg.setDeleteFlag(0);
        if (StringUtils.isNoneBlank(msg.getImei())) {
          msg.setImeiEncode(MD5Utils.md5Digest(msg.getImei().trim()));
        }
        gamePlayerMsgs2.add(msg);
      }
      if (!CollectionUtils.isEmpty(gamePlayerMsgs2)) {

        gamePlayerMsgService.insertBatch(gamePlayerMsgs2, gamePlayerMsgs2.size());


        return ResponseHelper.buildResponseModel("导入数据成功:导入" + gamePlayerMsgs2.size() + "条");
      } else {
        return ResponseHelper.buildResponseModel("没有导入合适的数据");
      }
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseModel<>(e.getLocalizedMessage(), ResponseModel.FAIL.getCode());
    }
  }

  @RequestMapping(value = "/import1")
  @Pass
  public ResponseModel<String> import1(@RequestParam(name = "file") MultipartFile file) {

    try {
      ImportExcel importExcel = new ImportExcel(file, 0, 0);
      List<List<String>> lists = importExcel.getDataList();
      logger.info(JSONObject.toJSONString(lists));

      return ResponseHelper.buildResponseModel("导入数据成功:导入n条");

    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseModel<>(e.getLocalizedMessage(), ResponseModel.FAIL.getCode());
    }
  }

  @DeleteMapping(value = "/remove")
  public ResponseModel<String> remove(@RequestBody List<Long> ids) {
    try {
      gamePlayerMsgService.deleteBatchIds(ids);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseModel<>(e.getLocalizedMessage(), ResponseModel.FAIL.getCode());
    }

    return ResponseHelper.buildResponseModel("删除数据成功");
  }

  public static String underscoreName(String name) {
    StringBuilder result = new StringBuilder();
    if (name != null && name.length() > 0) {
      // 将第一个字符处理成大写
      result.append(name.substring(0, 1).toUpperCase());
      // 循环处理其余字符
      for (int i = 1; i < name.length(); i++) {
        String s = name.substring(i, i + 1);
        // 在大写字母前添加下划线
        if (s.equals(s.toUpperCase()) && !Character.isDigit(s.charAt(0))) {
          result.append("_");
        }
        // 其他字符直接转成大写
        result.append(s.toUpperCase());
      }
    }
    return result.toString();
  }
}
