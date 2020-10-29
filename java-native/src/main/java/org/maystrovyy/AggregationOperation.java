package org.maystrovyy;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ZERO;
import static java.math.MathContext.DECIMAL64;
import static java.util.stream.Collectors.*;

public interface AggregationOperation<K, T, V> {

    Map<K, V> agg(Collection<T> collection);

    class SummerProductsAggregation implements AggregationOperation<String, SummerProduct, SummerProductsAggregation.Tuple> {

        public static record Tuple(BigDecimal avg, BigDecimal fivePercentage) {  }

        public static class BlankStringLatestComparator implements Comparator<String> {
            public int compare(String s1, String s2) {
                if (Objects.nonNull(s1) && Objects.nonNull(s2)) {
                    if (s1.isBlank() || s2.isBlank()) {
                        return -1;
                    }
                    return s1.compareTo(s2);
                }
                return Objects.isNull(s1) ? 1 : -1;
            }
        }

        @Override
        public Map<String, Tuple> agg(Collection<SummerProduct> collection) {
            return collection.stream()
                    .collect(
                            groupingBy(SummerProduct::originCountry,
                                    collectingAndThen(Collectors.toList(), productsPerCountry -> {
                                        var avgPrice = avgPrice(productsPerCountry);
                                        var ratingCountSum = sum(productsPerCountry, it -> it.ratingCount().doubleValue());
                                        var ratingFiveSum = sum(productsPerCountry, it -> it.ratingFiveCount().doubleValue());
                                        var fivePercentage = calc5Percentage(ratingCountSum, ratingFiveSum);
                                        return new Tuple(avgPrice, fivePercentage);
                                    })))
                    .entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey(new BlankStringLatestComparator()))
                    .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (t1, t2) -> t2, LinkedHashMap::new));
        }

        private BigDecimal avgPrice(Collection<SummerProduct> productsPerCountry) {
            return BigDecimal.valueOf(productsPerCountry.stream().collect(averagingDouble(it -> it.price().doubleValue())));
        }

        private BigDecimal sum(Collection<SummerProduct> productsPerCountry, ToDoubleFunction<SummerProduct> summingProperty) {
            return BigDecimal.valueOf(productsPerCountry.stream().mapToDouble(summingProperty).sum());
        }

        private BigDecimal calc5Percentage(BigDecimal ratingCountSum, BigDecimal ratingFiveSum) {
            return ratingFiveSum.compareTo(ZERO) == 0
                    ? ZERO
                    : ratingFiveSum.divide(ratingCountSum, DECIMAL64).multiply(BigDecimal.valueOf(100), DECIMAL64);
        }

    }

}