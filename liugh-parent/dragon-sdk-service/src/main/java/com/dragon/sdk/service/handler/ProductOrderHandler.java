package com.dragon.sdk.service.handler;

import com.dragon.sdk.entity.Order;
import com.dragon.sdk.enums.OrderAction;
import com.dragon.sdk.enums.OrderType;
import com.dragon.sdk.model.OrderModel;
import com.dragon.sdk.service.processor.ActionProcessor;
import org.springframework.stereotype.Component;

/**
 * @author liugh
 * @since 2019-05-09
 */
@Component("ProductOrderHandler")
public class ProductOrderHandler extends OrderHandler {

	@Override
	public Order handleInternal(OrderAction action, OrderType orderType, OrderModel orderDef, Order currentOrder) throws Exception {
		return  ActionProcessor.process(action,orderType,orderDef,currentOrder);
	}
}
