package com.ct.orderagent.repo;

import com.ct.orderagent.models.Order;
import com.ct.orderagent.models.OrderStatus;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class OrderRepositoryMockImpl implements OrderRepository {

    private final Map<String, Order> orders = new HashMap<>();

    public OrderRepositoryMockImpl() {
        // Populating mock orders, a new implementation can be written with a database connection and this can be removed
        Order order1 = new Order();
        order1.setOrderId("X_123");
        order1.setOrderStatus(OrderStatus.CONFIRMED);
        order1.setAmount(150.0);
        order1.setExpectedDeliveryDate(new Date(System.currentTimeMillis() + 86400000L)); // Tomorrow
        orders.put("X_123", order1);

        Order order2 = new Order();
        order2.setOrderId("X_121");
        order2.setOrderStatus(OrderStatus.DISPATCHED);
        order2.setAmount(200.0);
        order2.setExpectedDeliveryDate(new Date(System.currentTimeMillis() + 172800000L)); // Day after tomorrow
        orders.put("X_121", order2);

        Order order3 = new Order();
        order3.setOrderId("X_125");
        order3.setOrderStatus(OrderStatus.DELIVERY_FAILURE);
        order3.setAmount(75.0);
        order3.setExpectedDeliveryDate(new Date(System.currentTimeMillis() - 86400000L)); // Yesterday
        orders.put("X_125", order3);

        Order order4 = new Order();
        order4.setOrderId("X_128");
        order4.setOrderStatus(OrderStatus.LOST_IN_TRANSIT);
        order4.setAmount(300.0);
        order4.setExpectedDeliveryDate(new Date(System.currentTimeMillis() - 172800000L)); // Two days ago
        orders.put("X_128", order4);

        Order order5 = new Order();
        order5.setOrderId("X_130");
        order5.setOrderStatus(OrderStatus.PAYMENT_SETTLED);
        order5.setAmount(120.0);
        order5.setExpectedDeliveryDate(new Date(System.currentTimeMillis() + 259200000L)); // Three days from now
        orders.put("X_130", order5);

        Order order6 = new Order();
        order6.setOrderId("X_132");
        order6.setOrderStatus(OrderStatus.DELIVERED);
        order6.setAmount(180.0);
        order6.setExpectedDeliveryDate(new Date(System.currentTimeMillis() - 259200000L)); // Three days ago
        orders.put("X_132", order6);
    }

    @Override
    public Order saveOrder(String orderId, Order orderDetails) {
        if (!orders.containsKey(orderId)){
            throw new NoSuchElementException("Order not found for orderId: " + orderId);
        }
        orders.put(orderId, orderDetails);
        return orders.get(orderId);

    }

    @Override
    public Order getOrderDetails(String orderId) {
        return orders.get(orderId);
    }
}
