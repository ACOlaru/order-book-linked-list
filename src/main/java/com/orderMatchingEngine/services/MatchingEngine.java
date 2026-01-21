package com.orderMatchingEngine.services;

import com.orderMatchingEngine.domain.Order;
import com.orderMatchingEngine.domain.Trade;
import com.orderMatchingEngine.strategies.MatchingStrategy;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MatchingEngine {
    private final OrderBook orderBook;
    private final MatchingStrategy matchingStrategy;
    private final Lock lock = new ReentrantLock();

    public MatchingEngine(OrderBook orderBook, MatchingStrategy matchingStrategy) {
        this.orderBook = orderBook;
        this.matchingStrategy = matchingStrategy;
    }

    public List<Trade> processOrder(Order order) {
        lock.lock();
        try {
            switch (order.getOrderType()) {
                case NEW:
                    List<Trade> trades = matchingStrategy.match(order, orderBook);
                    if (!order.isFilled()) {
                        orderBook.add(order);
                    }
                    return trades;

                case CANCEL:
                    orderBook.cancel(order.getId());
                    return List.of();

                case AMEND:
                    orderBook.amend(
                            order.getId(),
                            order.getPrice(),
                            order.getQuantity()
                    );
                    return List.of();

                default:
                    throw new IllegalStateException("Unknown order type");
            }
        } finally {
            lock.unlock();
        }
    }

    public OrderBook getOrderBook() {
        return orderBook;
    }

    public MatchingStrategy getMatchingStrategy() {
        return matchingStrategy;
    }
}
