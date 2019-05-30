package com.dragon.sdk.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.dragon.sdk.annotation.AccessLimit;
import com.dragon.sdk.annotation.Pass;
import com.dragon.sdk.config.ResponseHelper;
import com.dragon.sdk.config.ResponseModel;
import com.dragon.sdk.config.ResponsePageHelper;
import com.dragon.sdk.config.ResponsePageModel;
import com.dragon.sdk.entity.GameCallData;
import com.dragon.sdk.service.IGameCallDataService;
import com.dragon.sdk.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 前端控制器
 *
 * @author liugh
 * @since 2019-05-09
 */
@RestController
@RequestMapping("/gameCallData")
public class GameCallDataController extends BaseController {
  private static final Logger logger = LoggerFactory.getLogger(GameCallDataController.class);
  @Resource IGameCallDataService gameCallDataService;

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
   * @return com.dragon.sdk.config.ResponsePageModel<com.dragon.sdk.entity.GamePlayerMsg> 数据
   */
  public ResponsePageModel<GameCallData> findList(
      @RequestParam(name = "page", defaultValue = "1", required = false) Integer pageIndex,
      @RequestParam(name = "limit", defaultValue = "10", required = false) Integer pageSize) {
    Wrapper<GameCallData> wrapper;
    wrapper = new EntityWrapper<>();

    return ResponsePageHelper.buildResponseModel(
        gameCallDataService.selectPage(new Page<>(pageIndex, pageSize), wrapper));
  }

  @GetMapping(value = "/receive")
  @Pass
  public ResponseModel<String> receive(
      @RequestParam(name = "type", required = true) Integer type,
      @RequestParam(name = "mac", defaultValue = "", required = false) String mac,
      @RequestParam(name = "imei", defaultValue = "", required = false) String imei,
      @RequestParam(name = "idfa", defaultValue = "", required = false) String idfa,
      @RequestParam(name = "androidid", defaultValue = "", required = false) String androidid,
      @RequestParam(name = "eventType", required = true) Integer eventType) {

    try {
      logger.info("type:{},mac:{},imei:{},idfa:{},eventType:{}", type, mac, imei, idfa, eventType);
      if (StringUtils.isBlank(imei)
          && StringUtils.isBlank(idfa)
          && StringUtils.isBlank(mac)
          && StringUtils.isBlank(androidid)) {
        throw new Exception("设备参数不可以为空");
      }
      Wrapper<GameCallData> wrapper = new EntityWrapper<>();
      boolean bool;
      if (StringUtils.isNotBlank(mac)) {
        bool = true;
        wrapper = wrapper.eq("mac", mac);
      }
      if (StringUtils.isNotBlank(imei)) {
        bool = true;
        if (bool) {
          wrapper = wrapper.or();
        }
        wrapper = wrapper.eq("imei", imei);
      }
      if (StringUtils.isNotBlank(idfa)) {
        bool = true;
        if (bool) {
          wrapper = wrapper.or();
        }
        wrapper = wrapper.eq("idfa", idfa);
      }
      if (StringUtils.isNotBlank(androidid)) {
        bool = true;
        if (bool) {
          wrapper = wrapper.or();
        }
        wrapper = wrapper.eq("androidid", androidid);
      }
      wrapper = wrapper.last("limit 1");
      GameCallData gameCallData = gameCallDataService.selectOne(wrapper);
      if (gameCallData == null) {
        gameCallData = new GameCallData(type, idfa, imei, mac,androidid, eventType);
        gameCallDataService.insert(gameCallData);
      } else {
        if (gameCallData.getEventType() < eventType) {
          gameCallData.setEventType(eventType);

        }
        if(StringUtils.isNotBlank(mac)){
          gameCallData.setMac(mac);
        }
        if(StringUtils.isNotBlank(imei)){
          gameCallData.setImei(imei);
        }
        if(StringUtils.isNotBlank(idfa)){
          gameCallData.setIdfa(idfa);
        }
        if(StringUtils.isNotBlank(androidid)){
          gameCallData.setAndroidid(androidid);
        }
        gameCallData.setUpdateTime(new Date());
        gameCallDataService.updateById(gameCallData);
      }
      return ResponseHelper.buildResponseModel("接收数据成功");
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseModel<>(e.getLocalizedMessage(), ResponseModel.FAIL.getCode());
    }
  }

  @DeleteMapping(value = "/remove")
  public ResponseModel<String> remove(@RequestBody List<Long> ids) {
    try {
      gameCallDataService.deleteBatchIds(ids);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseModel<>(e.getLocalizedMessage(), ResponseModel.FAIL.getCode());
    }

    return ResponseHelper.buildResponseModel("删除数据成功");
  }
}
