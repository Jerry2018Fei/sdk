package com.dragon.sdk.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.dragon.sdk.entity.TClickIosData;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Mapper 接口
 *
 * @author dingpengfei
 * @since 2019-05-09
 */
@Transactional(rollbackFor = Exception.class)
public interface TClickIosDataMapper extends BaseMapper<TClickIosData> {



    @Override
    Integer insert(TClickIosData entity);

    List<TClickIosData> selectByDevice(@Param("idfa") String idfa, Integer eventType);
}
