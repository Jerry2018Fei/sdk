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
import com.dragon.sdk.entity.TouTiaoAdData;
import com.dragon.sdk.service.ITouTiaoAdDataService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 前端控制器
 *
 * @author liugh
 * @since 2019-05-09
 */
@RestController
@RequestMapping("/touTiaoAdData")
public class TouTiaoAdDataController {
  private static final Logger logger = LoggerFactory.getLogger(TouTiaoAdDataController.class);
  @Resource ITouTiaoAdDataService touTiaoAdDataService;

  @GetMapping(value = "/pageList")
  @AccessLimit(perSecond = 1, timeOut = 500) // 5秒钟生成一个令牌
  public ResponsePageModel<TouTiaoAdData> findList(
      @RequestParam(name = "pageIndex", defaultValue = "1", required = false) Integer pageIndex,
      @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
      @RequestParam(value = "imei", defaultValue = "", required = false) String imei,
      @RequestParam(value = "ip", defaultValue = "", required = false) String ip,
      @RequestParam(value = "createTime", defaultValue = "", required = false)
          String createTimeRange) {
    Wrapper<TouTiaoAdData> wrapper;
    wrapper = new EntityWrapper<TouTiaoAdData>();
    if (StringUtils.isNoneBlank(imei)) {
      wrapper = wrapper.like("imei", imei);
    }
    if (StringUtils.isNoneBlank(ip)) {
      wrapper = wrapper.like("ip", ip);
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
        touTiaoAdDataService.selectPage(new Page<>(pageIndex, pageSize), wrapper));
  }

  @GetMapping(value = "/addData")
  @Pass
  public ResponseModel<String> addData(
      String ua,
      String mac,
      String uuid,
      String androidid,
      String openudid,
      Integer os,
      String imei,
      String ip,
      @RequestParam(name = "callback_url", required = false, defaultValue = "")
          String callbackUrl) {

    logger.info(ua, mac, uuid, androidid, openudid, os, imei, ip, callbackUrl);
    Integer count =
        touTiaoAdDataService.selectCount(
            new EntityWrapper<TouTiaoAdData>()
                .eq("ua", ua)
                .eq("mac", mac)
                .eq("uuid", uuid)
                .eq("androidid", androidid)
                .eq("openudid", openudid)
                .eq("os", os)
                .eq("imei", imei)
                .eq("ip", ip));
    if (count > 0) {
      TouTiaoAdData data =
          new TouTiaoAdData(mac, ua, uuid, androidid, openudid, os, callbackUrl, imei, ip);
      touTiaoAdDataService.insert(data);
      return ResponseHelper.buildResponseModel("操作成功");
    } else {
      return new ResponseModel<>("接收数据异常", ResponseModel.FAIL.getCode());
    }
  }
}
