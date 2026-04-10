package com.ct.orderagent.services;

import com.ct.orderagent.models.Order;
import com.ct.orderagent.models.OrderStatus;
import com.ct.orderagent.models.RefundResponse;
import com.ct.orderagent.services.repo.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public record RefundServiceImpl(OrderRepository orderRepository) implements RefundService {
    /**
     * Dummy Implementation of RefundService, it fetches data from the repo with mock data
     * You can write your own implementation as per your business logic
     * **/

    @Override
    public RefundResponse processRefund(String orderId) {
        /**Dummy implementation to process refund. In real world, this will be a call to payment gateway to process the refund and update the order status in DB**/
        Order order = Optional.ofNullable(orderRepository.getOrderDetails(orderId)).orElseThrow(() -> new RuntimeException("Order Not Found!"));

        if (order.getOrderStatus() == OrderStatus.DELIVERY_FAILURE) {
            order.setOrderStatus(OrderStatus.REFUNDED);
            orderRepository.saveOrder(orderId, order);
        }
        RefundResponse response = new RefundResponse();
        response.setOrderId(order.getOrderId());
        response.setRefundAmount(order.getAmount());
        response.setOrderStatus(order.getOrderStatus());
        return response;
    }

    @Override
    public RefundResponse getRefundStatus(String orderId) {
        Order order = Optional.ofNullable(orderRepository.getOrderDetails(orderId)).orElseThrow(() -> new RuntimeException("Order Not Found!"));

        RefundResponse response = new RefundResponse();
        response.setOrderId(order.getOrderId());
        response.setRefundAmount(order.getAmount());
        response.setOrderStatus(order.getOrderStatus());
        return response;
    }

}
