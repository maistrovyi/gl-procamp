package org.maystrovyy;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static java.math.BigDecimal.ZERO;

public interface SummerProduct extends Serializable {

    BigDecimal price();

    String originCountry();

    BigDecimal ratingCount();

    BigDecimal ratingFiveCount();

    record SimpleSummerProduct(BigDecimal price, String originCountry, BigDecimal ratingCount, BigDecimal ratingFiveCount) implements SummerProduct {  }

    interface SummerProductMapper {

        static Optional<SummerProduct> mapIfPossible(String[] csvValues) {
            if (csvValues[2].isBlank()) {
                System.err.printf("Unable to map product from line: '%s' exception: 'price should be not blank'%n", Arrays.toString(csvValues));
                return Optional.empty();
            }
            try {
                return Optional.of(
                        new SimpleSummerProduct(
                                new BigDecimal(csvValues[2].trim()),
                                csvValues[29].trim(),
                                csvValues[8].isBlank() ? ZERO : new BigDecimal(csvValues[8].trim()),
                                csvValues[9].isBlank() ? ZERO : new BigDecimal(csvValues[9].trim())
                        )
                );
            } catch (Exception ex) {
                System.err.printf("Unable to map product from line: '%s' exception: '%s'%n", Arrays.toString(csvValues), ex.toString());
                return Optional.empty();
            }
        }

    }

}