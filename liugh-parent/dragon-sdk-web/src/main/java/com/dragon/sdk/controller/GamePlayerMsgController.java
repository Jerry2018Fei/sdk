package com.dragon.sdk.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.dragon.sdk.annotation.AccessLimit;
import com.dragon.sdk.annotation.Pass;
import com.dragon.sdk.config.ResponseHelper;
import com.dragon.sdk.config.ResponseModel;
import com.dragon.sdk.config.ResponsePageHelper;
import com.dragon.sdk.config.ResponsePageModel;
import com.dragon.sdk.entity.GamePlayerMsg;
import com.dragon.sdk.service.IGamePlayerMsgService;
import com.dragon.sdk.util.MD5Utils;
import com.dragon.sdk.util.excel.ImportExcel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 前端控制器
 *
 * @author liugh
 * @since 2019-05-09
 */
@RestController
@RequestMapping("/gamePlayerMsg")
public class GamePlayerMsgController {
  private static final Logger logger = LoggerFactory.getLogger(GamePlayerMsgController.class);
  @Resource IGamePlayerMsgService gamePlayerMsgService;

  @GetMapping(value = "/pageList")
  //  @RequiresPermissions(value = {"user:list"})
  // 拥有超级管理员或管理员角色的用户可以访问这个接口,换成角色控制权限,改变请看MyRealm.class
  // @RequiresRoles(value = {Constant.RoleType.SYS_ASMIN_ROLE,Constant.RoleType.ADMIN},logical =
  // Logical.OR)
  @AccessLimit(perSecond = 1, timeOut = 500) // 5秒钟生成一个令牌
  public ResponsePageModel<GamePlayerMsg> findList(
      @RequestParam(name = "pageIndex", defaultValue = "1", required = false) Integer pageIndex,
      @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
      @RequestParam(value = "imei", defaultValue = "", required = false) String imei,
      @RequestParam(value = "deviceId", defaultValue = "", required = false) String deviceId,
      @RequestParam(value = "ip", defaultValue = "", required = false) String ip,
      @RequestParam(value = "createTime", defaultValue = "", required = false)
          String createTimeRange) {
    Wrapper<GamePlayerMsg> wrapper;
    wrapper = new EntityWrapper<GamePlayerMsg>();
    if (StringUtils.isNoneBlank(imei)) {
      wrapper = wrapper.like("imei", imei);
    }
    if (StringUtils.isNoneBlank(ip)) {
      wrapper = wrapper.like("ip", ip);
    }
    if (StringUtils.isNoneBlank(deviceId)) {
      wrapper = wrapper.like("device_id", deviceId);
    }

    if (StringUtils.isNoneBlank(createTimeRange)) {
      String[] arr = createTimeRange.split("-");
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
      try {
        Date start = sdf.parse(arr[0]);
        Date end = sdf.parse(arr[1]);
        wrapper = wrapper.between("create_time", start, end);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
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
        Integer count =
            gamePlayerMsgService.selectCount(
                new EntityWrapper<GamePlayerMsg>()
                    .eq("imei", msg.getImei().trim())
                    .eq("ip", msg.getIp().trim())
                    .eq("device_id", msg.getDeviceId().trim()));
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
      gamePlayerMsgService.insertBatch(gamePlayerMsgs2, gamePlayerMsgs2.size());
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseModel<>(e.getLocalizedMessage(), ResponseModel.FAIL.getCode());
    }

    return ResponseHelper.buildResponseModel("导入数据成功");
  }
}
