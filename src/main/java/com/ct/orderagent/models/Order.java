package com.ct.orderagent.models;

import com.google.auto.value.AutoValue;
import lombok.*;



public class Order {
    private String orderId;
    private OrderStatus orderStatus;

    public Order() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
