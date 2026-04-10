package com.ct.orderagent.tools;

import com.ct.orderagent.services.RefundService;
import com.google.adk.tools.Annotations;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class RefundTools {

    private static final Logger log = LoggerFactory.getLogger(RefundTools.class);
    private final RefundService refundService;

    @Annotations.Schema(description = "Processes a refund for a given order ID and returns the refund details")
    public Map<String, Object> processRefund(
            @Annotations.Schema(name = "orderId", description = "The unique order identifier for which to process the refund, e.g. ORD-1001")
            String orderId) {
        log.info("Tool: processRefund({})", orderId);
        return Map.of(
                "response", refundService.processRefund(orderId)
        );
    }

    @Annotations.Schema(description = "Checks the refund status for a given order ID and returns the current status")
    public Map<String, Object> getRefundStatus(
            @Annotations.Schema(name="orderId", description = "Then unique order identifier to check refund status for, e.g. ORD-1001")
            String orderId) {
            log.info("Tool:     getRefundStatus({})", orderId);
            return Map.of("response", refundService.getRefundStatus(orderId));
        }
}
