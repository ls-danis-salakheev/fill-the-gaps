package com.fillthegaps.study.salakheev.service;

import com.fillthegaps.study.salakheev.exception.CalculationException;
import com.fillthegaps.study.salakheev.external.PriceRetriever;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.logging.Level.WARNING;

@Component
public class PriceAggregator {

    private static final Logger LOGGER = Logger.getLogger(PriceAggregator.class.getName());
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
        return CompletableFuture
                .allOf(cfs.toArray(CompletableFuture<?>[]::new))
                .completeOnTimeout(null, TIMEOUT_VALUE, MILLISECONDS)
                .thenApply(res -> cfs.stream()
                        .filter(CompletableFuture::isDone)
                        .map(CompletableFuture::join)
                        .reduce(Double::min)
                        .orElseThrow(() -> {
                            LOGGER.log(WARNING, "Ни один магазин не вернул результат для товара с id = {0}", itemId);
                            throw new CalculationException("Ошибка получения данных, повторите запрос позднее");
                        }))
                .join();
    }
}
