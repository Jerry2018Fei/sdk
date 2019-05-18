package com.dragon.sdk.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dragon.sdk.entity.GamePlayerMsg;
import com.dragon.sdk.mapper.GamePlayerMsgMapper;
import com.dragon.sdk.service.IGamePlayerMsgService;
import org.springframework.stereotype.Service;

/**
 * 服务实现类
 *
 * @author dingpengfei
 * @since 2019-05-09
 */
@Service
public class GamePlayerMsgServiceImpl extends ServiceImpl<GamePlayerMsgMapper, GamePlayerMsg>
    implements IGamePlayerMsgService {}
