package com.dragon.sdk.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.dragon.sdk.entity.ThirdpartyGamePushAndroidData;
import org.springframework.stereotype.Repository;

/**
 * @author dingpengfei
 */
@Repository
public interface ThirdpartyGamePushAndroidDataMapper extends BaseMapper<ThirdpartyGamePushAndroidData> {
    /**
     * insert
     * @param entity 新增数据
     * @return 成功/失败
     */
    @Override
    Integer insert(ThirdpartyGamePushAndroidData entity);

}
