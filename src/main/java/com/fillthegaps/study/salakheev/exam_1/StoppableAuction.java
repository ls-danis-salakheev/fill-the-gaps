package com.fillthegaps.study.salakheev.exam_1;

/**
 * @version 1.3
 */
public class StoppableAuction extends OptimisticAuction implements Proposing<OptimisticAuction.Bid> {

    private volatile boolean open;

    public StoppableAuction() {
        this.open = true;
    }

    @Override
    public boolean propose(Bid newBid) {
        return isOpen() && super.propose(newBid);
    }

    public synchronized void stopAuction() {
        this.open = false;
    }

    private synchronized boolean isOpen() {
        return this.open;
    }
}
