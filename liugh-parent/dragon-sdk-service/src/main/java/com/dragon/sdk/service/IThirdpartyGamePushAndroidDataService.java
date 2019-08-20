package com.dragon.sdk.service;

import com.baomidou.mybatisplus.service.IService;
import com.dragon.sdk.entity.GameCallData;
import com.dragon.sdk.entity.ThirdpartyGamePushAndroidData;

/**
 * <p>
 * 第三方用户表 服务类
 * </p>
 *
 * @author dingpengfei
 * @since 2019-05-09
 */
public interface IThirdpartyGamePushAndroidDataService extends IService<ThirdpartyGamePushAndroidData> {

    @Override
    boolean insert(ThirdpartyGamePushAndroidData entity);
}
