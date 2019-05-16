package com.dragon.sdk.service;

import com.dragon.sdk.entity.Order;
import com.baomidou.mybatisplus.service.IService;
import com.dragon.sdk.enums.OrderAction;
import com.dragon.sdk.enums.OrderType;
import com.dragon.sdk.model.OrderModel;

/**
 * <p>
 * 订单主表，当前表只保存流转中的订单信息 服务类
 * </p>
 *
 * @author dingpengfei
 * @since 2019-05-09
 */
public interface IOrderService extends IService<Order> {

    Order handleOrder(OrderAction action, OrderType orderType, OrderModel orderDef) throws Exception;

}
