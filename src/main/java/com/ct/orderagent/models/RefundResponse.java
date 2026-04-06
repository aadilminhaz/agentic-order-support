package com.ct.orderagent.models;

import lombok.Data;

@Data
public class RefundResponse {
    private double refundAmount;
    private String orderId;
    private OrderStatus orderStatus;
}
