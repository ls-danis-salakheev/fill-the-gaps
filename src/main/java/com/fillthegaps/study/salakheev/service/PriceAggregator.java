package com.fillthegaps.study.salakheev.service;

import com.fillthegaps.study.salakheev.external.PriceRetriever;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toList;

@Component
public class PriceAggregator {

    private static final long TIMEOUT_VALUE = 2950L;
    private static final Set<Long> SHOP_IDS = Set.of(10L, 45L, 66L, 345L, 234L, 333L, 67L, 123L, 768L);

    private final PriceRetriever priceRetriever;

    public PriceAggregator(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    public double getMinPrice(long itemId) {
        List<CompletableFuture<Double>> cfs = SHOP_IDS
                .stream()
                .map(shopId -> CompletableFuture
                        .supplyAsync(() -> priceRetriever.getPrice(itemId, shopId))
                ).collect(Collectors.toUnmodifiableList());

        CompletableFuture<List<Double>> cfList = CompletableFuture
                .allOf(cfs.toArray(CompletableFuture<?>[]::new))
                .completeOnTimeout(null, TIMEOUT_VALUE, MILLISECONDS)
                .thenApply(res -> cfs.stream()
                        .filter(CompletableFuture::isDone)
                        .map(CompletableFuture::join)
                        .collect(toList()));
        List<Double> resultList = cfList.join();
        System.out.println(resultList);
        return Collections.min(resultList);
    }
}
