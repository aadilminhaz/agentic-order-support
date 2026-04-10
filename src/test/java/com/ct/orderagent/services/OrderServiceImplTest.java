package com.ct.orderagent.services;

import com.ct.orderagent.models.Order;
import com.ct.orderagent.models.OrderStatus;
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
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository);
    }

    @Test
    void testGetOrderStatus_Success() {
        String orderId = "ORD123";
        Order order = new Order();
        order.setOrderId(orderId);
        order.setOrderStatus(OrderStatus.DISPATCHED);
        order.setAmount(100.0);
        order.setExpectedDeliveryDate(new Date());

        when(orderRepository.getOrderDetails(orderId)).thenReturn(order);

        OrderStatus status = orderService.getOrderStatus(orderId).getOrderStatus();


        assertEquals(OrderStatus.DISPATCHED, status);
        verify(orderRepository, times(1)).getOrderDetails(orderId);
    }

    @Test
    void testGetOrderStatus_OrderNotFound() {
        String orderId = "INVALID123";

        when(orderRepository.getOrderDetails(orderId)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> orderService.getOrderStatus(orderId));
        
        assertEquals("Order Not Found!", exception.getMessage());
        verify(orderRepository, times(1)).getOrderDetails(orderId);
    }

    @Test
    void testGetOrderStatus_WithDifferentStatuses() {
        String orderId = "ORD456";
        
        for (OrderStatus status : new OrderStatus[]{
            OrderStatus.CONFIRMED, OrderStatus.DELIVERED, 
            OrderStatus.DELIVERY_FAILURE, OrderStatus.REFUNDED}) {
            
            Order order = new Order();
            order.setOrderId(orderId);
            order.setOrderStatus(status);

            when(orderRepository.getOrderDetails(orderId)).thenReturn(order);

            OrderStatus result = orderService.getOrderStatus(orderId).getOrderStatus();

            assertEquals(status, result);
        }
    }

    @Test
    void testReAttemptDelivery_Success() {
        String orderId = "ORD789";
        Order order = new Order();
        order.setOrderId(orderId);
        order.setOrderStatus(OrderStatus.DELIVERY_FAILURE);
        order.setAmount(150.0);
        order.setExpectedDeliveryDate(new Date());

        when(orderRepository.getOrderDetails(orderId)).thenReturn(order);

        Order result = orderService.reAttemptDelivery(orderId);

        assertEquals(OrderStatus.DISPATCHED, result.getOrderStatus());
        verify(orderRepository, times(1)).getOrderDetails(orderId);
        verify(orderRepository, times(1)).saveOrder(orderId, result);
    }

    @Test
    void testReAttemptDelivery_DeliveryDateUpdated() {
        String orderId = "ORD101";
        Order order = new Order();
        order.setOrderId(orderId);
        order.setOrderStatus(OrderStatus.DELIVERY_FAILURE);
        order.setAmount(200.0);
        Date oldDate = new Date();
        order.setExpectedDeliveryDate(oldDate);

        when(orderRepository.getOrderDetails(orderId)).thenReturn(order);

        long beforeTime = System.currentTimeMillis();
        Order result = orderService.reAttemptDelivery(orderId);
        long afterTime = System.currentTimeMillis();

        assertNotNull(result.getExpectedDeliveryDate());
        assertTrue(result.getExpectedDeliveryDate().getTime() >= beforeTime + 172800000L);
        assertTrue(result.getExpectedDeliveryDate().getTime() <= afterTime + 172800000L);
    }

    @Test
    void testReAttemptDelivery_NonDeliveryFailureStatus() {
        String orderId = "ORD202";
        Order order = new Order();
        order.setOrderId(orderId);
        order.setOrderStatus(OrderStatus.DISPATCHED);
        order.setAmount(300.0);

        when(orderRepository.getOrderDetails(orderId)).thenReturn(order);

        Order result = orderService.reAttemptDelivery(orderId);

        assertEquals(OrderStatus.DISPATCHED, result.getOrderStatus());
        verify(orderRepository, times(1)).saveOrder(orderId, result);
    }

    @Test
    void testReAttemptDelivery_OrderNotFound() {
        String orderId = "INVALID999";

        when(orderRepository.getOrderDetails(orderId)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> orderService.reAttemptDelivery(orderId));
        
        assertEquals("Order Not Found!", exception.getMessage());
        verify(orderRepository, never()).saveOrder(anyString(), any());
    }
}

