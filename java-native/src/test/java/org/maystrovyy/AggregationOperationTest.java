package org.maystrovyy;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AggregationOperationTest {

    @Test
    void agg() throws IOException {
        var csvFile = Paths.get("src/test/resources/test-task_dataset_summer_products.csv").toFile();

        Collection<SummerProduct> products = new DatasetProvider.SimpleProductsProvider().provide(csvFile);
        var result = new AggregationOperation.SummerProductsAggregation().agg(products);

        assertEquals(7, result.size());
    }

}