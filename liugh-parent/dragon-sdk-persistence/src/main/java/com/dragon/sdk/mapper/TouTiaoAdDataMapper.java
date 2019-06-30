package com.dragon.sdk.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.dragon.sdk.entity.GameCallData;
import com.dragon.sdk.entity.TouTiaoAdData;

import java.util.List;

/**
 * Mapper 接口
 *
 * @author dingpengfei
 * @since 2019-05-09
 */
public interface TouTiaoAdDataMapper extends BaseMapper<TouTiaoAdData> {

    /**
     * 根据条件查询
     * @param gameCallData 参数
     * @return TouTiaoAdData
     */
    List<TouTiaoAdData> selectByGameCallData(GameCallData gameCallData);
}
