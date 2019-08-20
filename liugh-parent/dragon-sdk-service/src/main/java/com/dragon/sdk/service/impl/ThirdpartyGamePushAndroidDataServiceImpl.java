package com.dragon.sdk.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dragon.sdk.entity.GameCallData;
import com.dragon.sdk.entity.ThirdpartyGamePushAndroidData;
import com.dragon.sdk.mapper.GameCallDataMapper;
import com.dragon.sdk.mapper.ThirdpartyGamePushAndroidDataMapper;
import com.dragon.sdk.service.IGameCallDataService;
import com.dragon.sdk.service.IThirdpartyGamePushAndroidDataService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 服务实现类
 *
 * @author dingpengfei
 * @since 2019-05-09
 */
@Service
public class ThirdpartyGamePushAndroidDataServiceImpl extends ServiceImpl<ThirdpartyGamePushAndroidDataMapper, ThirdpartyGamePushAndroidData>
    implements IThirdpartyGamePushAndroidDataService {

    @Resource private ThirdpartyGamePushAndroidDataMapper gamePushAndroidDataMapper;

    @Override
    public boolean insert(ThirdpartyGamePushAndroidData entity) {
        return gamePushAndroidDataMapper.insert(entity)==1;
    }
}
