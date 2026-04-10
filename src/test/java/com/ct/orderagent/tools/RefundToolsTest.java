package com.ct.orderagent.tools;

import com.ct.orderagent.models.OrderStatus;
import com.ct.orderagent.models.RefundResponse;
import com.ct.orderagent.services.RefundService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefundToolsTest {

    @Mock
    private RefundService refundService;

    private RefundTools refundTools;

    @BeforeEach
    void setUp() {
        refundTools = new RefundTools(refundService);
    }

    @Test
    void testProcessRefund_Success() {
        String orderId = "ORD123";
        RefundResponse refundResponse = new RefundResponse();
        refundResponse.setOrderId(orderId);
        refundResponse.setRefundAmount(100.0);
        refundResponse.setOrderStatus(OrderStatus.REFUNDED);

        when(refundService.processRefund(orderId)).thenReturn(refundResponse);

        Map<String, Object> result = refundTools.processRefund(orderId);

        assertNotNull(result);
        assertTrue(result.containsKey("response"));
        assertEquals(refundResponse, result.get("response"));
        verify(refundService, times(1)).processRefund(orderId);
    }

    @Test
    void testProcessRefund_OrderNotFound() {
        String orderId = "INVALID";
        when(refundService.processRefund(orderId)).thenThrow(new RuntimeException("Order Not Found!"));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> refundTools.processRefund(orderId));

        assertEquals("Order Not Found!", exception.getMessage());
    }

    @Test
    void testProcessRefund_VariousAmounts() {
        double[] amounts = {50.0, 100.0, 500.0, 1000.0};

        for (double amount : amounts) {
            String orderId = "ORD" + (int)amount;
            RefundResponse refundResponse = new RefundResponse();
            refundResponse.setOrderId(orderId);
            refundResponse.setRefundAmount(amount);
            refundResponse.setOrderStatus(OrderStatus.REFUNDED);

            when(refundService.processRefund(orderId)).thenReturn(refundResponse);

            Map<String, Object> result = refundTools.processRefund(orderId);

            RefundResponse response = (RefundResponse) result.get("response");
            assertEquals(amount, response.getRefundAmount());
        }
    }

    @Test
    void testProcessRefund_MapStructure() {
        String orderId = "ORD999";
        RefundResponse refundResponse = new RefundResponse();
        refundResponse.setOrderId(orderId);
        refundResponse.setRefundAmount(150.0);
        refundResponse.setOrderStatus(OrderStatus.REFUNDED);

        when(refundService.processRefund(orderId)).thenReturn(refundResponse);

        Map<String, Object> result = refundTools.processRefund(orderId);

        assertEquals(1, result.size());
        assertTrue(result.containsKey("response"));
    }

    @Test
    void testGetRefundStatus_Success() {
        String orderId = "ORD456";
        RefundResponse refundResponse = new RefundResponse();
        refundResponse.setOrderId(orderId);
        refundResponse.setRefundAmount(200.0);
        refundResponse.setOrderStatus(OrderStatus.REFUNDED);

        when(refundService.getRefundStatus(orderId)).thenReturn(refundResponse);

        Map<String, Object> result = refundTools.getRefundStatus(orderId);

        assertNotNull(result);
        assertTrue(result.containsKey("response"));
        assertEquals(refundResponse, result.get("response"));
        verify(refundService, times(1)).getRefundStatus(orderId);
    }

    @Test
    void testGetRefundStatus_OrderNotFound() {
        String orderId = "NONEXISTENT";
        when(refundService.getRefundStatus(orderId)).thenThrow(new RuntimeException("Order Not Found!"));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> refundTools.getRefundStatus(orderId));

        assertEquals("Order Not Found!", exception.getMessage());
    }

    @Test
    void testGetRefundStatus_VariousStatuses() {
        String orderId = "ORD789";
        OrderStatus[] statuses = {OrderStatus.REFUNDED, OrderStatus.REFUND_INITIATED,
                                  OrderStatus.DELIVERY_FAILURE, OrderStatus.DISPATCHED};

        for (OrderStatus status : statuses) {
            RefundResponse refundResponse = new RefundResponse();
            refundResponse.setOrderId(orderId);
            refundResponse.setRefundAmount(300.0);
            refundResponse.setOrderStatus(status);

            when(refundService.getRefundStatus(orderId)).thenReturn(refundResponse);

            Map<String, Object> result = refundTools.getRefundStatus(orderId);

            RefundResponse response = (RefundResponse) result.get("response");
            assertEquals(status, response.getOrderStatus());
        }
    }

    @Test
    void testGetRefundStatus_ReturnsRefundDetails() {
        String orderId = "ORD222";
        RefundResponse refundResponse = new RefundResponse();
        refundResponse.setOrderId(orderId);
        refundResponse.setRefundAmount(450.0);
        refundResponse.setOrderStatus(OrderStatus.REFUNDED);

        when(refundService.getRefundStatus(orderId)).thenReturn(refundResponse);

        Map<String, Object> result = refundTools.getRefundStatus(orderId);

        RefundResponse response = (RefundResponse) result.get("response");
        assertEquals(orderId, response.getOrderId());
        assertEquals(450.0, response.getRefundAmount());
        assertEquals(OrderStatus.REFUNDED, response.getOrderStatus());
    }

    @Test
    void testGetRefundStatus_MapStructure() {
        String orderId = "ORD333";
        RefundResponse refundResponse = new RefundResponse();
        refundResponse.setOrderId(orderId);
        refundResponse.setRefundAmount(175.0);
        refundResponse.setOrderStatus(OrderStatus.REFUND_INITIATED);

        when(refundService.getRefundStatus(orderId)).thenReturn(refundResponse);

        Map<String, Object> result = refundTools.getRefundStatus(orderId);

        assertEquals(1, result.size());
        assertTrue(result.containsKey("response"));
    }
}

