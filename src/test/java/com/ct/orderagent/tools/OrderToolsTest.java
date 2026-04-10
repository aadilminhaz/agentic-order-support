package com.ct.orderagent.tools;

import com.ct.orderagent.models.OrderStatus;
import com.ct.orderagent.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderToolsTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderTools orderTools;

    @Test
    void testCheckOrderStatus() {
        String orderId = "X_123";
        OrderStatus mockStatus = OrderStatus.CONFIRMED;
        when(orderService.getOrderStatus(orderId)).thenReturn(mockStatus);

        Map<String, Object> result = orderTools.checkOrderStatus(orderId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockStatus, result.get("status"));
        verify(orderService, times(1)).getOrderStatus(orderId);
    }
}
