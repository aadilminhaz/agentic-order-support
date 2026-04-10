package com.ct.orderagent.tools;

import com.ct.orderagent.models.OrderStatus;
import com.ct.orderagent.models.RefundResponse;
import com.ct.orderagent.services.RefundService;
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
class RefundToolsTest {

    @Mock
    private RefundService refundService;

    @InjectMocks
    private RefundTools refundTools;

    private RefundResponse mockResponse;

    @BeforeEach
    void setUp() {
        mockResponse = new RefundResponse();
        mockResponse.setOrderId("X_125");
        mockResponse.setRefundAmount(75.0);
        mockResponse.setOrderStatus(OrderStatus.REFUNDED);
    }

    @Test
    void testProcessRefund() {
        String orderId = "X_125";
        when(refundService.processRefund(orderId)).thenReturn(mockResponse);

        Map<String, Object> result = refundTools.processRefund(orderId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockResponse, result.get("response"));
        verify(refundService, times(1)).processRefund(orderId);
    }

    @Test
    void testGetRefundStatus() {
        String orderId = "X_125";
        when(refundService.getRefundStatus(orderId)).thenReturn(mockResponse);

        Map<String, Object> result = refundTools.getRefundStatus(orderId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockResponse, result.get("response"));
        verify(refundService, times(1)).getRefundStatus(orderId);
    }
}
