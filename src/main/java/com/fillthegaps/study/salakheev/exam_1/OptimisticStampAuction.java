package com.fillthegaps.study.salakheev.exam_1;

import java.util.concurrent.locks.StampedLock;

/**
 * @version 1.1
 */
public class OptimisticStampAuction implements Proposing<OptimisticAuction.Bid> {

    private final StampedLock stampedLock;
    private volatile OptimisticAuction.Bid lastBid;
    private final OptimisticAuction.Notifier notifier;

    public OptimisticStampAuction() {
        this.stampedLock = new StampedLock();
        this.notifier = new OptimisticAuction.Notifier();
        this.lastBid = new OptimisticAuction.Bid(null, null, 0L);
    }

    @Override
    public boolean propose(OptimisticAuction.Bid newBid) {
        long stamp = stampedLock.tryOptimisticRead();
        if (!stampedLock.validate(stamp) && this.lastBid.price > newBid.price) {
            return false;
        }
        return writeLastBid(newBid);
    }

    private boolean writeLastBid(OptimisticAuction.Bid newBid) {
        long writeStamp = stampedLock.writeLock();
        boolean res = false;
        try {
            final var old = this.lastBid;
            if (newBid.price > old.price) {
                this.lastBid = newBid;
                notifier.sendOutdatedMessage(old);
                res = true;
            }
        } finally {
            stampedLock.unlockWrite(writeStamp);
        }
        return res;
    }

    public OptimisticAuction.Bid getLatestBid() {
        return lastBid;
    }

}
