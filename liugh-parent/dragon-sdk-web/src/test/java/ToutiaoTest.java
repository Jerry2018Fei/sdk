import com.dragon.sdk.entity.GameCallData;
import com.dragon.sdk.service.ITouTiaoAdDataService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @author dingpengfei
 * @date 2019-06-22 16:54
 */
@Slf4j
public class ToutiaoTest extends BaseTest {
  @Resource private ITouTiaoAdDataService touTiaoAdDataService;

//  @Test
  public void hello() {
    GameCallData gameCallData = new GameCallData();
    gameCallData.setMac("");
    gameCallData.setAndroidid("");
    gameCallData.setImei("");
    gameCallData.setIdfa("");
//    TouTiaoAdData data = touTiaoAdDataService.selectByGameCallData(gameCallData);
//    log.info(JSONObject.toJSONString(data));

  }
}
