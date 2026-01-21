package com.orderMatchingEngine.domain;

import java.time.Instant;
import java.util.UUID;

public class Order {
    private final String id;
    private final String symbol;
    private final Side side;
    private int price;
    private int quantity;
    private final long timestamp;
    private State state;
    private Node node;
    private OrderType orderType;

    private Order(Builder builder) {
        this.id = builder.id;
        this.symbol = builder.symbol;
        this.side = builder.side;
        this.price = builder.price;
        this.quantity = builder.quantity;
        this.timestamp = builder.timestamp;
        this.state = State.NEW;
        this.orderType = builder.orderType;
    }

    public boolean isBuy() {
        return side == Side.BUY;
    }

    public void reduceQuantity(double tradeQuantity) {
        if (quantity > 0 && tradeQuantity <= quantity) {
            this.state = State.PARTIALLY_FILLED;
            quantity -= tradeQuantity;
        }
    }

    public boolean isFilled() {
        if (quantity == 0) this.state = State.FILLED;
        return quantity == 0;
    }

    public String getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public Side getSide() {
        return side;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    @Override
    public String toString() {
        return "Order: " + this.id + " " + this.symbol + " " + this.side + " " + this.price + " " + this.quantity + " " + this.timestamp;
    }

    public static class Builder {
        private String id = UUID.randomUUID().toString();
        private String symbol;
        private Side side;
        private int price;
        private int quantity;
        private long timestamp = Instant.now().toEpochMilli();
        private OrderType orderType;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder symbol(String symbol) {
            this.symbol = symbol;
            return this;
        }

        public Builder side(Side side) {
            this.side = side;
            return this;
        }

        public Builder price(int price) {
            this.price = price;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder orderType(OrderType orderType) {
            this.orderType = orderType;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}
