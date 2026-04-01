package com.ct.orderagent.services;

import com.ct.orderagent.models.Order;
import com.ct.orderagent.models.OrderStatus;

public interface OrderService {
    public OrderStatus getOrderStatus(String orderId);
}
