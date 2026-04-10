package com.ct.orderagent.models;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefundResponse {
    private double refundAmount;
    private String orderId;
    private OrderStatus orderStatus;
}
