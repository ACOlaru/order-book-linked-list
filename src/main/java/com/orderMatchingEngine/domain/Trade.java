package com.orderMatchingEngine.domain;

public record Trade(
        long id,
        String buyOrderId,
        String sellOrderId,
        String symbol,
        int price,
        double quantity,
        long timestamp
) {
}
