package com.orderMatchingEngine.domain;

public class PriceLevel {
    private Node head;
    private Node tail;

    public PriceLevel(Node head, Node tail) {
        this.head = head;
        this.tail = tail;
    }

    public Node getHead() {
        return head;
    }

    public void setHead(Node head) {
        this.head = head;
    }

    public Node getTail() {
        return tail;
    }

    public void setTail(Node tail) {
        this.tail = tail;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Node node = head;
        while (node != null) {
            sb.append(node.getOrder().toString());
            node = node.getNext();
        }

        return sb.toString();
    }
}
