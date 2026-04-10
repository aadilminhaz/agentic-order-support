package com.ct.orderagent.services;

import com.ct.orderagent.models.Order;
import com.ct.orderagent.models.OrderStatus;
import com.ct.orderagent.services.repo.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public record OrderServiceImpl(OrderRepository orderRepository) implements OrderService {

    /**
     * Dummy Implementation of OrderService, it fetches data from the repo with mock data
     * You can write your own implementation by implementing OrderService as per your business logic
     * **/

    @Override
    public Order getOrderStatus(String orderId) {
        log.info("OrderService: getOrderStatus for orderId: {}", orderId);
        return Optional.ofNullable(orderRepository.getOrderDetails(orderId)).orElseThrow(() -> new RuntimeException("Order Not Found!"));
    }

    @Override
    public Order reAttemptDelivery(String orderId) {
        log.info("OrderService: reAttemptDelivery for orderId: {}", orderId);
        Order order = Optional.ofNullable(orderRepository.getOrderDetails(orderId)).orElseThrow(() -> new RuntimeException("Order Not Found!"));
        if (order.getOrderStatus() == OrderStatus.DELIVERY_FAILURE) {
            order.setOrderStatus(OrderStatus.DISPATCHED);
            order.setExpectedDeliveryDate(new Date(System.currentTimeMillis() + 172800000L));
        }
        orderRepository.saveOrder(orderId, order);
        return order;
    }
}
