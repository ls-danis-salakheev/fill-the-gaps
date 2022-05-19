package com.fillthegaps.study.salakheev.immutable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.concurrent.CompletableFuture.runAsync;

public class OrderService {

    private final ConcurrentHashMap<Long, Order> currentOrders = new ConcurrentHashMap<>();

    public long createOrder(List<Item> items) {
        Order order = new Order(items);
        currentOrders.put(order.getId(), order);
        return order.getId();
    }

    public void updatePaymentInfo(long cartId, PaymentInfo paymentInfo) {
        currentOrders
                .computeIfPresent(cartId, (c, o) -> {
                    Order paidOrder = o.withPaymentInfo(paymentInfo);
                    currentOrders.put(cartId, o.withPaymentInfo(paymentInfo));
                    if (paidOrder.checkStatus()) {
                        runAsync(() -> deliver(paidOrder));
                        Order delivered = paidOrder.withStatus(Status.DELIVERED);
                        currentOrders.put(delivered.getId(), delivered);
                    }
                    System.out.println("");
                    return paidOrder;
                });
    }

    public void setPacked(long cartId) {
        currentOrders
                .computeIfPresent(cartId, (c, o) -> {
                    final Order packedOrder = o.withPacked();
                    currentOrders.put(cartId, packedOrder);
                    if (packedOrder.checkStatus()) {
                        runAsync(() -> deliver(packedOrder));
                    }
                    return packedOrder;
                });
    }

    private void deliver(Order order) {
        System.out.println("OK!");
    }

    public Map<Long, Order> getOrders() {
        return Collections.unmodifiableMap(currentOrders);
    }
}
