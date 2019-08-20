package com.dragon.sdk.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dragon.sdk.entity.TClickAndroidData;
import com.dragon.sdk.entity.ThirdpartyGamePushAndroidData;
import com.dragon.sdk.mapper.TClickAndroidDataMapper;
import com.dragon.sdk.mapper.ThirdpartyGamePushAndroidDataMapper;
import com.dragon.sdk.service.ITClickAndroidDataService;
import com.dragon.sdk.service.IThirdpartyGamePushAndroidDataService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 服务实现类
 *
 * @author dingpengfei
 * @since 2019-05-09
 */
@Service
public class TClickAndroidDataServiceImpl extends ServiceImpl<TClickAndroidDataMapper, TClickAndroidData>
    implements ITClickAndroidDataService {

    @Resource private TClickAndroidDataMapper mapper;


    @Override
    public boolean insert(TClickAndroidData entity) {
        return mapper.insert(entity)==1;
    }

    @Override
    public List<TClickAndroidData> selectByDevice(String androidid, String imei) {
        return mapper.selectByDevice(androidid,imei);
    }
}
