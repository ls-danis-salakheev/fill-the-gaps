package com.fillthegaps.study.salakheev.immutable;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class OrderService {

    private final ConcurrentHashMap<Long, Order> currentOrders = new ConcurrentHashMap<>();

    public long createOrder(List<Item> items) {
        Order order = new Order(items);
        currentOrders.put(order.getId(), order);
        return order.getId();
    }

    public void updatePaymentInfo(long cartId, PaymentInfo paymentInfo) {
        final Order order = currentOrders.get(cartId);
        order.setPaymentInfo(paymentInfo);
        if (order.checkStatus()) {
            deliver(order);
            order.setStatus(Status.DELIVERED);
        }
    }

    public void setPacked(long cartId) {
        final Order order = currentOrders.get(cartId);
        order.setPacked(true);
        if (order.checkStatus()) {
            deliver(order);
        }
    }

    private void deliver(Order order) {
    }
}
