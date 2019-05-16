package com.dragon.sdk.service.impl;

import com.dragon.sdk.entity.Order;
import com.dragon.sdk.enums.OrderAction;
import com.dragon.sdk.enums.OrderType;
import com.dragon.sdk.mapper.OrderMapper;
import com.dragon.sdk.model.OrderModel;
import com.dragon.sdk.service.IOrderService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dragon.sdk.service.handler.OrderHandler;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单主表，当前表只保存流转中的订单信息 服务实现类
 * </p>
 *
 * @author liugh
 * @since 2019-05-09
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {


    @Override
    public Order handleOrder(OrderAction action, OrderType orderType, OrderModel orderDef) throws Exception {
        Order order = OrderHandler.handle(action, orderType, orderDef);
        return order;
    }

}
