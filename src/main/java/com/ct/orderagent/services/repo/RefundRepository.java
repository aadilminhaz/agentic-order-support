package com.ct.orderagent.services.repo;

import com.ct.orderagent.models.RefundResponse;

public interface RefundRepository {

    public RefundResponse getRefundDetails(String orderId);
    public RefundResponse processRefund(String orderId);
}
