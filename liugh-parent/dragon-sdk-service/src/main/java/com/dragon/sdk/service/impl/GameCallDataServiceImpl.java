package com.dragon.sdk.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dragon.sdk.entity.GameCallData;
import com.dragon.sdk.mapper.GameCallDataMapper;
import com.dragon.sdk.service.IGameCallDataService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 服务实现类
 *
 * @author dingpengfei
 * @since 2019-05-09
 */
@Service
public class GameCallDataServiceImpl extends ServiceImpl<GameCallDataMapper, GameCallData>
    implements IGameCallDataService {

    @Resource private GameCallDataMapper gameCallDataMapper;
    @Override
    public GameCallData queryByDevice(Integer type, String idfa, String imei, String androidid) {
        return gameCallDataMapper.queryByDevice(type,idfa,imei,androidid);
    }
}
