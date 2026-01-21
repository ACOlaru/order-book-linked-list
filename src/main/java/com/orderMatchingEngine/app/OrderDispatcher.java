package com.orderMatchingEngine.app;

import com.orderMatchingEngine.domain.Order;
import com.orderMatchingEngine.domain.Trade;
import com.orderMatchingEngine.services.MatchingEngine;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class OrderDispatcher {
    private final BlockingQueue<Order> queue = new LinkedBlockingQueue<>();
    private final MatchingEngine engine;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean running = true;

    public OrderDispatcher(MatchingEngine engine) {
        this.engine = engine;
    }

    public void start() {
        executor.submit(() -> {
            while (running) {
                try {
                    Order order = queue.take();
                    List<Trade> trades = engine.processOrder(order);

                    System.out.println("Processing order: " + order);

                    if (!trades.isEmpty()) {
                        System.out.println("Trades executed:");
                        printTrades(trades);
                    } else {
                        System.out.println("No trades found");
                    }

                    System.out.println(engine.getOrderBook());
                    System.out.println("-----------------------------------------");

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    private static void printTrades(List<Trade> trades) {
        for (Trade trade : trades) {
            System.out.println(trade.toString());
        }
    }

    public void submitOrder(Order order) {
        queue.offer(order);
    }

    public void shutdown() {
        running = false;
        executor.shutdownNow();
    }

    public boolean isIdle() {
        return queue.isEmpty();
    }
}