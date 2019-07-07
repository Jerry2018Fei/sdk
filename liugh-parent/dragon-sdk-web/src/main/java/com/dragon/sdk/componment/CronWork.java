package com.dragon.sdk.componment;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.clover.common.util.MD5Utils;
import com.dragon.sdk.entity.GameCallData;
import com.dragon.sdk.entity.TouTiaoAdData;
import com.dragon.sdk.po.AdCallbackResult;
import com.dragon.sdk.service.IGameCallDataService;
import com.dragon.sdk.service.IGamePlayerMsgService;
import com.dragon.sdk.service.ITouTiaoAdDataService;
import lombok.NonNull;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
@Configuration // 1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling
public class CronWork {
  private static final Logger log = LoggerFactory.getLogger(CronWork.class);
  @Resource private IGamePlayerMsgService gamePlayerMsgService;
  @Resource private ITouTiaoAdDataService touTiaoAdDataService;
  @Resource private IGameCallDataService gameCallDataService;
  @Resource private RestTemplate restTemplate;
  @Resource private Executor taskAsyncPool;

  //  @PostConstruct
  public void init() {
    deleteToutiaoData();
  }

  //  @Scheduled(cron = "0 0 0 ? * ? ")
  private void deleteToutiaoData() {
    log.info("清理超过七天未匹配数据");
    touTiaoAdDataService.delete(
        new EntityWrapper<TouTiaoAdData>()
            .le("create_time", DateUtils.addDays(new Date(), -7))
            .eq("is_send", -1));

    log.info("清理异常客户端数据");

    gameCallDataService.delete(
        new EntityWrapper<GameCallData>().like("mac", "00:00:00").or().like("idfa", "00000000"));

    taskAsyncPool.execute(
        () -> {
          log.info("自动匹配客户端数据");

          List<GameCallData> dataList = gameCallDataService.selectList(new EntityWrapper<>());
          if (!CollectionUtils.isEmpty(dataList)) {
            for (GameCallData d : dataList) {
              try {
                checkGameCallData(d);
              } catch (Exception e) {
                e.printStackTrace();
              }
            }
          }
        });
  }

  public void checkGameCallData(@NonNull GameCallData gameCallData) {
    if (StringUtils.isEmpty(gameCallData.getAndroidid())
        && StringUtils.isEmpty(gameCallData.getImei())
        && StringUtils.isEmpty(gameCallData.getIdfa())) {
      return;
    }
    if (!StringUtils.isEmpty(gameCallData.getImei())) {
      String imei = gameCallData.getImei();
      try {
        imei = getMD5Sum(imei(imei));
        gameCallData.setImei(imei);
      } catch (Exception e) {
        log.error("imei加密异常：{}",e.getLocalizedMessage());
      }

    }

    if (!StringUtils.isEmpty(gameCallData.getMac())) {
      String mac = gameCallData.getMac();
      try {
        mac = mac.replaceAll(":", "").toUpperCase();
        mac = getMD5Sum(mac);
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
    boolean b =
        !StringUtils.isEmpty(gameCallData.getIdfa())
            || (!StringUtils.isEmpty(gameCallData.getImei())
                || !StringUtils.isEmpty(gameCallData.getAndroidid()));
    if(!b){
      return;
    }
    List<TouTiaoAdData> dataList = touTiaoAdDataService.selectByGameCallData(gameCallData);
    log.info(dataList.size()+"");
    if (!CollectionUtils.isEmpty(dataList)) {
      for (TouTiaoAdData data : dataList) {
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
          // -1 0 1 2

          Random random = new Random();
          taskAsyncPool.execute(
              new Runnable() {
                @Override
                public void run() {
                  for (Integer t : arr) {
                    try {
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
                      log.info("发送请求：{}", data.getCallbackUrl() + "&event_type=" + t);

                      AdCallbackResult result =
                          restTemplate.getForObject(
                              data.getCallbackUrl() + "&event_type=" + t, AdCallbackResult.class);
                      log.info(JSONObject.toJSONString(result));

                      if (result != null && result.getCode() == 0) {
                        log.info("回调{}成功", data.getCallbackUrl() + "&event_type=" + t);
                        data.setIsSend(t);
                        touTiaoAdDataService.updateAllColumnById(data);
                      }
                    } catch (InterruptedException e) {
                      e.printStackTrace();
                    }
                  }
                }
              });
        }
      }
    }
  }

  public static void main(String[] args) throws Exception {
    System.out.println(getMD5Sum(imei("868953042454828")));
    System.out.println(md5sum("99000758283350"));
    //    System.out.println(md5sum(imei("99000758283350")));
    System.out.println(MD5Utils.string2MD5("7763c6a6486d5ce8"));
    //    System.out.println(getMD5Sum(imei("869095036103950")));
  }

  public static String getMD5Sum(String need2Encode) throws NoSuchAlgorithmException {
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

  public static String imei(String msg) {
    if (StringUtils.isEmpty(msg)) {
      return "";
    }
    if (msg.length() != 14) {
      return msg;
    }
    //     msg="35566778898256";
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

  public static String md5sum(String msg) {
    String command = "echo -n '" + msg + "'|md5sum|cut -d ' ' -f1";
    System.out.println(command);
    try {
      Process process = Runtime.getRuntime().exec(command);
      BufferedInputStream bis = new BufferedInputStream(process.getInputStream());
      BufferedReader br = new BufferedReader(new InputStreamReader(bis));
      String line;
      while ((line = br.readLine()) != null) {
        System.out.println(line);
        return line;
      }

      process.waitFor();
      if (process.exitValue() != 0) {
        System.out.println("error!");
      }

      bis.close();
      br.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return "";
  }
}
