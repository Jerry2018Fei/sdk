package com.dragon.sdk.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.dragon.sdk.entity.ThirdpartyGamePushAndroidData;
import com.dragon.sdk.entity.ThirdpartyGamePushIosData;
import org.springframework.stereotype.Repository;

/**
 * @author dingpengfei
 */
@Repository
public interface ThirdpartyGamePushIosDataMapper extends BaseMapper<ThirdpartyGamePushIosData> {
    /**
     * insert
     * @param entity 新增数据
     * @return 成功/失败
     */
    @Override
    Integer insert(ThirdpartyGamePushIosData entity);

}
