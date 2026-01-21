package com.orderMatchingEngine.services;

import com.orderMatchingEngine.domain.*;

import java.time.Instant;
import java.util.*;

public class OrderBook {
    // Bids = BUY => highest price first
    private final TreeMap<Integer, PriceLevel> bids = new TreeMap<>(Collections.reverseOrder());

    // Asks =  SELL => lowest price first
    private final TreeMap<Integer, PriceLevel> asks = new TreeMap<>();

    private final Map<String, Order> orders = new HashMap<>();

    public void add(Order order) {
        NavigableMap<Integer, PriceLevel> map = order.isBuy() ? bids : asks;
        PriceLevel priceLevel = map.get(order.getPrice());

        if (priceLevel == null) {
            Node node = new Node(order);
            order.setNode(node);
            priceLevel = new PriceLevel(node, node);
            map.put(order.getPrice(), priceLevel);
        }  else {
            Node node = new Node(order);
            node.setPrev(priceLevel.getTail());
            priceLevel.getTail().setNext(node);
            priceLevel.setTail(node);
            order.setNode(node);
        }

        orders.put(order.getId(), order);
        order.setState(State.NEW);
    }

    public void cancel(String orderId) {
        Optional<Order> order = findOrder(orderId);
        if (order.isEmpty()) {
            return;
        }

        if (order.get().getState() == State.CANCELLED || order.get().getState() == State.FILLED) {
            return;
        }

        removeOrderNode(order.get());
        order.get().setNode(null);
        order.get().setState(State.CANCELLED);
        removeOrder(orderId);
    }

    public void removeOrder(String orderId) {
        orders.remove(orderId);
    }

    public void removeOrderNode(Order order) {
        NavigableMap<Integer, PriceLevel> map = order.isBuy() ? bids : asks;
        PriceLevel priceLevel = map.get(order.getPrice());
        if (priceLevel == null) {
            return;
        }

        Node node = order.getNode();

        if (node.getPrev() == null) {
            priceLevel.setHead(node.getNext());
        } else {
            node.getPrev().setNext(node.getNext());
        }

        if (node.getNext() == null) {
            priceLevel.setTail(node.getPrev());
        } else {
            node.getNext().setPrev(node.getPrev());
        }

        if (priceLevel.getTail() == null && priceLevel.getHead() == null) {
            map.remove(order.getPrice());
        }
    }

    private Optional<Order> findOrder(String orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

    public void amend(String orderId, Integer newPrice, Integer newQuantity) {
        Order order = orders.get(orderId);
        if (order == null) return;
        if (order.getState() != State.NEW) return;

        boolean priceChanged = newPrice != null && newPrice != order.getPrice();
        boolean qtyChanged   = newQuantity != null && newQuantity != order.getQuantity();

        if (priceChanged) {
            removeOrderNode(order);
            order.setPrice(newPrice);
            if (newQuantity != null && newQuantity <= 0) {
                cancel(orderId);
                return;
            }

            add(order);
            return;
        }

        if (qtyChanged) {
            if (newQuantity < order.getQuantity()) {
                order.setQuantity(newQuantity);
            } else {
                if (newQuantity > order.getQuantity()) {
                    removeOrderNode(order);
                    order.setQuantity(newQuantity);
                    add(order);
                }
            }
        }
    }


    public Optional<Integer> getBestBid() {
        return bids.isEmpty()? Optional.empty() : Optional.of(bids.firstKey());
    }

    public Optional<Integer> getBestAsk() {
        return asks.isEmpty()? Optional.empty() : Optional.of(asks.firstKey());
    }

    public NavigableMap<Integer, PriceLevel> getBids() {
        return Collections.unmodifiableNavigableMap(bids);
    }

    public NavigableMap<Integer, PriceLevel> getAsks() {
        return Collections.unmodifiableNavigableMap(asks);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("OrderBook [bids=");
        for (Map.Entry<Integer, PriceLevel> entry : bids.entrySet()) {
            builder.append(entry.getKey()).append(":").append(entry.getValue().toString()).append("\n");
        }

        builder.append("asks=");
        for (Map.Entry<Integer, PriceLevel> entry : asks.entrySet()) {
            builder.append(entry.getKey()).append(":").append(entry.getValue().toString()).append("\n");
        }

        return builder.toString();
    }

    public Trade executeTrade(Order incomingOrder, Order restingOrder, double tradeQuantity) {
        int tradePrice = restingOrder.getPrice();
        String buyerOrderId = incomingOrder.isBuy() ? incomingOrder.getId() : restingOrder.getId();
        String sellerOrderId = incomingOrder.isBuy() ? restingOrder.getId() : incomingOrder.getId();
        incomingOrder.reduceQuantity(tradeQuantity);
        restingOrder.reduceQuantity(tradeQuantity);
        if (restingOrder.isFilled()) {
            removeOrderNode(restingOrder);
            removeOrder(restingOrder.getId());
            restingOrder.setState(State.FILLED);
            restingOrder.setNode(null);
        }

        if (incomingOrder.isFilled()) {
            incomingOrder.setState(State.FILLED);
        }

        return new Trade(
                TradeIdGenerator.getNextId(),
                buyerOrderId,
                sellerOrderId,
                incomingOrder.getSymbol(),
                tradePrice,
                tradeQuantity,
                Instant.now().toEpochMilli()
        );
    }
}
