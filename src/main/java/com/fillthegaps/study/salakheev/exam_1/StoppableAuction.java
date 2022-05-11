package com.fillthegaps.study.salakheev.exam_1;

import java.util.concurrent.atomic.AtomicMarkableReference;

/**
 * @version 1.5
 */
public class StoppableAuction implements Proposing<OptimisticAuction.Bid> {

    private final OptimisticAuction.Notifier notifier;
    private final AtomicMarkableReference<OptimisticAuction.Bid> markableReference;

    public StoppableAuction() {
        this.notifier = new OptimisticAuction.Notifier();
        this.markableReference = new AtomicMarkableReference<>(new OptimisticAuction.Bid(null, null, 0L), true);
    }

    @Override
    public boolean propose(OptimisticAuction.Bid newBid) {
        if (!markableReference.isMarked()) return false;
        OptimisticAuction.Bid old = markableReference.getReference();
        if (!markableReference.compareAndSet(old, newBid, true, true)) {
            return false;
        }
        notifier.sendOutdatedMessage(old);
        return true;
    }

    public OptimisticAuction.Bid getLatestBid() {
        return markableReference.getReference();
    }

    public void stopAuction() {
        OptimisticAuction.Bid last;
        do {
            last = markableReference.getReference();
        } while (markableReference.compareAndSet(last, last, true, false));
    }
}
