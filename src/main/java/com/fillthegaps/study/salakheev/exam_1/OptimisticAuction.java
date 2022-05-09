package com.fillthegaps.study.salakheev.exam_1;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @version 1.2
 */
public class OptimisticAuction implements Proposing<OptimisticAuction.Bid> {

    public static class Bid {
        Long id; // ID заявки
        Long participantId; // ID участника
        Long price; // предложенная цена

        public Bid(Long id, Long participantId, Long price) {
            this.id = id;
            this.participantId = participantId;
            if (price == null) {
                throw new IllegalArgumentException("Поле цена в заявке обязательна!");
            }
            this.price = price;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Bid) {
                return this.price.longValue() == ((Bid) obj).price.longValue();
            }
            return false;
        }
    }

    public static class Notifier {
        public void sendOutdatedMessage(Bid bid) {
            CompletableFuture.runAsync(() -> {
                String notice = "Ваше заявка: " + bid + " принята";
                //            delay(2000L);
            });
        }
    }

    private final Notifier notifier;
    private final AtomicReference<Bid> atomicBidRef;

    public OptimisticAuction() {
        this.notifier = new Notifier();
        this.atomicBidRef = new AtomicReference<>(new Bid(null, null, 0L));
    }

    @Override
    public boolean propose(Bid newBid) {
        Bid old = atomicBidRef.get();
        if (getLatestBid().price > newBid.price
                && !atomicBidRef.compareAndSet(old, newBid)) {
            return false;
        }
        notifier.sendOutdatedMessage(old);
        return true;
    }

    public Bid getLatestBid() {
        return atomicBidRef.get();
    }
}
