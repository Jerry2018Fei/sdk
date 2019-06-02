package com.dragon.sdk.controller;

import com.dragon.sdk.config.web.http.ResponseHelper;
import com.dragon.sdk.config.web.http.ResponseModel;
import com.dragon.sdk.entity.Order;
import com.dragon.sdk.enums.OrderAction;
import com.dragon.sdk.enums.OrderType;
import com.dragon.sdk.model.OrderModel;
import com.dragon.sdk.service.IOrderService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author liugh
 * @since 2019-05-09
 */
@RestController
public class OrderController {

    @Resource
    private IOrderService orderService;

    /**
     * 测试工作流
     * 订单发货(动作),待发货-->已发货(状态)
     * @param orderType
     * @param orderModel
     * @return
     * @throws Exception
     */
    @PutMapping(value = "/deliver/{orderType}")
    public ResponseModel<Order> updateDeliver(@PathVariable String orderType, @RequestBody OrderModel orderModel)
            throws Exception {
        Order orderDef = orderService.handleOrder(OrderAction.deliver, OrderType.getInstance(orderType), orderModel);
        return ResponseHelper.buildResponseModel(orderDef);
    }


}
