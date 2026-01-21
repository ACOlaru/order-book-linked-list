package com.orderMatchingEngine.app;

import com.orderMatchingEngine.domain.Order;
import com.orderMatchingEngine.io.CsvOrderReader;
import com.orderMatchingEngine.io.OrderFileReader;
import com.orderMatchingEngine.io.Reader;
import com.orderMatchingEngine.services.MatchingEngine;
import com.orderMatchingEngine.services.OrderBook;
import com.orderMatchingEngine.strategies.FifoMatchingStrategy;
import com.orderMatchingEngine.strategies.MatchingStrategy;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class MatchingEngineSimulator {

    public static void main( String[] args )
    {
        String filepath = "src/main/resources/orders.csv";

        MatchingStrategy strategy = new FifoMatchingStrategy();
        MatchingEngine engine = new MatchingEngine(new OrderBook(), strategy);
        OrderDispatcher dispatcher = new OrderDispatcher(engine);
        dispatcher.start();

        OrderFileReader reader = new OrderFileReader();
        List<Order> orders = null;
        try {
            orders = reader.read(Path.of("src/main/resources/orderFile.csv"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//
//        Reader reader = new CsvOrderReader();
//        List<Order> orders = reader.loadOrders(filepath);

        for (Order order : orders) {
            dispatcher.submitOrder(order);
        }


    }
}