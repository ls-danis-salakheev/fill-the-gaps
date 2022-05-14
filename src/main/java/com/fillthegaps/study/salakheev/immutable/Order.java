package com.fillthegaps.study.salakheev.immutable;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.util.List;
import java.util.concurrent.atomic.LongAdder;

import static java.util.Collections.unmodifiableList;

@Getter
@Setter
public class Order {

    private static final LongAdder nextId = new LongAdder();
    private final Long id;
    private final List<Item> items;
    private volatile PaymentInfo paymentInfo;
    private volatile boolean isPacked;
    private volatile Status status;

    public Order(List<Item> items) {
        this.items = items;
        this.status = Status.CREATED;
        nextId.increment();
        this.id = nextId.longValue();
    }

    public boolean checkStatus() {
        if (items != null && !items.isEmpty() && paymentInfo != null && isPacked) {
            status = Status.DELIVERED;
            return true;
        }
        return false;
    }

    public List<Item> getItems() {
        return unmodifiableList(items);
    }
}

@Value
class Item {
}

@Value
class PaymentInfo {
}

enum Status {
    CREATED,
    DELIVERED // etc
}
