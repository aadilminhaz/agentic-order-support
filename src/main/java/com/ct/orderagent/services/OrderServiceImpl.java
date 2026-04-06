package com.ct.orderagent.services;

import com.ct.orderagent.models.Order;
import com.ct.orderagent.models.OrderStatus;
import com.ct.orderagent.repo.OrderRepository;


public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

   public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderStatus getOrderStatus(String orderId) {
        Order order = orderRepository.getOrderDetails(orderId);
        if (order == null) {
            throw new RuntimeException("Order Not Found!");
        }
        return order.getOrderStatus();
    }

    @Override
    public Order reAttemptDelivery(String orderId) {
        Order order = orderRepository.getOrderDetails(orderId);
        if (order == null) {
            throw new RuntimeException("Order Not Found!");
        }
        if (order.getOrderStatus() == OrderStatus.DELIVERY_FAILURE) {
            order.setOrderStatus(OrderStatus.DISPATCHED);
        }
        orderRepository.saveOrder(orderId, order);
        return order;
    }
}
