package com.dragon.sdk.componment;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.clover.common.util.MD5Utils;
import com.dragon.sdk.entity.GameCallData;
import com.dragon.sdk.entity.GamePlayerMsg;
import com.dragon.sdk.entity.TouTiaoAdData;
import com.dragon.sdk.po.AdCallbackResult;
import com.dragon.sdk.service.IGamePlayerMsgService;
import com.dragon.sdk.service.ITouTiaoAdDataService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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
  @Resource private RestTemplate restTemplate;
  @Resource private Executor taskAsyncPool;

  public void checkGameCallData(@NonNull GameCallData gameCallData) {
    if (!StringUtils.isEmpty(gameCallData.getImei())) {
      String imei = gameCallData.getImei();
      try {
        imei = getMD5Sum(imei(imei));
      } catch (Exception e) {
        e.printStackTrace();
      }
      gameCallData.setImei(imei);
    }

    if (!StringUtils.isEmpty(gameCallData.getMac())) {
      String mac = gameCallData.getMac();
      try {
        mac = mac.replaceAll(":", "");
        mac = getMD5Sum(mac);
      } catch (Exception e) {
        e.printStackTrace();
      }
      gameCallData.setMac(mac);
    }

    if (!StringUtils.isEmpty(gameCallData.getAndroidid())) {
      String androidid = gameCallData.getAndroidid();
      try {
        androidid = getMD5Sum(androidid);
      } catch (Exception e) {
        e.printStackTrace();
      }
      gameCallData.setAndroidid(androidid);
    }
    List<TouTiaoAdData> dataList = touTiaoAdDataService.selectByGameCallData(gameCallData);
    if (!CollectionUtils.isEmpty(dataList)) {
      for (TouTiaoAdData data : dataList) {
        if (data != null) {
          Integer type = 0;
          switch (gameCallData.getType()) {
            case 1:
              type = 0;
              break;
            case 2:
              type = 1;
              break;
            case 3:
              type = 2;
              break;
            default:
          }
          List<Integer> arr = new ArrayList<>(3);
          if (type == 0) {
            arr.add(0);
          } else if (type == 1) {
            if (data.getIsSend() == -1) {
              arr.add(0);
            }
            arr.add(1);
          } else {
            switch (data.getIsSend()) {
              case -1:
                arr.add(0);
                arr.add(1);
                arr.add(2);
                break;
              case 0:
                arr.add(1);
                arr.add(2);
                break;
              case 1:
                arr.add(2);
                break;
              default:
            }
          }
          Random random = new Random();
          taskAsyncPool.execute(
              new Runnable() {
                @Override
                public void run() {
                  for (Integer t : arr) {
                    try {

                      Thread.sleep(1000 * (random.nextInt(5) + 1));
                      AdCallbackResult result =
                          restTemplate.getForObject(
                              data.getCallbackUrl() + "&event_type=" + t, AdCallbackResult.class);
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

  @Scheduled(cron = "0 2/10 * * * ?")
  public void cron() {
    //    List<GamePlayerMsg> msgs =
    //        gamePlayerMsgService.selectList(
    //            new EntityWrapper<GamePlayerMsg>()
    //                .eq("delete_flag", 0)
    //                .eq("status", 0)
    //                .isNotNull("ip")
    //                .orderBy("createTime", false));
    //    if (!CollectionUtils.isEmpty(msgs)) {
    //      int max = 50;
    //      max = msgs.size() <= max ? msgs.size() : max;
    //      int per = msgs.size() / max;
    //      List<Task> tasks = new ArrayList<>(max);
    //      for (int i = 0; i < max; i++) {
    //        int start = i * per;
    //        int end = i == max - 1 ? max : per * (i + 1);
    //        Task task =
    //            new Task(
    //                msgs.subList(start, end), gamePlayerMsgService, touTiaoAdDataService,
    // restTemplate);
    //        tasks.add(task);
    //        taskAsyncPool.execute(task);
    //      }
    //      if (!CollectionUtils.isEmpty(tasks)) {
    //
    //        while (true) {
    //          int count = 0;
    //          for (Task task : tasks) {
    //            if (task.finish) {
    //              ++count;
    //            }
    //          }
    //          if (count == tasks.size()) {
    //            break;
    //          }
    //        }
    //        gamePlayerMsgService.delete(new EntityWrapper<GamePlayerMsg>().eq("status", 0));
    //      }
    //    }
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Task implements Runnable {

    private List<GamePlayerMsg> msgs;
    private IGamePlayerMsgService gamePlayerMsgService;
    private ITouTiaoAdDataService touTiaoAdDataService;
    private RestTemplate restTemplate;
    private boolean finish = false;

    public Task(
        List<GamePlayerMsg> msgs,
        IGamePlayerMsgService gamePlayerMsgService,
        ITouTiaoAdDataService touTiaoAdDataService,
        RestTemplate restTemplate) {
      this.msgs = msgs;
      this.gamePlayerMsgService = gamePlayerMsgService;
      this.touTiaoAdDataService = touTiaoAdDataService;
      this.restTemplate = restTemplate;
    }

    @Override
    public void run() {
      finish = false;
      for (GamePlayerMsg msg : msgs) {
        TouTiaoAdData data =
            touTiaoAdDataService.selectOne(
                new EntityWrapper<TouTiaoAdData>()
                    .eq("delete_flag", 0)
                    .eq("is_send", 0)
                    .eq("ip", msg.getIp())
                    .isNotNull("imei")
                    .orderBy("createTime", false)
                    .last("limit 1"));
        try {
          if (data != null) {
            log.info(JSONObject.toJSONString(data));
            AdCallbackResult result =
                restTemplate.getForObject(
                    data.getCallbackUrl() + "&event_type=0", AdCallbackResult.class);
            if (result != null && result.getCode() == 0) {
              log.info("回调{}成功", data.getCallbackUrl() + "&event_type=0");
              msg.setStatus(1);
              gamePlayerMsgService.updateAllColumnById(msg);
              data.setIsSend(1);
              touTiaoAdDataService.updateAllColumnById(data);
            }
          }

        } catch (RestClientException e) {
          e.printStackTrace();
        }
      }

      finish = true;
    }
  }

  public static void main(String[] args) throws Exception {
    System.out.println(MD5Utils.md5Digest("86634103984145"));
    System.out.println(MD5Utils.string2MD5("86634103984145"));
    System.out.println("7e9c923a6236b81c56700d204cb329e3".length());
    System.out.println(getMD5Sum("866341039841458"));
    System.out.println(getMD5Sum("5c79afd01925cb52"));
    System.out.println(imei("86634103984145"));
    System.out.println(imei("86634103984145"));
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
    if (msg.length() == 15) {
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
}
