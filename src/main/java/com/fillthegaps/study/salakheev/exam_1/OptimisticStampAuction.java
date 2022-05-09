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
        OptimisticAuction.Bid old;
        try {
            old = this.lastBid;
            if (old.price > newBid.price) {
                return false;
            }
            this.lastBid = newBid;
        } finally {
            stampedLock.unlockWrite(writeStamp);
        }
        notifier.sendOutdatedMessage(old);
        return true;
    }

    public OptimisticAuction.Bid getLatestBid() {
        return lastBid;
    }
}
