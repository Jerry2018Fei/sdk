package com.dragon.sdk.service.impl;

import com.dragon.sdk.entity.OperationLog;
import com.dragon.sdk.mapper.OperationLogMapper;
import com.dragon.sdk.service.IOperationLogService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 操作日志 服务实现类
 * </p>
 *
 * @author dingpengfei
 * @since 2019-05-09
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements IOperationLogService {

}
