package com.ct.orderagent.tools;

import com.ct.orderagent.models.Order;
import com.ct.orderagent.models.OrderStatus;
import com.ct.orderagent.services.OrderService;
import com.ct.orderagent.services.OrderServiceImpl;
import com.google.adk.tools.Annotations;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Slf4j
public class OrderTools {


    private static final Logger log = LoggerFactory.getLogger(OrderTools.class);

    // Services shared across tool calls — in production inject via DI
    private static final OrderService orderService = new OrderServiceImpl();

    @Annotations.Schema(description = "Fetches the current status and details of an order by order ID")
    public static Map<String, Object> checkOrderStatus(
            @Annotations.Schema(name = "orderId", description = "The unique order identifier, e.g. ORD-1001")
            String orderId) {

        log.info("Tool: checkOrderStatus({})", orderId);
        return Map.of("status", orderService.getOrderStatus(orderId));

    }



    @Annotations.Schema(description = "Get the weather forecast for a given city")
    public static Map<String, String> getWeather(
            @Annotations.Schema(name = "city",
                    description = "Name of the city to get the weather forecast for")
            String city) {
        return Map.of(
                "city", city,
                "forecast", "Sunny day, clear blue sky, temperature up to 24°C"
        );
    }
}
