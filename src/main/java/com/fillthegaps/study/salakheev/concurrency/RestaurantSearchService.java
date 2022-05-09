package com.fillthegaps.study.salakheev.concurrency;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toUnmodifiableSet;

/**
 * @version 1.0
 */
public class RestaurantSearchService {

    private final ConcurrentHashMap<String, Long> stat = new ConcurrentHashMap<>();

    public Restaurant getByName(String restaurantName) {
        addToStat(restaurantName);
        return new Restaurant(restaurantName, stat.get(restaurantName));
    }

    public void addToStat(String restaurantName) {
        stat.compute(restaurantName, (name, count) -> count != null ? count + 1L : 1L);
    }

    public Set<String> printStat() {
        return stat.entrySet()
                .stream()
                .map(this::format)
                .collect(toUnmodifiableSet());
    }

    private String format(Map.Entry<String, Long> entry) {
        return String.format("%s - %d", entry.getKey(), entry.getValue());
    }
}

class Restaurant {

    private final String name;
    private final Long count;

    public Restaurant(String name, Long count) {
        this.name = name;
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Restaurant that = (Restaurant) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name + " - " + count;
    }
}