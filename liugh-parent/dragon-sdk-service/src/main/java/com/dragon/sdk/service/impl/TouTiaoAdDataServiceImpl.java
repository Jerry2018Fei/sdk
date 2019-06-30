package com.dragon.sdk.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dragon.sdk.entity.GameCallData;
import com.dragon.sdk.entity.TouTiaoAdData;
import com.dragon.sdk.mapper.TouTiaoAdDataMapper;
import com.dragon.sdk.service.ITouTiaoAdDataService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 服务实现类
 *
 * @author dingpengfei
 * @since 2019-05-09
 */
@Service
public class TouTiaoAdDataServiceImpl extends ServiceImpl<TouTiaoAdDataMapper, TouTiaoAdData>
    implements ITouTiaoAdDataService {
    @Resource
    private TouTiaoAdDataMapper touTiaoAdDataMapper;

    @Override
    public List<TouTiaoAdData> selectByGameCallData(GameCallData gameCallData) {
        return touTiaoAdDataMapper.selectByGameCallData(gameCallData);
    }
}
