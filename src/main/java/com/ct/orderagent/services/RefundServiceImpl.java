package com.ct.orderagent.services;

import com.ct.orderagent.models.Order;
import com.ct.orderagent.models.OrderStatus;
import com.ct.orderagent.models.RefundResponse;
import com.ct.orderagent.repo.OrderRepository;
import com.ct.orderagent.repo.OrderRepositoryMockImpl;

public class RefundServiceImpl implements RefundService {

    private final OrderRepository orderRepository;

    public RefundServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public RefundResponse processRefund(String orderId) {
        /**Dummy implementation to process refund. In real world, this will be a call to payment gateway to process the refund and update the order status in DB**/
        Order order = orderRepository.getOrderDetails(orderId);
        if (order == null) {
            throw new RuntimeException("Order Not Found!");
        }
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
        Order order = orderRepository.getOrderDetails(orderId);
        if (order == null) {
            throw new RuntimeException("Order Not Found!");
        }
        RefundResponse response = new RefundResponse();
        response.setOrderId(order.getOrderId());
        response.setRefundAmount(order.getAmount());
        response.setOrderStatus(order.getOrderStatus());
        return response;
    }

}
