package com.dragon.sdk;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.dragon.sdk.entity.GamePlayerMsg;
import com.dragon.sdk.entity.TouTiaoAdData;
import com.dragon.sdk.service.IGamePlayerMsgService;
import com.dragon.sdk.service.ITouTiaoAdDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;

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

  @Scheduled(cron = "0 0/10 * * * ?")
  public void hello() {
    List<TouTiaoAdData> datas =
        touTiaoAdDataService.selectList(
            new EntityWrapper<TouTiaoAdData>().eq("delete_flag", 0).eq("is_send", 0));
    if (!CollectionUtils.isEmpty(datas)) {
      for (TouTiaoAdData data : datas) {
        GamePlayerMsg msg =
            gamePlayerMsgService.selectOne(
                new EntityWrapper<GamePlayerMsg>()
                    .eq("delete_flag", 0)
                    .eq("status", 0)
                    .eq("imei", data.getImei())
                    .or()
                    .eq("ip", data.getIp()));
        if (msg != null) {
          try {
            AdCallbackResult result =
                restTemplate.getForObject(
                    data.getCallbackUrl() + "&os=0&event_type=0", AdCallbackResult.class);
            if (result != null && result.getCode() == 0) {
              log.info("回调{}成功", data.getCallbackUrl() + "&os=0&event_type=0");
              msg.setStatus(1);
              gamePlayerMsgService.updateAllColumnById(msg);
              data.setIsSend(1);
              touTiaoAdDataService.updateAllColumnById(data);
            }

          } catch (RestClientException e) {
            e.printStackTrace();
          }
        }
      }
    }

  }

  public static void main(String[] args) {
    System.out.println("9bc0bbdf6f770e1c627fbd52ed642c5c");
  }
}
