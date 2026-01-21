package com.orderMatchingEngine.strategies;

import com.orderMatchingEngine.domain.*;
import com.orderMatchingEngine.services.OrderBook;

import java.time.Instant;
import java.util.*;

public class FifoMatchingStrategy implements MatchingStrategy {

    @Override
    public List<Trade> match(Order orderIncoming, OrderBook orderBook) {
        NavigableMap<Integer, PriceLevel> map = orderIncoming.isBuy() ? orderBook.getBids() : orderBook.getAsks();
        return matchOrder(orderIncoming, map, orderBook);
    }

    private List<Trade> matchOrder(Order orderIncoming, NavigableMap<Integer, PriceLevel> map, OrderBook orderBook) {
        List<Trade> trades = new ArrayList<>();
        Iterator<Map.Entry<Integer, PriceLevel>> it = map.entrySet().iterator();
        while (it.hasNext() && !orderIncoming.isFilled()) {
            Map.Entry<Integer, PriceLevel> entry = it.next();
            Integer price = entry.getKey();

            if (orderIncoming.isBuy() && price > orderIncoming.getPrice()) break;
            if (!orderIncoming.isBuy() && price < orderIncoming.getPrice()) break;

            PriceLevel orders = entry.getValue();
            Node node = orders.getHead();
            while (node != null && !orderIncoming.isFilled()) {
                Order order = node.getOrder();
                if (order == null) break;

                double tradeQuantity = Math.min(orderIncoming.getQuantity(), order.getQuantity());
                Node next = node.getNext();
                trades.add(orderBook.executeTrade(orderIncoming, order, tradeQuantity));
                if (order.isFilled()) {
                    node = next;
                } else {
                    break;
                }
            }
        }

        return trades;
    }
}
