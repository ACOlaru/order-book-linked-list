package com.orderMatchingEngine.domain;

public class Node {
    private final Order order;
    private Node prev;
    private Node next;

    public Node(Order order) {
        this.order = order;
        this.prev = null;
        this.next = null;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public Order getOrder() {
        return order;
    }

    public Node getPrev() {
        return prev;
    }

    public Node getNext() {
        return next;
    }
}
