package com.fillthegaps.study.salakheev.immutable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.runAsync;

public class OrderService {
    private final ConcurrentHashMap<Long, Order> currentOrders = new ConcurrentHashMap<>();

    public long createOrder(List<Item> items) {
        Order order = new Order(items);
        currentOrders.put(order.getId(), order);
        return order.getId();
    }

    public void updatePaymentInfo(long cartId, PaymentInfo paymentInfo) {
        Order paidOrder = currentOrders
                .computeIfPresent(cartId, (id, order) -> {
                    order.setPaymentInfo(paymentInfo);
                    return order;
                });
        if (requireNonNull(paidOrder).checkStatus()) {
            runAsync(() -> deliver(paidOrder));
            Order delivered = paidOrder.withStatus(Status.DELIVERED);
            currentOrders.put(delivered.getId(), delivered);
        }
    }

    public void setPacked(long cartId) {
        Order packedOrder = currentOrders
                .computeIfPresent(cartId, (id, order) -> {
                    order.pack();
                    return order;
                });
        if (requireNonNull(packedOrder).checkStatus()) {
            runAsync(() -> deliver(packedOrder));
        }
    }

    private void deliver(Order order) {
        System.out.println("OK!");
    }

    public Map<Long, Order> getOrders() {
        return Collections.unmodifiableMap(currentOrders);
    }
}
