package com.fillthegaps.study.salakheev.exam_1;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @version 1.4
 */
public class StoppableAuction implements Proposing<OptimisticAuction.Bid> {

    private volatile boolean open;
    private final OptimisticAuction.Notifier notifier;
    private final AtomicReference<OptimisticAuction.Bid> atomicBidRef;

    public StoppableAuction() {
        this.open = true;
        this.notifier = new OptimisticAuction.Notifier();
        this.atomicBidRef = new AtomicReference<>(new OptimisticAuction.Bid(null, null, 0L));
    }

    @Override
    public boolean propose(OptimisticAuction.Bid newBid) {
        if (!open) return false;
        OptimisticAuction.Bid old = atomicBidRef.get();
        if (getLatestBid().price > newBid.price
                && !compareAndSet(newBid, old)) {
            return false;
        }
        notifier.sendOutdatedMessage(old);
        return true;
    }

    private boolean compareAndSet(OptimisticAuction.Bid newBid, OptimisticAuction.Bid old) {
        return open && atomicBidRef.compareAndSet(old, newBid);
    }

    public OptimisticAuction.Bid getLatestBid() {
        return atomicBidRef.get();
    }

    public void stopAuction() {
        this.open = false;
    }
}
