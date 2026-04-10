package com.ct.orderagent.repo;

import com.ct.orderagent.models.Order;
import com.ct.orderagent.models.OrderStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderRepositoryMockImplTest {

    private final OrderRepositoryMockImpl repository = new OrderRepositoryMockImpl();

    @Test
    void testGetOrderDetails_ExistingOrder() {
        Order order = repository.getOrderDetails("X_123");

        assertNotNull(order);
        assertEquals("X_123", order.getOrderId());
        assertEquals(OrderStatus.CONFIRMED, order.getOrderStatus());
        assertEquals(150.0, order.getAmount());
        assertNotNull(order.getExpectedDeliveryDate());
    }

    @Test
    void testGetOrderDetails_NonExistingOrder() {
        Order order = repository.getOrderDetails("X_999");

        assertNull(order);
    }

    @Test
    void testGetOrderDetails_DeliveryFailureOrder() {
        Order order = repository.getOrderDetails("X_125");

        assertNotNull(order);
        assertEquals("X_125", order.getOrderId());
        assertEquals(OrderStatus.DELIVERY_FAILURE, order.getOrderStatus());
        assertEquals(75.0, order.getAmount());
        assertNotNull(order.getExpectedDeliveryDate());
    }

    @Test
    void testGetOrderDetails_LostInTransitOrder() {
        Order order = repository.getOrderDetails("X_128");

        assertNotNull(order);
        assertEquals("X_128", order.getOrderId());
        assertEquals(OrderStatus.LOST_IN_TRANSIT, order.getOrderStatus());
        assertEquals(300.0, order.getAmount());
        assertNotNull(order.getExpectedDeliveryDate());
    }
}
