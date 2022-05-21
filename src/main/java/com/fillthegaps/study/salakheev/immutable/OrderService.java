package com.fillthegaps.study.salakheev.immutable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Collections.unmodifiableMap;
import static java.util.concurrent.CompletableFuture.runAsync;

public class OrderService {

    private final ConcurrentHashMap<Long, Order> currentOrders = new ConcurrentHashMap<>();

    public long createOrder(List<Item> items) {
        Order order = new Order(items);
        currentOrders.put(order.getId(), order);
        return order.getId();
    }

    public void updatePaymentInfo(long cartId, PaymentInfo paymentInfo) {
        final Order paid = currentOrders
                .computeIfPresent(cartId, (id, order) -> order.withPaymentInfo(paymentInfo));
        if (paid.checkStatus()) {
            runAsync(() -> deliver(paid));
            currentOrders.put(cartId, paid.withStatus(Status.DELIVERED));
        }
    }

    public void setPacked(long cartId) {
        final Order packed = currentOrders
                .computeIfPresent(cartId, (id, order) -> order.doPack());
        if (packed.checkStatus()) {
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
