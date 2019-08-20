package com.dragon.sdk.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dragon.sdk.entity.ThirdpartyGamePushAndroidData;
import com.dragon.sdk.entity.ThirdpartyGamePushIosData;
import com.dragon.sdk.mapper.ThirdpartyGamePushAndroidDataMapper;
import com.dragon.sdk.mapper.ThirdpartyGamePushIosDataMapper;
import com.dragon.sdk.service.IThirdpartyGamePushAndroidDataService;
import com.dragon.sdk.service.IThirdpartyGamePushIosDataService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 服务实现类
 *
 * @author dingpengfei
 * @since 2019-05-09
 */
@Service
public class ThirdpartyGamePushIosDataServiceImpl extends ServiceImpl<ThirdpartyGamePushIosDataMapper, ThirdpartyGamePushIosData>
    implements IThirdpartyGamePushIosDataService {

    @Resource private ThirdpartyGamePushIosDataMapper gamePushIosDataMapper;

    @Override
    public boolean insert(ThirdpartyGamePushIosData entity) {
        return gamePushIosDataMapper.insert(entity)==1;
    }
}
