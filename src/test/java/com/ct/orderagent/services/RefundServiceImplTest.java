package com.ct.orderagent.services;

import com.ct.orderagent.models.Order;
import com.ct.orderagent.models.OrderStatus;
import com.ct.orderagent.models.RefundResponse;
import com.ct.orderagent.repo.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RefundServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private RefundServiceImpl refundService;

    private Order mockOrder;

    @BeforeEach
    void setUp() {
        mockOrder = new Order();
        mockOrder.setOrderId("X_125");
        mockOrder.setOrderStatus(OrderStatus.DELIVERY_FAILURE);
        mockOrder.setAmount(75.0);
    }

    @Test
    void testProcessRefund_OrderExists_DeliveryFailure() {
        when(orderRepository.getOrderDetails("X_125")).thenReturn(mockOrder);

        RefundResponse response = refundService.processRefund("X_125");

        assertNotNull(response);
        assertEquals("X_125", response.getOrderId());
        assertEquals(75.0, response.getRefundAmount());
        assertEquals(OrderStatus.REFUNDED, response.getOrderStatus());
        verify(orderRepository, times(1)).getOrderDetails("X_125");
        verify(orderRepository, times(1)).saveOrder("X_125", mockOrder);
        assertEquals(OrderStatus.REFUNDED, mockOrder.getOrderStatus());
    }

    @Test
    void testProcessRefund_OrderNotFound() {
        when(orderRepository.getOrderDetails("X_999")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            refundService.processRefund("X_999");
        });

        assertEquals("Order Not Found!", exception.getMessage());
        verify(orderRepository, times(1)).getOrderDetails("X_999");
        verify(orderRepository, never()).saveOrder(anyString(), any());
    }

    @Test
    void testGetRefundStatus_OrderExists() {
        when(orderRepository.getOrderDetails("X_125")).thenReturn(mockOrder);

        RefundResponse response = refundService.getRefundStatus("X_125");

        assertNotNull(response);
        assertEquals("X_125", response.getOrderId());
        assertEquals(75.0, response.getRefundAmount());
        assertEquals(OrderStatus.DELIVERY_FAILURE, response.getOrderStatus());
        verify(orderRepository, times(1)).getOrderDetails("X_125");
        verify(orderRepository, never()).saveOrder(anyString(), any());
    }

    @Test
    void testGetRefundStatus_OrderNotFound() {
        when(orderRepository.getOrderDetails("X_999")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            refundService.getRefundStatus("X_999");
        });

        assertEquals("Order Not Found!", exception.getMessage());
        verify(orderRepository, times(1)).getOrderDetails("X_999");
        verify(orderRepository, never()).saveOrder(anyString(), any());
    }
}
