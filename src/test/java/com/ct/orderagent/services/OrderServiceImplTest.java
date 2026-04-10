package com.ct.orderagent.services;

import com.ct.orderagent.models.Order;
import com.ct.orderagent.models.OrderStatus;
import com.ct.orderagent.repo.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order mockOrder;

    @BeforeEach
    void setUp() {
        mockOrder = new Order();
        mockOrder.setOrderId("X_123");
        mockOrder.setOrderStatus(OrderStatus.CONFIRMED);
    }

    @Test
    void testGetOrderStatus_OrderExists() {
        when(orderRepository.getOrderDetails("X_123")).thenReturn(mockOrder);

        OrderStatus status = orderService.getOrderStatus("X_123");

        assertEquals(OrderStatus.CONFIRMED, status);
        verify(orderRepository, times(1)).getOrderDetails("X_123");
    }

    @Test
    void testGetOrderStatus_OrderNotFound() {
        when(orderRepository.getOrderDetails("X_999")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.getOrderStatus("X_999");
        });

        assertEquals("Order Not Found!", exception.getMessage());
        verify(orderRepository, times(1)).getOrderDetails("X_999");
    }

    @Test
    void testReAttemptDelivery_DeliveryFailure() {
        mockOrder.setOrderStatus(OrderStatus.DELIVERY_FAILURE);
        when(orderRepository.getOrderDetails("X_125")).thenReturn(mockOrder);

        Order result = orderService.reAttemptDelivery("X_125");

        assertEquals(OrderStatus.DISPATCHED, result.getOrderStatus());
        verify(orderRepository, times(1)).getOrderDetails("X_125");
    }

    @Test
    void testReAttemptDelivery_OtherStatus() {
        mockOrder.setOrderStatus(OrderStatus.CONFIRMED);
        when(orderRepository.getOrderDetails("X_123")).thenReturn(mockOrder);

        Order result = orderService.reAttemptDelivery("X_123");

        assertEquals(OrderStatus.CONFIRMED, result.getOrderStatus()); // Should remain unchanged
        verify(orderRepository, times(1)).getOrderDetails("X_123");
    }

    @Test
    void testReAttemptDelivery_OrderNotFound() {
        when(orderRepository.getOrderDetails("X_999")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.reAttemptDelivery("X_999");
        });

        assertEquals("Order Not Found!", exception.getMessage());
        verify(orderRepository, times(1)).getOrderDetails("X_999");
    }
}
