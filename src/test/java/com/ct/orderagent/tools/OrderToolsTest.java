package com.ct.orderagent.tools;

import com.ct.orderagent.models.Order;
import com.ct.orderagent.models.OrderStatus;
import com.ct.orderagent.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderToolsTest {

    @Mock
    private OrderService orderService;

    private OrderTools orderTools;

    @BeforeEach
    void setUp() {
        orderTools = new OrderTools(orderService);
    }

    @Test
    void testCheckOrderStatus_Success() {
        String orderId = "ORD123";
        Order testOrder = createMockOrder();
        testOrder.setOrderStatus(OrderStatus.DISPATCHED);
        when(orderService.getOrderStatus(orderId)).thenReturn(createMockOrder());

        Map<String, Object> result = orderTools.checkOrderStatus(orderId);

        Order resultOrder = (Order) result.get("status");
        assertNotNull(result);
        assertTrue(result.containsKey("status"));
        assertEquals(OrderStatus.DISPATCHED, resultOrder.getOrderStatus());
        verify(orderService, times(1)).getOrderStatus(orderId);
    }

    @Test
    void testCheckOrderStatus_VariousStatuses() {
        String orderId = "ORD456";
        OrderStatus[] statuses = {OrderStatus.CONFIRMED, OrderStatus.DELIVERED, 
                                  OrderStatus.DELIVERY_FAILURE, OrderStatus.REFUNDED};

        for (OrderStatus status : statuses) {
            Order mockOrder = createMockOrder();
            mockOrder.setOrderStatus(status);
            when(orderService.getOrderStatus(orderId)).thenReturn(mockOrder);

            Map<String, Object> result = orderTools.checkOrderStatus(orderId);
            Order resultOrder = (Order) result.get("status");
            assertEquals(status, resultOrder.getOrderStatus());
        }
    }

    @Test
    void testCheckOrderStatus_OrderNotFound() {
        String orderId = "INVALID";
        when(orderService.getOrderStatus(orderId)).thenThrow(new RuntimeException("Order Not Found!"));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> orderTools.checkOrderStatus(orderId));

        assertEquals("Order Not Found!", exception.getMessage());
    }

    @Test
    void testReAttemptDelivery_Success() {
        String orderId = "ORD789";
        Order order = new Order();
        order.setOrderId(orderId);
        order.setOrderStatus(OrderStatus.DISPATCHED);
        order.setAmount(100.0);

        when(orderService.reAttemptDelivery(orderId)).thenReturn(order);

        Map<String, Object> result = orderTools.reAttemptDelivery(orderId);

        assertNotNull(result);
        assertTrue(result.containsKey("response"));
        assertEquals(order, result.get("response"));
        verify(orderService, times(1)).reAttemptDelivery(orderId);
    }

    @Test
    void testReAttemptDelivery_OrderNotFound() {
        String orderId = "NONEXISTENT";
        when(orderService.reAttemptDelivery(orderId)).thenThrow(new RuntimeException("Order Not Found!"));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> orderTools.reAttemptDelivery(orderId));

        assertEquals("Order Not Found!", exception.getMessage());
    }

    @Test
    void testReAttemptDelivery_ReturnsUpdatedOrder() {
        String orderId = "ORD999";
        Order order = new Order();
        order.setOrderId(orderId);
        order.setOrderStatus(OrderStatus.DISPATCHED);
        order.setAmount(250.0);
        order.setExpectedDeliveryDate(new Date());

        when(orderService.reAttemptDelivery(orderId)).thenReturn(order);

        Map<String, Object> result = orderTools.reAttemptDelivery(orderId);

        Order returnedOrder = (Order) result.get("response");
        assertEquals(orderId, returnedOrder.getOrderId());
        assertEquals(OrderStatus.DISPATCHED, returnedOrder.getOrderStatus());
        assertEquals(250.0, returnedOrder.getAmount());
    }

    @Test
    void testCheckOrderStatus_MapStructure() {
        String orderId = "ORD100";
        Order mockOrder = createMockOrder();
        mockOrder.setOrderStatus(OrderStatus.CONFIRMED);
        when(orderService.getOrderStatus(orderId)).thenReturn(mockOrder);

        Map<String, Object> result = orderTools.checkOrderStatus(orderId);

        assertEquals(1, result.size());
        assertTrue(result.containsKey("status"));
    }

    @Test
    void testReAttemptDelivery_MapStructure() {
        String orderId = "ORD200";
        Order order = new Order();
        order.setOrderId(orderId);
        order.setOrderStatus(OrderStatus.DISPATCHED);

        when(orderService.reAttemptDelivery(orderId)).thenReturn(order);

        Map<String, Object> result = orderTools.reAttemptDelivery(orderId);

        assertEquals(1, result.size());
        assertTrue(result.containsKey("response"));
    }

    Order createMockOrder(){
        String orderId = "ORD789";
        Order order = new Order();
        order.setOrderId(orderId);
        order.setOrderStatus(OrderStatus.DISPATCHED);
        order.setAmount(100.0);
        return order;
    }
}

