package com.ct.orderagent.services.repo;

import com.ct.orderagent.models.RefundResponse;

public class RefundRepositoryMockImpl implements RefundRepository {

    @Override
    public RefundResponse getRefundDetails(String orderId) {
        return null;
    }

    @Override
    public RefundResponse processRefund(String orderId) {
        return null;
    }
}
