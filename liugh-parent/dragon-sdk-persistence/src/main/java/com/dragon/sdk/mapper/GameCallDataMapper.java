package com.dragon.sdk.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.dragon.sdk.entity.GameCallData;

/**
 * Mapper 接口
 *
 * @author dingpengfei
 * @since 2019-05-09
 */
public interface GameCallDataMapper extends BaseMapper<GameCallData> {


    GameCallData queryByDevice(Integer type, String idfa, String imei, String androidid);
}
