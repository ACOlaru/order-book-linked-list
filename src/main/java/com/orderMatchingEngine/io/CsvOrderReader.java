package com.orderMatchingEngine.io;

import com.orderMatchingEngine.domain.Order;
import com.orderMatchingEngine.domain.OrderType;
import com.orderMatchingEngine.domain.Side;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CsvOrderReader implements Reader{
    @Override
    public List<Order> loadOrders(String filePath) {
        List<Order> orders = new ArrayList<>();

        try (Stream<String> lines = Files.lines(Path.of(filePath))) {
            orders = lines
                    .skip(1)
                    .map(this::parseOrder)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            System.err.println("❌ Error reading file: " + filePath + " - " + e.getMessage());
        }

        return orders;
    }

    private Order parseOrder(String line) {
        try {
            String[] parts = line.split(",");

            if (parts.length < 4) {
                System.err.println("Skipping malformed line: " + line);
                return null;
            }

            String symbol = parts[0].trim();
            Side action = Side.valueOf(parts[1].trim().toUpperCase());
            int price = Integer.parseInt(parts[2].trim());
            int quantity = Integer.parseInt(parts[3].trim());

            return new Order.Builder()
                    .symbol(symbol)
                    .side(action)
                    .price(price)
                    .quantity(quantity)
                    .orderType(OrderType.NEW)
                    .build();

        } catch (Exception e) {
            System.err.println("Skipping malformed line: " + line);
        }

        return null;
    }
}