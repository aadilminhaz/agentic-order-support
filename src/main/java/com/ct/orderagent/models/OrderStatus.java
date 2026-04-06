package com.ct.orderagent.models;

public enum OrderStatus {
    CONFIRMED("Confirmed"),
    DISPATCHED("Dispatched"),
    PAYMENT_SETTLED("Payment_Settled"),
    DELIVERED("Delivered"),

    DELIVERY_FAILURE("Delivery_Failure"),
    LOST_IN_TRANSIT("Lost_In_Transit"),
    RETURNED("Returned"),
    REFUND_INITIATED("Refund_Initiated"),
    REFUNDED("Refunded");

    private final String status;


    OrderStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }
}
