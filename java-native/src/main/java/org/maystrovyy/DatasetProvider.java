package org.maystrovyy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.maystrovyy.SummerProduct.*;

public interface DatasetProvider<T> {

    Collection<T> provide(File file) throws IOException;

    final class SimpleProductsProvider implements DatasetProvider<SummerProduct> {

        private static final int DELIM_LIMIT = -1;
        private static final int CSV_FIELDS_AMOUNT = 43;
        private static final String REPLACEMENT_WORD = "$1";
        private static final boolean VALID_CSV_LINE_DATA = TRUE;
        private static final boolean INVALID_CSV_LINE_DATA = FALSE;
        private static final String VALID_CSV_LINE_TAIL = ",2020-08";
        private static final String BRACKETS_CLEANER_REGEX = "\\[(.*?)\\]";
        private static final String DELIMITER = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

        private static final Predicate<String[]> CSV_LINE_VALIDITY_PREDICATE = csvValues -> csvValues.length == CSV_FIELDS_AMOUNT;

        @Override
        public Collection<SummerProduct> provide(File file) throws IOException {
            var dataset = Files.readAllLines(file.toPath()).stream()
                    .skip(1)
                    .map(line -> line.split(DELIMITER, DELIM_LIMIT))
                    .collect(
                            Collectors.teeing(
                                    Collectors.filtering(CSV_LINE_VALIDITY_PREDICATE, Collectors.toList()),
                                    Collectors.filtering(Predicate.not(CSV_LINE_VALIDITY_PREDICATE), Collectors.toList()),
                                    (validData, invalidData) -> Map.of(VALID_CSV_LINE_DATA, validData, INVALID_CSV_LINE_DATA, invalidData))
                    );
            var products = dataset.get(VALID_CSV_LINE_DATA)
                    .stream()
                    .map(SummerProductMapper::mapIfPossible)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            if (!dataset.get(INVALID_CSV_LINE_DATA).isEmpty()) {
                var productsFromFormattedDataset = Stream.of(dataset.get(INVALID_CSV_LINE_DATA).stream()
                        .map(Arrays::toString)
                        .map(it -> it.replaceAll(BRACKETS_CLEANER_REGEX, REPLACEMENT_WORD))
                        .collect(Collectors.joining())
                        .split(VALID_CSV_LINE_TAIL))
                        .map(line -> line.split(DELIMITER, DELIM_LIMIT))
                        .map(SummerProductMapper::mapIfPossible)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList());
                products.addAll(productsFromFormattedDataset);
            }
            return products;
        }

    }

}