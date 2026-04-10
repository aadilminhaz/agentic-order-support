package com.ct.orderagent.services;

import com.ct.orderagent.models.RefundResponse;

public interface RefundService {
    public RefundResponse processRefund(String orderId);
    public RefundResponse getRefundStatus(String orderId);
}
