package com.orderMatchingEngine.io;

import com.orderMatchingEngine.domain.Order;

import java.util.List;

public interface Reader {
    public List<Order> loadOrders(String filename);
}