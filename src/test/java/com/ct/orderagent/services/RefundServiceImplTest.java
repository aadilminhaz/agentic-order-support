package com.ct.orderagent.services;

import com.ct.orderagent.models.Order;
import com.ct.orderagent.models.OrderStatus;
import com.ct.orderagent.models.RefundResponse;
import com.ct.orderagent.services.repo.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefundServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    private RefundServiceImpl refundService;

    @BeforeEach
    void setUp() {
        refundService = new RefundServiceImpl(orderRepository);
    }

    @Test
    void testProcessRefund_DeliveryFailureStatus() {
        String orderId = "ORD123";
        Order order = new Order();
        order.setOrderId(orderId);
        order.setOrderStatus(OrderStatus.DELIVERY_FAILURE);
        order.setAmount(100.0);
        order.setExpectedDeliveryDate(new Date());

        when(orderRepository.getOrderDetails(orderId)).thenReturn(order);

        RefundResponse response = refundService.processRefund(orderId);

        assertEquals(orderId, response.getOrderId());
        assertEquals(100.0, response.getRefundAmount());
        assertEquals(OrderStatus.REFUNDED, response.getOrderStatus());
        assertEquals(OrderStatus.REFUNDED, order.getOrderStatus());
        verify(orderRepository, times(1)).getOrderDetails(orderId);
        verify(orderRepository, times(1)).saveOrder(orderId, order);
    }

    @Test
    void testProcessRefund_NonDeliveryFailureStatus() {
        String orderId = "ORD456";
        Order order = new Order();
        order.setOrderId(orderId);
        order.setOrderStatus(OrderStatus.DELIVERED);
        order.setAmount(200.0);

        when(orderRepository.getOrderDetails(orderId)).thenReturn(order);

        RefundResponse response = refundService.processRefund(orderId);

        assertEquals(orderId, response.getOrderId());
        assertEquals(200.0, response.getRefundAmount());
        assertEquals(OrderStatus.DELIVERED, response.getOrderStatus());
        verify(orderRepository, times(1)).getOrderDetails(orderId);
        verify(orderRepository, never()).saveOrder(anyString(), any());
    }

    @Test
    void testProcessRefund_OrderNotFound() {
        String orderId = "INVALID123";

        when(orderRepository.getOrderDetails(orderId)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> refundService.processRefund(orderId));
        
        assertEquals("Order Not Found!", exception.getMessage());
        verify(orderRepository, never()).saveOrder(anyString(), any());
    }

    @Test
    void testProcessRefund_VariousAmounts() {
        double[] amounts = {50.0, 100.0, 500.0, 1000.0};

        for (double amount : amounts) {
            String orderId = "ORD" + (int)amount;
            Order order = new Order();
            order.setOrderId(orderId);
            order.setOrderStatus(OrderStatus.DELIVERY_FAILURE);
            order.setAmount(amount);

            when(orderRepository.getOrderDetails(orderId)).thenReturn(order);

            RefundResponse response = refundService.processRefund(orderId);

            assertEquals(amount, response.getRefundAmount());
        }
    }

    @Test
    void testGetRefundStatus_Success() {
        String orderId = "ORD789";
        Order order = new Order();
        order.setOrderId(orderId);
        order.setOrderStatus(OrderStatus.REFUNDED);
        order.setAmount(150.0);

        when(orderRepository.getOrderDetails(orderId)).thenReturn(order);

        RefundResponse response = refundService.getRefundStatus(orderId);

        assertEquals(orderId, response.getOrderId());
        assertEquals(150.0, response.getRefundAmount());
        assertEquals(OrderStatus.REFUNDED, response.getOrderStatus());
        verify(orderRepository, times(1)).getOrderDetails(orderId);
    }

    @Test
    void testGetRefundStatus_WithVariousStatuses() {
        OrderStatus[] statuses = {OrderStatus.REFUNDED, OrderStatus.REFUND_INITIATED, 
                                  OrderStatus.DELIVERY_FAILURE, OrderStatus.DISPATCHED};

        for (OrderStatus status : statuses) {
            String orderId = "ORD_" + status;
            Order order = new Order();
            order.setOrderId(orderId);
            order.setOrderStatus(status);
            order.setAmount(300.0);

            when(orderRepository.getOrderDetails(orderId)).thenReturn(order);

            RefundResponse response = refundService.getRefundStatus(orderId);

            assertEquals(status, response.getOrderStatus());
            assertEquals(300.0, response.getRefundAmount());
        }
    }

    @Test
    void testGetRefundStatus_OrderNotFound() {
        String orderId = "NONEXISTENT";

        when(orderRepository.getOrderDetails(orderId)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> refundService.getRefundStatus(orderId));
        
        assertEquals("Order Not Found!", exception.getMessage());
    }

    @Test
    void testGetRefundStatus_DoesNotModifyOrder() {
        String orderId = "ORD999";
        Order order = new Order();
        order.setOrderId(orderId);
        order.setOrderStatus(OrderStatus.DELIVERY_FAILURE);
        order.setAmount(250.0);

        when(orderRepository.getOrderDetails(orderId)).thenReturn(order);

        RefundResponse response = refundService.getRefundStatus(orderId);

        assertEquals(OrderStatus.DELIVERY_FAILURE, response.getOrderStatus());
        verify(orderRepository, never()).saveOrder(anyString(), any());
    }

    @Test
    void testRefundResponse_PropertiesSet() {
        RefundResponse response = new RefundResponse();
        response.setOrderId("ORD100");
        response.setRefundAmount(500.0);
        response.setOrderStatus(OrderStatus.REFUNDED);

        assertEquals("ORD100", response.getOrderId());
        assertEquals(500.0, response.getRefundAmount());
        assertEquals(OrderStatus.REFUNDED, response.getOrderStatus());
    }
}

