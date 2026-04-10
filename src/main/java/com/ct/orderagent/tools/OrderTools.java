package com.ct.orderagent.tools;

import com.ct.orderagent.services.OrderService;
import com.google.adk.tools.Annotations;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@RequiredArgsConstructor
public class OrderTools  {

    private static final Logger log = LoggerFactory.getLogger(OrderTools.class);
    private final OrderService orderService;

    @Annotations.Schema(description = "Fetches the current status and details of an order by order ID")
    public Map<String, Object> checkOrderStatus(
            @Annotations.Schema(name = "orderId", description = "The unique order identifier, e.g. ORD-1001")
            String orderId) {

        log.info("Tool: checkOrderStatus for orderId:{}", orderId);
        return Map.of("status", orderService.getOrderStatus(orderId));

    }

    @Annotations.Schema(description = "Re-attempts delivery for a given order ID and returns the updated order details")
    public Map<String, Object> reAttemptDelivery(
            @Annotations.Schema(name = "orderId", description = "The unique order identifier for which to re-attempt delivery, e.g. ORD-1001")
            String orderId) {
        log.info("Tool: reAttemptDelivery for orderId:{}", orderId);
        return Map.of("response", orderService.reAttemptDelivery(orderId));
    }

}
