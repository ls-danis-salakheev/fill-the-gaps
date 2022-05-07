package com.fillthegaps.study.salakheev.exam_1;

import org.springframework.data.util.Pair;

import java.util.concurrent.CompletableFuture;

/**
 * @version 1.3
 */
public class Auction {
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
    }

    public static class Notifier {
        public void sendOutdatedMessage(Bid bid) {
            CompletableFuture.runAsync(() -> {
                String notice = "Ваше заявка: " + bid + " снята с аукциона";
                //            delay(2000L);
            });
        }
    }

    private final Notifier notifier;
    private volatile Bid latestBid;

    public Auction() {
        this.notifier = new Notifier();
        this.latestBid = new Bid(null, null, 0L);
    }

    public boolean propose(Bid bid) {
        Pair<Boolean, Bid> result = checkCandidateAndSet(bid);
        if (result.getFirst()) {
            notifier.sendOutdatedMessage(result.getSecond());
            return true;
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid;
    }

    private synchronized Pair<Boolean, Bid> checkCandidateAndSet(Bid bid) {
        boolean condition = bid.price > this.latestBid.price;
        if (condition) {
            this.latestBid = bid;
        }
        return Pair.of(condition, this.latestBid);
    }
}
