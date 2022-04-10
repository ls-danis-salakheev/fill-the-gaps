package com.fillthegaps.study.salakheev.external;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class PriceRetriever {

    public double getPrice(long itemId, long shopId) {
        // имитация долгого HTTP-запроса
        int delay = ThreadLocalRandom.current().nextInt(10);
        try {
            Thread.sleep(delay * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return ThreadLocalRandom.current().nextDouble(1000);
    }
}
