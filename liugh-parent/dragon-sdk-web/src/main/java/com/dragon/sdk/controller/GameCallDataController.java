package com.dragon.sdk.controller;

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
import com.dragon.sdk.Constants;
import com.dragon.sdk.EnumUtils;
import com.dragon.sdk.componment.CronWork;
import com.dragon.sdk.entity.GameCallData;
import com.dragon.sdk.entity.ThirdpartyGamePushAndroidData;
import com.dragon.sdk.entity.ThirdpartyGamePushIosData;
import com.dragon.sdk.enums.ClientEventTypeEnum;
import com.dragon.sdk.enums.ClientTypeEnum;
import com.dragon.sdk.service.IGameCallDataService;
import com.dragon.sdk.service.IThirdpartyGamePushAndroidDataService;
import com.dragon.sdk.service.IThirdpartyGamePushIosDataService;
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
    @Resource
    IGameCallDataService gameCallDataService;
    @Resource
    IThirdpartyGamePushAndroidDataService androidDataService;
    @Resource
    IThirdpartyGamePushIosDataService iosDataService;
    @Resource
    CronWork cronWork;

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
    public ResponsePageModel<GameCallData> findList(
            @RequestParam(name = "page", defaultValue = "1", required = false) Integer pageIndex,
            @RequestParam(name = "limit", defaultValue = "10", required = false) Integer pageSize) {
        Wrapper<GameCallData> wrapper;
        wrapper = new EntityWrapper<>();

        return ResponsePageHelper.buildResponseModel(
                gameCallDataService.selectPage(new Page<>(pageIndex, pageSize), wrapper));
    }

    @GetMapping(value = "/receive")
    @Log(action = "Client", modelName = "ClientReceive", description = "接收客户端信息")
    @Pass
    public ResponseModel<String> receive(
            @RequestParam Integer type,
            @RequestParam(name = "mac", defaultValue = "", required = false) String mac,
            @RequestParam(name = "imei", defaultValue = "", required = false) String imei,
            @RequestParam(name = "idfa", defaultValue = "", required = false) String idfa,
            @RequestParam(name = "andoridid", defaultValue = "", required = false) String androidid,
            @RequestParam Integer eventType) {

        try {
            logger.info("type:{},mac:{},imei:{},idfa:{},eventType:{}", type, mac, imei, idfa, eventType);
            ClientTypeEnum typeEnum = EnumUtils.getClientTypeEnumByValue(type);
            ClientEventTypeEnum eventTypeEnum = EnumUtils.getClientEventTypeEnumByValue(eventType);
            if (typeEnum == null) {
                logger.error("客户端参数 type 异常");
                throw new Exception("客户端参数 type 异常");
            }
            if (eventTypeEnum == null) {
                logger.error("客户端参数 eventType 异常");
                throw new Exception("客户端参数 eventType 异常");
            }
            switch (typeEnum) {
                case ANDROID:
                    if (StringUtils.isEmpty(imei) || StringUtils.isEmpty(androidid)) {
                        logger.error("type=1时，imei，andoridid不能全部为空");
                        throw new Exception("type=1时，imei，andoridid不能全部为空");
                    }
                    ThirdpartyGamePushAndroidData d = new ThirdpartyGamePushAndroidData(imei, mac, androidid, eventType);
                    androidDataService.insert(d);
                    d=androidDataService.selectById(d.getId());
                    System.out.println();
                    cronWork.patchAdData(d);
                    break;
                case IOS:
                    if (StringUtils.isEmpty(idfa)) {
                        logger.error("type=2时，idfa 不可以为空");
                        throw new Exception("type=2时，idfa 不可以为空");
                    }
                    if (idfa.trim().equalsIgnoreCase(Constants.EMPTY_I_D_F_A)) {
                        logger.error("无效idfa参数");
                        throw new Exception("无效idfa参数");
                    }
                    ThirdpartyGamePushIosData d2 = new ThirdpartyGamePushIosData(idfa, mac, eventType);
                    iosDataService.insert(d2);
                    d2=iosDataService.selectById(d2.getId());
                    System.out.println();
                    cronWork.patchAdData(d2);

                    break;
                default:
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
