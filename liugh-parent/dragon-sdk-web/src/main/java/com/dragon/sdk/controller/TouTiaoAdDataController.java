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
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 前端控制器
 *
 * @author liugh
 * @since 2019-05-09
 */
@RestController
@RequestMapping("/touTiaoAdData")
public class TouTiaoAdDataController extends BaseController {
  private static final Logger logger = LoggerFactory.getLogger(TouTiaoAdDataController.class);
  @Resource ITouTiaoAdDataService touTiaoAdDataService;

  @GetMapping(value = "/pageList")
  @AccessLimit(perSecond = 1, timeOut = 500) // 5秒钟生成一个令牌
  public ResponsePageModel<TouTiaoAdData> findList(
      @RequestParam(name = "pageIndex", defaultValue = "1", required = false) Integer pageIndex,
      @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
      @RequestParam(value = "imei", defaultValue = "", required = false) String imei,
      @RequestParam(value = "idfa", defaultValue = "", required = false) String idfa,
      @RequestParam(value = "ip", defaultValue = "", required = false) String ip,
      @RequestParam(value = "createTime", defaultValue = "", required = false)
          String createTimeRange) {
    Wrapper<TouTiaoAdData> wrapper;
    wrapper = new EntityWrapper<>();
    if (StringUtils.isNoneBlank(imei)) {
      wrapper = wrapper.like("imei", imei);
    }
    if (StringUtils.isNoneBlank(ip)) {
      wrapper = wrapper.like("ip", ip);
    }

    wrapper=createTimeRange((EntityWrapper) wrapper,createTimeRange);
    return ResponsePageHelper.buildResponseModel(
        touTiaoAdDataService.selectPage(new Page<>(pageIndex, pageSize), wrapper));
  }

  @GetMapping(value = "/addData")
  @Pass
  public ResponseModel<String> addData(
      @RequestParam(name = "ua", required = false, defaultValue = "") String ua,
      @RequestParam(name = "mac", required = false, defaultValue = "") String mac,
      @RequestParam(name = "uuid", required = false, defaultValue = "") String uuid,
      @RequestParam(name = "androidid", required = false, defaultValue = "") String androidid,
      @RequestParam(name = "openudid", required = false, defaultValue = "") String openudid,
      @RequestParam(name = "os", required = false, defaultValue = "") String os,
      @RequestParam(name = "imei", required = false, defaultValue = "") String imei,
      @RequestParam(name = "idfa", required = false, defaultValue = "") String idfa,
      @RequestParam(name = "ip", required = false, defaultValue = "") String ip,
      @RequestParam(name = "callback_url", required = false, defaultValue = "")
          String callbackUrl) {

    logger.info(
        "ua:{}, mac:{}, uuid:{}, androidid:{}, openudid:{}, "
            + "os:{}, imei:{},idfa:{}, ip:{}, callbackUrl:{}",
        ua,
        mac,
        uuid,
        androidid,
        openudid,
        os,
        imei,
        idfa,
        ip,
        callbackUrl);
    boolean bool =
        checkParam(ua)
            && checkParam(mac)
            && checkParam(uuid)
            && checkParam(androidid)
            && checkParam(openudid)
            && checkParam(os)
            && checkParam(imei)
            && checkParam(idfa)
            && checkParam(ip)
            && checkParam(callbackUrl);
    if (!bool) {
      return new ResponseModel<>("接收到无效信息", ResponseModel.FAIL.getCode());
    }
    if (!StringUtils.isNumeric(os)) {
      os = "";
    }

    int count =
        touTiaoAdDataService.selectCount(
            new EntityWrapper<TouTiaoAdData>()
                .eq("ua", ua)
                .eq("mac", mac)
                .eq("uuid", uuid)
                .eq("androidid", androidid)
                .eq("openudid", openudid)
                .eq("os", Integer.getInteger(os))
                .eq("imei", imei)
                .eq("idfa", idfa)
                .eq("ip", ip));
    if (count == 0) {
      TouTiaoAdData data =
          new TouTiaoAdData(
              mac, ua, uuid, androidid, openudid, Integer.getInteger(os), callbackUrl, imei, ip,idfa);
      touTiaoAdDataService.insert(data);
      return ResponseHelper.buildResponseModel("操作成功");
    } else {
      return new ResponseModel<>("接收数据异常,重复数据", ResponseModel.FAIL.getCode());
    }
  }

  private boolean checkParam(String msg) {
    boolean bool = true;
    String emptyFlag = "__";
    if (StringUtils.isNoneBlank(msg)
        && msg.trim().startsWith(emptyFlag)
        && msg.trim().endsWith(emptyFlag)) {
      bool = false;
    }
    if(StringUtils.isBlank(msg)){
      return true;
    }
    return bool;
  }

  @DeleteMapping(value = "/remove")
  public ResponseModel<String> remove(@RequestBody List<Long> ids) {
    try {
      touTiaoAdDataService.deleteBatchIds(ids);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseModel<>(e.getLocalizedMessage(), ResponseModel.FAIL.getCode());
    }

    return ResponseHelper.buildResponseModel("删除数据成功");
  }
}
