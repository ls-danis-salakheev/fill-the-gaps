package com.fillthegaps.study.salakheev.exam_1;

/**
 * @version 1.2
 */
public class StoppableAuction extends OptimisticAuction {

    private volatile boolean open;

    public StoppableAuction() {
        this.open = true;
    }

    @Override
    public boolean propose(Bid newBid) {
        if (!open) return false;
        return super.propose(newBid);
    }

    public void stopAuction() {
        this.open = false;
    }
}
