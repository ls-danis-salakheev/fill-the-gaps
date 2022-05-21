package com.fillthegaps.study.salakheev.immutable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Collections.*;
import static java.util.concurrent.CompletableFuture.runAsync;

public class OrderService {

    private final ConcurrentHashMap<Long, Order> currentOrders = new ConcurrentHashMap<>();

    public long createOrder(List<Item> items) {
        Order order = new Order(items);
        currentOrders.put(order.getId(), order);
        return order.getId();
    }

    public void updatePaymentInfo(long cartId, PaymentInfo paymentInfo) {
        currentOrders.get(cartId).withPaymentInfo(paymentInfo, currentOrders);
        if (currentOrders.get(cartId).checkStatus()) {
            final Order order = currentOrders.get(cartId);
            runAsync(() -> deliver(order));
            order.withStatus(Status.DELIVERED, currentOrders);
        }
    }

    public void setPacked(long cartId) {
        currentOrders.get(cartId).withPacked(currentOrders);
        if (currentOrders.get(cartId).checkStatus()) {
            runAsync(() -> deliver(currentOrders.get(cartId)));
        }
    }

    private void deliver(Order order) {
        System.out.println("OK!");
    }

    public Map<Long, Order> getOrders() {
        return unmodifiableMap(currentOrders);
    }
}
