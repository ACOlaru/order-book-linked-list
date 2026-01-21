# Java Order Matching Engine (Strategy-Based)

## Overview

This project is a **modular, extensible order matching engine** written in Java, designed to simulate how modern electronic exchanges match buy and sell orders.

The engine is intentionally built around a **pluggable matching strategy architecture**, making it easy to add new matching algorithms (e.g. price–time priority, pro-rata, auction-style matching) without rewriting the core order book or execution logic.

This repository represents **Version 2** of the engine, evolving from a simpler implementation into a more realistic, exchange-style design using explicit order nodes and price levels.

---

## Key Design Goals

- Strategy-based matching (Open/Closed Principle)
- Clear separation of concerns
- Efficient order traversal and removal
- Deterministic execution flow
- Extensible order lifecycle support

---

## Current Features

### Implemented

- Limit order matching
- Price–time priority strategy
- Order book with price levels
- Doubly linked node structure (FIFO per price)
- Partial and fulfills
- Trade generation
- Single-threaded dispatcher
- Order states:
    - `NEW`
    - `PARTIALLY_FILLED`
    - `FILLED`
    - `CANCELLED`
    - `AMEND`

---

## Matching Strategy Architecture

Matching behavior is **decoupled** from the engine.

```java
public interface MatchingStrategy {
    List<Trade> match(Order incomingOrder, OrderBook orderBook);
}
````

### Current Strategy

* Price-Time Priority
 
  * Best price first

  * FIFO within each price level

### Adding New Strategies

* New strategies can be added by implementing the MatchingStrategy interface:
  * Pro-rata matching
  * Auction-style matching
  * Iceberg-aware matching
  * Custom venue rules

No changes to the order book or dispatcher are required.

## Order Book Design

* Orders are grouped into price levels
* Each price level maintains a doubly linked list of order nodes
* Each Order holds a reference to its node for:
  * O(1) cancellation
  * O(1) amendment
  * Efficient removal after fills

## Input & Simulation

* Orders are currently read from a file
* Orders are processed sequentially via a dispatcher
* Designed for future support of:
  * real-time feeds
  * market data replay
  * concurrent ingestion
