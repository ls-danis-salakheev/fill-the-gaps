package com.fillthegaps.study.salakheev.cf;

import com.fillthegaps.study.salakheev.exception.CalculationException;
import com.fillthegaps.study.salakheev.external.PriceRetriever;
import com.fillthegaps.study.salakheev.service.PriceAggregator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static java.math.BigDecimal.ZERO;
import static java.time.Duration.ofMillis;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PriceAggregatorTest {

    static List<Long> shopIds;
    static long DUMMY_ID = 1L;

    @InjectMocks
    PriceAggregator priceAggregator;

    @Mock
    PriceRetriever priceRetriever;

    @BeforeAll
    static void setUpBefore() {
        shopIds = LongStream.iterate(0L, l -> l + 1).limit(10).boxed().collect(Collectors.toUnmodifiableList());
    }

    @Test
    void shouldReturnMinimalPriceGreaterThan0() {
        when(priceRetriever.getPrice(DUMMY_ID, shopIds.get(0))).thenAnswer(invocation -> {
            Thread.sleep(4000L);
            return 3.3;
        });
        when(priceRetriever.getPrice(DUMMY_ID, shopIds.get(1))).thenReturn(10.4);
        when(priceRetriever.getPrice(DUMMY_ID, shopIds.get(2))).thenReturn(11.2);
        when(priceRetriever.getPrice(DUMMY_ID, shopIds.get(3))).thenReturn(12.4);
        when(priceRetriever.getPrice(DUMMY_ID, shopIds.get(4))).thenReturn(13.4);
        when(priceRetriever.getPrice(DUMMY_ID, shopIds.get(5))).thenReturn(14.2);
        when(priceRetriever.getPrice(DUMMY_ID, shopIds.get(6))).thenReturn(15.2);
        when(priceRetriever.getPrice(DUMMY_ID, shopIds.get(7))).thenReturn(6.3);
        when(priceRetriever.getPrice(DUMMY_ID, shopIds.get(8))).thenReturn(17.2);
        when(priceRetriever.getPrice(DUMMY_ID, shopIds.get(9))).thenReturn(18.3);

        double minPrice = assertTimeout(ofMillis(4000L), () -> priceAggregator.getMinPriceByReduce(1L));
        assertThat(minPrice).isGreaterThan(ZERO.doubleValue());
        System.out.println(minPrice);
    }

    @Test
    void shouldReturnKnownMinimalPrice() {
        when(priceRetriever.getPrice(DUMMY_ID, shopIds.get(0))).thenAnswer(invocation -> {
            Thread.sleep(4000L);
            return 33.3;
        });
        when(priceRetriever.getPrice(DUMMY_ID, shopIds.get(1))).thenAnswer(invocation -> {
            Thread.sleep(4000L);
            return 31.3;
        });
        when(priceRetriever.getPrice(DUMMY_ID, shopIds.get(2))).thenAnswer(invocation -> {
            Thread.sleep(4000L);
            return 30.3;
        });
        when(priceRetriever.getPrice(DUMMY_ID, shopIds.get(3))).thenAnswer(invocation -> 13.3);
        when(priceRetriever.getPrice(DUMMY_ID, shopIds.get(4))).thenAnswer(invocation -> {
            Thread.sleep(4000L);
            return 23.3;
        });
        when(priceRetriever.getPrice(DUMMY_ID, shopIds.get(5))).thenAnswer(invocation -> {
            Thread.sleep(4000L);
            return 33.3;
        });
        when(priceRetriever.getPrice(DUMMY_ID, shopIds.get(6))).thenAnswer(invocation -> {
            Thread.sleep(4000L);
            return 53.3;
        });
        when(priceRetriever.getPrice(DUMMY_ID, shopIds.get(7))).thenAnswer(invocation -> {
            Thread.sleep(4000L);
            return 63.3;
        });
        when(priceRetriever.getPrice(DUMMY_ID, shopIds.get(8))).thenAnswer(invocation -> {
            Thread.sleep(4000L);
            return 93.3;
        });
        when(priceRetriever.getPrice(DUMMY_ID, shopIds.get(9))).thenAnswer(invocation -> {
            Thread.sleep(4000L);
            return 83.3;
        });

        double minPrice = priceAggregator.getMinPriceByAnyOf(1L);
        assertThat(minPrice).isGreaterThan(ZERO.doubleValue());
        assertThat(minPrice).isEqualTo(13.3);
        System.out.println(minPrice);
    }

    @Test
    void shouldThrowExceptionByTimeout() {
        when(priceRetriever.getPrice(DUMMY_ID, shopIds.get(0))).thenAnswer(invocation -> {
            Thread.sleep(4000L);
            return 33.3;
        });
        when(priceRetriever.getPrice(DUMMY_ID, shopIds.get(1))).thenAnswer(invocation -> {
            Thread.sleep(4000L);
            return 31.3;
        });
        when(priceRetriever.getPrice(DUMMY_ID, shopIds.get(2))).thenAnswer(invocation -> {
            Thread.sleep(4000L);
            return 30.3;
        });
        when(priceRetriever.getPrice(DUMMY_ID, shopIds.get(3))).thenAnswer(invocation -> {
            Thread.sleep(4000L);
            return 13.3;
        });
        when(priceRetriever.getPrice(DUMMY_ID, shopIds.get(4))).thenAnswer(invocation -> {
            Thread.sleep(4000L);
            return 23.3;
        });
        when(priceRetriever.getPrice(DUMMY_ID, shopIds.get(5))).thenAnswer(invocation -> {
            Thread.sleep(4000L);
            return 33.3;
        });
        when(priceRetriever.getPrice(DUMMY_ID, shopIds.get(6))).thenAnswer(invocation -> {
            Thread.sleep(4000L);
            return 53.3;
        });
        when(priceRetriever.getPrice(DUMMY_ID, shopIds.get(7))).thenAnswer(invocation -> {
            Thread.sleep(4000L);
            return 63.3;
        });
        when(priceRetriever.getPrice(DUMMY_ID, shopIds.get(8))).thenAnswer(invocation -> {
            Thread.sleep(4000L);
            return 93.3;
        });
        when(priceRetriever.getPrice(DUMMY_ID, shopIds.get(9))).thenAnswer(invocation -> {
            Thread.sleep(4000L);
            return 83.3;
        });
        assertThrows(CalculationException.class, () -> priceAggregator.getMinPriceByReduce(1L));
    }
}
