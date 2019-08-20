package com.dragon.sdk.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.dragon.sdk.entity.TClickAndroidData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper 接口
 *
 * @author dingpengfei
 * @since 2019-05-09
 */
public interface TClickAndroidDataMapper extends BaseMapper<TClickAndroidData> {


    /**
     * insert
     * @param entity entity
     * @return 1/0
     */
    @Override
    Integer insert(TClickAndroidData entity);

    /**
     * 根据设备信息查询
     * @param androidid androidid
     * @param imei imei
     * @param eventType
     * @return list
     */
    List<TClickAndroidData> selectByDevice(@Param("androidid") String androidid, @Param("imei") String imei, Integer eventType);
}
