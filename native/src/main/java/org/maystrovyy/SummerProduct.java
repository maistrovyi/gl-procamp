package org.maystrovyy;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

public interface ProductInfo extends Serializable {

    BigDecimal price();

    @Nullable
    String originCountry();

    BigDecimal ratingCount();

    BigDecimal ratingFiveCount();

    record DefaultProductInfo(
            BigDecimal price,
            String originCountry,
            BigDecimal ratingCount,
            BigDecimal ratingFiveCount
    ) implements ProductInfo {  }

    interface ProductInfoMapper {

        org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ProductInfoMapper.class);

        static Optional<ProductInfo> mapIfPossible(String[] csvValues) {
            if (csvValues[2].isBlank() || csvValues[8].isBlank() || csvValues[9].isBlank()) {
                return Optional.empty();
            }
            try {
                return Optional.of(
                        new DefaultProductInfo(
                                new BigDecimal(csvValues[2].trim()),
                                csvValues[29].trim(),
                                new BigDecimal(csvValues[8].trim()),
                                new BigDecimal(csvValues[9].trim())
                        )
                );
            } catch (Exception ex) {
                LOG.warn("Unable to map product from line: '{}' exception: '{}'", Arrays.toString(csvValues), ex.toString());
                return Optional.empty();
            }
        }

    }

}