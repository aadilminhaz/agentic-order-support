package com.ct.orderagent.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private String orderId;
    private OrderStatus orderStatus;
    private double amount;
    private Date expectedDeliveryDate;
}
