import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.dragon.sdk.componment.CronWork;
import com.dragon.sdk.entity.GameCallData;
import com.dragon.sdk.mapper.BaseTest;
import com.dragon.sdk.mapper.GameCallDataMapper;
import com.dragon.sdk.service.ITouTiaoAdDataService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @author dingpengfei
 * @date 2019-06-22 16:54
 */
@Slf4j
public class ToutiaoTest extends BaseTest {
  @Resource private ITouTiaoAdDataService touTiaoAdDataService;
  @Resource private GameCallDataMapper gameCallDataMapper;
  @Resource private CronWork cronWork;

//    @Test
  public void hello() throws NoSuchAlgorithmException {
//    List<GameCallData> datas = gameCallDataMapper.selectList(new EntityWrapper<>());
//    for(GameCallData d:datas){
//      cronWork.patchAdData(d);
//    }
//    List<TouTiaoAdData> list = touTiaoAdDataService.selectList(
//            new EntityWrapper<TouTiaoAdData>()
//
//                    .ne("is_send", -1)
//    );
//    for(GameCallData d:datas){
//      if(!StringUtils.isEmpty(d.getAndroidid())){
//        d.setAndroidid(MD5Utils.string2MD5(d.getAndroidid()));
//      }
//      if(!StringUtils.isEmpty(d.getImei())){
//        try {
//          d.setImei(CronWork.getMD5Sum(CronWork.imei(d.getImei())));
//        } catch (Exception e) {
//          e.printStackTrace();
//        }
//      }
//    }
//    Set<TouTiaoAdData> un=new HashSet<>();
//    for(TouTiaoAdData d1:list){
//      boolean b=false;
//      for(GameCallData d:datas){
//        if(d.getImei().equalsIgnoreCase(d1.getImei())
//        ||d.getAndroidid().equalsIgnoreCase(d1.getAndroidid())
//        ||d.getIdfa().equalsIgnoreCase(d1.getIdfa())){
//          b=true;
//        }
//      }
//      if(!b){
//        un.add(d1);
//      }
//    }
//
//    System.out.println(un.size());
  }
}
