package org.justvit.docsearch.util;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Some NLP utility functions
 */
public class Nlp {

    private static final double EPSILON = 1e-10;

    private Nlp() { }

    /**
     * Performs cosine normalization: v / (sqrt(sum(v[i]^2))
     */
    public static <T> Map<T, Double> normalize(Map<T, Double> vector) {
        double sumOfSquares = vector.entrySet().stream()
                .mapToDouble(e -> e.getValue() * e.getValue())
                .sum();
        double normFactor = sumOfSquares < EPSILON ? 1.0 : Math.sqrt(sumOfSquares);

        return vector.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() / normFactor));
    }

    /**
     * Cosine between two vectors: sum(v1[i]*v2[i])
     */
    public static <T> double cosine(Map<T, Double> v1, Map<T, Double> v2) {
        return v1.entrySet().stream()
                .mapToDouble(e1 -> e1.getValue() * v2.getOrDefault(e1.getKey(), 0.0))
                .sum();
    }

    public static <T> List<Pair<T, Double>> sortByWeight(Map<T, Double> weighedDocs) {
        // convert the map into a list, to make it sortable
        List<Pair<T, Double>> wdocs = weighedDocs.entrySet().stream()
                .map(e -> Pair.of(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        // sort docs by weight: the heaviest goes first
        wdocs.sort((a,b) -> b.getValue().compareTo(a.getValue()));

        return wdocs;
    }
}
