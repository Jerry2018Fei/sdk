package com.dragon.sdk.componment;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.dragon.sdk.entity.GamePlayerMsg;
import com.dragon.sdk.entity.TouTiaoAdData;
import com.dragon.sdk.po.AdCallbackResult;
import com.dragon.sdk.service.IGamePlayerMsgService;
import com.dragon.sdk.service.ITouTiaoAdDataService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
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

  @Scheduled(cron = "0 2/10 * * * ?")
  public void hello() {
    List<GamePlayerMsg> msgs =
        gamePlayerMsgService.selectList(
            new EntityWrapper<GamePlayerMsg>()
                .eq("delete_flag", 0)
                .eq("status", 0)
                .isNotNull("ip")
                .orderBy("createTime", false));
    if (!CollectionUtils.isEmpty(msgs)) {
      int max = 50;
      max = msgs.size() <= max ? msgs.size() : max;
      int per = msgs.size() / max;
      List<Task> tasks = new ArrayList<>(max);
      for (int i = 0; i < max; i++) {
        int start = i * per;
        int end = i == max - 1 ? max : per * (i + 1);
        Task task =
            new Task(
                msgs.subList(start, end), gamePlayerMsgService, touTiaoAdDataService, restTemplate);
        tasks.add(task);
        taskAsyncPool.execute(task);
      }
      if (!CollectionUtils.isEmpty(tasks)) {

        while (true) {
          int count = 0;
          for (Task task : tasks) {
            if (task.finish) {
              ++count;
            }
          }
          if (count == tasks.size()) {
            break;
          }
        }
        gamePlayerMsgService.delete(new EntityWrapper<GamePlayerMsg>().eq("status", 0));
      }
    }
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

  public static void main(String[] args) {
    System.out.println("9bc0bbdf6f770e1c627fbd52ed642c5c");
  }
}
