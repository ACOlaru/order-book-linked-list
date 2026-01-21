package com.orderMatchingEngine.strategies;

import com.orderMatchingEngine.domain.Order;
import com.orderMatchingEngine.domain.Trade;
import com.orderMatchingEngine.services.OrderBook;

import java.util.List;

public interface MatchingStrategy {
    List<Trade> match(Order orderIncoming, OrderBook orderBook);
}
