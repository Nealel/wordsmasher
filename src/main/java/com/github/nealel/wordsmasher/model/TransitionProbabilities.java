package com.github.nealel.wordsmasher.model;

import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * A normalized representation of TransitionCounts data
 * The total probability will always add up to 1, but the relative probabilities are preserved from the counts
 */
public class TransitionProbabilities {
    private final Map<String, Double> transitionProbabilities;

    public TransitionProbabilities(Map<String, Double> weightedCounts) {
        double total = weightedCounts.values()
                .stream()
                .mapToDouble(Double::doubleValue).sum();

        transitionProbabilities = weightedCounts.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() / total));
    }

    public Map<String, Double> asMap() {
        return transitionProbabilities;
    }
}
