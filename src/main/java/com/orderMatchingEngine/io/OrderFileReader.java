package com.orderMatchingEngine.io;

import com.orderMatchingEngine.domain.Order;
import com.orderMatchingEngine.domain.OrderType;
import com.orderMatchingEngine.domain.Side;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class OrderFileReader {

    public List<Order> read(Path path) throws IOException {
        List<Order> orders = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(",", -1);
                OrderType type = OrderType.valueOf(parts[0].trim());
                long id = Long.parseLong(parts[1].trim());

                switch (type) {
                    case NEW -> {
                        Side side = Side.valueOf(parts[2].trim());
                        String symbol = parts[3].trim();
                        int price = Integer.parseInt(parts[4].trim());
                        int qty = Integer.parseInt(parts[5].trim());
                        Order o = new Order.Builder()
                                .id(String.valueOf(id))
                                .orderType(type)
                                .symbol(symbol)
                                .side(side)
                                .price(price)
                                .quantity(qty)
                                .build();
                        orders.add(o);
                    }
                    case AMEND -> {
                        Integer newPrice = !parts[2].isBlank() ? Integer.parseInt(parts[2]) : null;
                        Integer newQty = !parts[3].isBlank() ? Integer.parseInt(parts[3]) : null;
                        Order o = new Order.Builder()
                                .id(String.valueOf(id))
                                .orderType(type)
                                .price(newPrice != null ? newPrice : 0)
                                .quantity(newQty != null ? newQty : 0)
                                .build();
                        orders.add(o);
                    }
                    case CANCEL -> {
                        Order o = new Order.Builder()
                                .id(String.valueOf(id))
                                .orderType(type)
                                .build();
                        orders.add(o);
                    }
                }
            }
        }

        return orders;
    }
}
