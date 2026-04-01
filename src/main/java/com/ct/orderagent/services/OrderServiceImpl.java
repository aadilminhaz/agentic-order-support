package com.ct.orderagent.services;

import com.ct.orderagent.models.Order;
import com.ct.orderagent.models.OrderStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderServiceImpl implements OrderService {
    @Override
    public OrderStatus getOrderStatus(String orderId) {
        List<Order> orders = getOrders();
        return orders.stream()
                .filter(o -> o.getOrderId().equals(orderId)).findAny().map(Order::getOrderStatus).orElseThrow(()-> new RuntimeException("Order Not Found!"));
    }

    List<Order> getOrders() {
        List<Order> orders = new ArrayList<>();
        //mock orders
        Order order_1 = new Order();
        order_1.setOrderId("X_123");
        order_1.setOrderStatus(OrderStatus.CONFIRMED);

        Order order_2 = new Order();
        order_2.setOrderId("X_121");
        order_2.setOrderStatus(OrderStatus.DISPATCHED);

        Order order_3 = new Order();
        order_3.setOrderId("X_125");
        order_3.setOrderStatus(OrderStatus.DELIVERY_FAILURE);

        Order order_4 = new Order();
        order_3.setOrderId("X_128");
        order_3.setOrderStatus(OrderStatus.LOST_IN_TRANSIT);

        orders.add(order_1);
        orders.add(order_2);
        orders.add(order_3);
        orders.add(order_4);

        return orders;


    }
}
