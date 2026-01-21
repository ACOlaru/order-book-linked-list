package com.orderMatchingEngine.domain;

import java.util.concurrent.atomic.AtomicLong;

public class TradeIdGenerator {
    private static final AtomicLong counter = new AtomicLong(0);

    public static long getNextId() {
        return counter.incrementAndGet();
    }
}
