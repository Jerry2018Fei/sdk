package com.dragon.sdk.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.clover.common.annotation.AccessLimit;
import com.clover.common.annotation.Log;
import com.clover.common.annotation.Pass;
import com.clover.common.config.web.http.ResponseHelper;
import com.clover.common.config.web.http.ResponseModel;
import com.clover.common.config.web.http.ResponsePageHelper;
import com.clover.common.config.web.http.ResponsePageModel;
import com.clover.common.controller.BaseController;
import com.dragon.sdk.entity.TClickAndroidData;
import com.dragon.sdk.entity.TClickIosData;
import com.dragon.sdk.entity.TouTiaoAdData;
import com.dragon.sdk.service.ITClickAndroidDataService;
import com.dragon.sdk.service.ITClickIosDataService;
import com.dragon.sdk.service.ITouTiaoAdDataService;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 前端控制器
 *
 * @author liugh
 * @since 2019-05-09
 */
@RestController
@RequestMapping("/t_ad")
public class TouTiaoAdDataController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(TouTiaoAdDataController.class);
    @Resource
    ITouTiaoAdDataService touTiaoAdDataService;
    @Resource
    ITClickIosDataService iosDataService;
    @Resource
    ITClickAndroidDataService androidDataService;

    @GetMapping(value = "/pageList")
    @AccessLimit(perSecond = 1, timeOut = 500) // 5秒钟生成一个令牌
    public ResponsePageModel<TouTiaoAdData> findList(
            @RequestParam(name = "page", defaultValue = "1", required = false) Integer pageIndex,
            @RequestParam(name = "limit", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(value = "imei", defaultValue = "", required = false) String imei,
            @RequestParam(value = "idfa", defaultValue = "", required = false) String idfa,
            @RequestParam(value = "ip", defaultValue = "", required = false) String ip,
            @RequestParam(value = "createTime", defaultValue = "", required = false)
                    String createTimeRange,
            @RequestParam(value = "field", defaultValue = "createTime", required = false) String field,
            @RequestParam(value = "order", defaultValue = "desc", required = false) String order) {
        Wrapper<TouTiaoAdData> wrapper;
        wrapper = new EntityWrapper<>();
        if (StringUtils.isNoneBlank(imei)) {
            wrapper = wrapper.like("imei", imei);
        }
        if (StringUtils.isNoneBlank(ip)) {
            wrapper = wrapper.like("ip", ip);
        }

        wrapper =
                createTimeRange((EntityWrapper) wrapper, createTimeRange)
                        .orderBy(underscoreName(field), order.equalsIgnoreCase("asc"));
        return ResponsePageHelper.buildResponseModel(
                touTiaoAdDataService.selectPage(new Page<>(pageIndex, pageSize), wrapper));
    }

    @GetMapping(value = "/receive")
    @Pass
    @Log(action = "Ad", modelName = "Click", description = "接收头条广告信息")
    public ResponseModel<String> addData(
            @RequestParam(name = "ua", required = false, defaultValue = "") String ua,
            @RequestParam(name = "mac", required = false, defaultValue = "") String mac,
            @RequestParam(name = "uuid", required = false, defaultValue = "") String uuid,
            @RequestParam(name = "androidid", required = false, defaultValue = "") String androidid,
            @RequestParam(name = "openudid", required = false, defaultValue = "") String openudid,
            @RequestParam(name = "imei", required = false, defaultValue = "") String imei,
            @RequestParam(name = "idfa", required = false, defaultValue = "") String idfa,
            @RequestParam String adid, @RequestParam String cid, @RequestParam String ip,
            @RequestParam(name = "callback_url") String callbackUrl) {

        logger.info(
                "ua:{}, mac:{}, uuid:{}, androidid:{}, openudid:{}, " + " imei:{},idfa:{}, ip:{}, callbackUrl:{}",
                ua, mac, uuid, androidid, openudid, imei, idfa, ip, callbackUrl);
        if (StringUtils.isEmpty(androidid)) {
            logger.error("androidid 不可以为空");
            return new ResponseModel<String>("androidid 不可以为空", ResponseModel.FAIL.getCode());
        }
        String osTemp = "&os=%s&";
        try {
            if (callbackUrl.contains(String.format(osTemp, 1))) {
                iosDataService.insert(new TClickIosData(mac, ua, androidid, openudid, callbackUrl, ip, idfa, adid, cid));
            } else if (callbackUrl.contains(String.format(osTemp, 0))) {
                androidDataService.insert(new TClickAndroidData(mac, ua, uuid, androidid, openudid, callbackUrl, imei, ip, adid, cid));
            } else {
                logger.error("callbackUrl 异常");
                return new ResponseModel<String>("callbackUrl 不可以为空", ResponseModel.FAIL.getCode());
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseModel<String>(e.getMessage(), ResponseModel.FAIL.getCode());
        }
        return ResponseHelper.buildResponseModel("操作成功");
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
