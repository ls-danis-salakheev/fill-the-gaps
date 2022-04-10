package com.fillthegaps.study.salakheev.service;

import com.fillthegaps.study.salakheev.exception.CalculationException;
import com.fillthegaps.study.salakheev.external.PriceRetriever;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.logging.Level.WARNING;
import static java.util.stream.Collectors.toList;

@Component
public class PriceAggregator {

    private static final Logger LOGGER = Logger.getLogger(PriceAggregator.class.getName());
    private static final long TIMEOUT_VALUE = 3000L;
    private static final Set<Long> SHOP_IDS = Set.of(10L, 45L, 66L, 345L, 234L, 333L, 67L, 123L, 768L);

    private final PriceRetriever priceRetriever;

    public PriceAggregator(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    public double getMinPriceByReduce(long itemId) {
        List<CompletableFuture<Double>> futures = SHOP_IDS
                .stream()
                .map(shopId -> CompletableFuture
                        .supplyAsync(() -> priceRetriever
                                .getPrice(itemId, shopId))
                ).collect(toList());

        try {
            CompletableFuture
                    .anyOf(futures.toArray(new CompletableFuture[futures.size()]))
                    .get(TIMEOUT_VALUE, MILLISECONDS);
        } catch (Exception e) {
            LOGGER.log(WARNING, "Не удалось рассчитать среднюю стоимость для товара с id = {0}", itemId);
            throw new CalculationException("Ошибка получения данных");
        }

        return futures
                .stream()
                .filter(future -> future.isDone()
                        && !future.isCompletedExceptionally()
                )
                .map(CompletableFuture::join)
                .reduce(Double::min)
                .orElseThrow(() -> {
                    LOGGER.log(WARNING, "Не удалось рассчитать среднюю стоимость для товара с id = {0}", itemId);
                    throw new CalculationException("Ошибка получения данных, повторите запрос позднее");
                });
    }

    public double getMinPriceByAnyOf(long itemId) {
        try {
            return (Double) CompletableFuture
                    .anyOf(SHOP_IDS
                            .stream()
                            .map(shopId -> CompletableFuture
                                    .supplyAsync(() -> priceRetriever
                                            .getPrice(itemId, shopId))
                            ).toArray(CompletableFuture[]::new))
                    .orTimeout(TIMEOUT_VALUE, MILLISECONDS)
                    .join();
        } catch (Exception e) {
            LOGGER.log(WARNING, "Не удалось рассчитать среднюю стоимость для товара с id = {0}", itemId);
            throw new CalculationException("Ошибка получения данных");
        }
    }
}
