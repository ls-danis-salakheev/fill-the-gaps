package com.fillthegaps.study.salakheev;

import com.fillthegaps.study.salakheev.external.PriceRetriever;
import com.fillthegaps.study.salakheev.service.PriceAggregator;

public class FillTheGapsApplication {

    public static void main(String[] args) {
        PriceAggregator priceAggregator = new PriceAggregator(new PriceRetriever());
        long itemId = 12L;

        long start = System.currentTimeMillis();
        double min = priceAggregator.getMinPriceByAnyOf(itemId);
        long end = System.currentTimeMillis();

        System.out.println(min);
        System.out.println((end - start) < 3000); // should be true
    }
}
