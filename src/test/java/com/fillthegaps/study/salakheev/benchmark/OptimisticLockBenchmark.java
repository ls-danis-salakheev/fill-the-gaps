package com.fillthegaps.study.salakheev.benchmark;

import com.fillthegaps.study.salakheev.exam_1.Auction;
import com.fillthegaps.study.salakheev.exam_1.OptimisticAuction;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class OptimisticLockBenchmark extends AbstractBenchmark {

    final OptimisticAuction optimisticAuction = new OptimisticAuction();
    final OptimisticAuction.Bid bid = new OptimisticAuction.Bid(1L, 2L, 2000L);

    final Auction auction = new Auction();
    final Auction.Bid bid2 = new Auction.Bid(1L, 2L, 3000L);

    @Benchmark
    public void optimistic() {
        optimisticAuction.propose(bid);
    }

    @Benchmark
    public void synchronizedType() {
        auction.propose(bid2);
    }
}
