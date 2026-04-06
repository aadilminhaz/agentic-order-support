package com.ct.orderagent.repo;

import com.ct.orderagent.models.Order;

public interface OrderRepository {
    public Order saveOrder(String orderId, Order orderDetails);
    public Order getOrderDetails(String orderId);
}
