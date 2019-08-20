package com.dragon.sdk.componment;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.clover.common.entity.OperationLog;
import com.clover.common.service.IOperationLogService;
import com.clover.common.util.MD5Utils;
import com.dragon.sdk.entity.*;
import com.dragon.sdk.enums.ClientTypeEnum;
import com.dragon.sdk.po.AdCallbackResult;
import com.dragon.sdk.service.*;
import lombok.NonNull;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;

/**
 * @author dingpengfei
 * @date 2019-05-19 18:00
 */
@Component
@Configuration
@EnableScheduling
public class CronWork {
    private static final Logger log = LoggerFactory.getLogger(CronWork.class);
    @Resource
    private ITouTiaoAdDataService touTiaoAdDataService;
    @Resource
    private ITClickAndroidDataService clickAndroidDataService;
    @Resource
    private ITClickIosDataService clickIosDataService;
    @Resource
    private IGameCallDataService gameCallDataService;
    @Resource
    private IThirdpartyGamePushAndroidDataService thirdpartyGamePushAndroidDataService;
    @Resource
    private IThirdpartyGamePushIosDataService thirdpartyGamePushIosDataService;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private Executor taskAsyncPool;
    @Resource
    private IOperationLogService logService;

    @PostConstruct
    public void init() {
        convertClickData();
    }

    private void convertClickData() {
        List<TouTiaoAdData> list = touTiaoAdDataService.selectList(new EntityWrapper<>());
        if (!CollectionUtils.isEmpty(list)) {
            String osTemp = "&os=%s&";
            for (TouTiaoAdData data : list) {
                try {
                    if (data.getCallbackUrl().contains(String.format(osTemp, 1))) {
                        TClickIosData iosData = new TClickIosData(data.getMac(), data.getUa(), data.getAndroidid(), data.getOpenudid(), data.getCallbackUrl(), data.getIp(), data.getIdfa(), data.getAdid(), data.getCid());
                        iosData.setCreateTime(data.getCreateTime());
                        iosData.setUpdateTime(data.getCreateTime());
                        clickIosDataService.insert(iosData);
                    } else if (data.getCallbackUrl().contains(String.format(osTemp, 0))) {
                        TClickAndroidData androidData = new TClickAndroidData(data.getMac(), data.getUa(), data.getUuid(), data.getAndroidid(), data.getOpenudid(), data.getCallbackUrl(), data.getImei(), data.getIp(), data.getAdid(), data.getCid());
                        androidData.setCreateTime(data.getCreateTime());
                        androidData.setUpdateTime(data.getCreateTime());
                        clickAndroidDataService.insert(androidData);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            touTiaoAdDataService.delete(new EntityWrapper<>());
        }

        List<GameCallData> list1 = gameCallDataService.selectList(new EntityWrapper<>());
        if (!CollectionUtils.isEmpty(list1)) {
            for (GameCallData data : list1) {
                try {
                    switch (data.getType()) {
                        case 1:
                            ThirdpartyGamePushAndroidData androidData = new ThirdpartyGamePushAndroidData(data.getImei(), data.getMac(), data.getAndroidid(), data.getEventType());
                            androidData.setCreateTime(data.getUpdateTime());
                            androidData.setUpdateTime(data.getUpdateTime());
                            thirdpartyGamePushAndroidDataService.insert(androidData);
                            break;
                        case 2:
                            ThirdpartyGamePushIosData iosData = new ThirdpartyGamePushIosData(data.getIdfa(), data.getMac(), data.getEventType());
                            iosData.setCreateTime(data.getUpdateTime());
                            iosData.setUpdateTime(data.getUpdateTime());
                            thirdpartyGamePushIosDataService.insert(iosData);
                            break;
                        default:
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            gameCallDataService.delete(new EntityWrapper<>());
        }
    }




    public void patchAdData(@NonNull ThirdpartyGamePushIosData gameCallData) {
        if (StringUtils.isEmpty(gameCallData.getIdfa())) {
            return;
        }
        List<TClickIosData> dataList = clickIosDataService.selectByDevice(gameCallData.getIdfa(),gameCallData.getEventType()-1);
        if (!CollectionUtils.isEmpty(dataList)) {
            for (TClickIosData data : dataList) {
                if (data != null) {
                    List<Integer> arr = new ArrayList<>(3);

                    Integer rType = gameCallData.getEventType();
                    // 1 2 3
                    Integer tType = data.getIsSend();
                    for (int i = tType + 1; i < rType; i++) {
                        arr.add(i);
                    }
                    log.info(
                            "客户端状态:{} 头条信息状态:{} 待发送头条信息状态：{}",
                            gameCallData.getEventType(),
                            data.getIsSend(),
                            JSONObject.toJSONString(arr));

                    Random random = new Random();
                    taskAsyncPool.execute(
                            () -> {
                                for (Integer t : arr) {
                                    try {
                                        s(random, t);

                                        log.info("发送请求：{}", data.getCallbackUrl() + "&event_type=" + t);

                                        AdCallbackResult result =
                                                restTemplate.getForObject(
                                                        data.getCallbackUrl() + "&event_type=" + t, AdCallbackResult.class);
                                        log.info(JSONObject.toJSONString(result));

                                        if (result != null && result.getCode() == 0) {
                                            log.info("回调{}成功", data.getCallbackUrl() + "&event_type=" + t);
                                            data.setIsSend(t);
                                            clickIosDataService.updateAllColumnById(data);
                                            try {
                                                OperationLog log = new OperationLog();
                                                log.setModelName("callback_toutiao");
                                                log.setAction("callback_toutiao_ios");
                                                log.setActionArgs("idfa:" + data.getIdfa() + "&" + data.getCallbackUrl() + "&event_type=" + t);
                                                log.setResult(JSONObject.toJSONString(result));
                                                log.setCreateTime(new Date());
                                                logService.insert(log);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                }
            }
        }
    }

    public void patchAdData(@NonNull ThirdpartyGamePushAndroidData gameCallData) {
        if (StringUtils.isEmpty(gameCallData.getImei()) && StringUtils.isEmpty(gameCallData.getAndroidid())) {
            return;
        }

        handlerAdSecurity(gameCallData);
        List<TClickAndroidData> dataList = clickAndroidDataService.selectByDevice(gameCallData.getAndroidid(), gameCallData.getImei(),gameCallData.getEventType()-1);
        if (!CollectionUtils.isEmpty(dataList)) {
            for (TClickAndroidData data : dataList) {
                if (data != null) {
                    List<Integer> arr = new ArrayList<>(3);

                    Integer rType = gameCallData.getEventType();
                    // 1 2 3
                    Integer tType = data.getIsSend();
                    for (int i = tType + 1; i < rType; i++) {
                        arr.add(i);
                    }
                    log.info(
                            "客户端状态:{} 头条信息状态:{} 待发送头条信息状态：{}",
                            gameCallData.getEventType(),
                            data.getIsSend(),
                            JSONObject.toJSONString(arr));

                    Random random = new Random();
                    taskAsyncPool.execute(
                            () -> {
                                for (Integer t : arr) {
                                    try {
                                        s(random, t);

                                        log.info("发送请求：{}", data.getCallbackUrl() + "&event_type=" + t);

                                        AdCallbackResult result =
                                                restTemplate.getForObject(
                                                        data.getCallbackUrl() + "&event_type=" + t, AdCallbackResult.class);
                                        log.info(JSONObject.toJSONString(result));

                                        if (result != null && result.getCode() == 0) {
                                            log.info("回调{}成功", data.getCallbackUrl() + "&event_type=" + t);
                                            data.setIsSend(t);
                                            clickAndroidDataService.updateAllColumnById(data);

                                            try {
                                                OperationLog log = new OperationLog();
                                                log.setModelName("callback_toutiao");

                                                log.setAction("callback_toutiao_android");
                                                log.setActionArgs("androidid:" + data.getAndroidid() + "&" + "imei:" + data.getImei() + "&" + data.getCallbackUrl() + "&event_type=" + t);
                                                log.setResult(JSONObject.toJSONString(result));
                                                log.setCreateTime(new Date());
                                                logService.insert(log);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                }
            }
        }
    }


    private void s(Random random, Integer t) throws InterruptedException {
        switch (t) {
            case 0:
                Thread.sleep(1);
                break;
            case 1:
                Thread.sleep(1000 * (random.nextInt(5) + 5));

                break;
            case 2:
                Thread.sleep(1000 * (random.nextInt(30) + 60));

                break;
            default:
        }
    }


    private void handlerAdSecurity(@NonNull ThirdpartyGamePushAndroidData gameCallData) {
        if (!StringUtils.isEmpty(gameCallData.getImei())) {
            String imei = gameCallData.getImei();
            try {
                imei = getmd5Sum(imei(imei));
                gameCallData.setImei(imei);
            } catch (Exception e) {
                log.error("imei加密异常：{}", e.getLocalizedMessage());
            }
        }

        if (!StringUtils.isEmpty(gameCallData.getMac())) {
            String mac = gameCallData.getMac();
            try {
                mac = mac.replaceAll(":", "").toUpperCase();
                mac = getmd5Sum(mac);
            } catch (Exception e) {
                e.printStackTrace();
            }
            gameCallData.setMac(mac);
        }

        if (!StringUtils.isEmpty(gameCallData.getAndroidid())) {
            String androidid = gameCallData.getAndroidid();
            try {
                androidid = MD5Utils.string2MD5(androidid).toLowerCase();
            } catch (Exception e) {
                e.printStackTrace();
            }
            gameCallData.setAndroidid(androidid);
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println(getmd5Sum(imei("868953042454828")));
        System.out.println(MD5Utils.string2MD5("7763c6a6486d5ce8"));
    }

    public static String getmd5Sum(String need2Encode) throws NoSuchAlgorithmException {
        byte[] buf = need2Encode.getBytes();
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(buf);
        byte[] tmp = md5.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : tmp) {
            if (b >= 0 && b < 16) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(b & 0xff));
        }
        return sb.toString();
    }

    /**
     * 转换imei 补全第15位imei校验值
     *
     * @param msg imei
     * @return imei
     */
    public static String imei(String msg) {
        if (StringUtils.isEmpty(msg)) {
            return "";
        }
        if (msg.length() != 14) {
            return msg;
        }
        char[] imeiChar = msg.toCharArray();
        int resultInt = 0;
        for (int i = 0; i < imeiChar.length; i++) {
            int a = Integer.parseInt(String.valueOf(imeiChar[i]));
            i++;
            final int temp = Integer.parseInt(String.valueOf(imeiChar[i])) * 2;
            final int b = temp < 10 ? temp : temp - 9;
            resultInt += a + b;
        }
        resultInt %= 10;
        resultInt = resultInt == 0 ? 0 : 10 - resultInt;
        return msg + resultInt;
    }
}
