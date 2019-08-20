package com.dragon.sdk.service;

import com.baomidou.mybatisplus.service.IService;
import com.dragon.sdk.entity.TClickAndroidData;
import com.dragon.sdk.entity.ThirdpartyGamePushAndroidData;
import com.dragon.sdk.mapper.TClickAndroidDataMapper;

import java.util.List;

/**
 * <p>
 * 第三方用户表 服务类
 * </p>
 *
 * @author dingpengfei
 * @since 2019-05-09
 */
public interface ITClickAndroidDataService extends IService<TClickAndroidData> {

    @Override
    boolean insert(TClickAndroidData entity);

    List<TClickAndroidData> selectByDevice(String androidid, String imei);
}
