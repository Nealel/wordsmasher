package com.github.nealel.wordsmasher.model;

import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * A normalized representation of TransitionCounts data
 * The total probability will always add up to 1, but the relative probabilities are preserved from the counts
 */
public class TransitionProbabilities {
    private static final Random RANDOM = new Random();
    private final Map<String, Double> transitionProbabilities;

    public TransitionProbabilities(Map<String, Double> weightedCounts) {
        double total = weightedCounts.values()
                .stream()
                .mapToDouble(Double::doubleValue).sum();

        transitionProbabilities = weightedCounts.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() / total));
    }

    /**
     * Uses random weighted roulette selection to generate the next letter, based on relative probabilities
     */
    public String randomNextLetter() {
        double target = RANDOM.nextDouble();
        double current = 0d;
        for (Map.Entry<String, Double> entry : transitionProbabilities.entrySet()) {
            current += entry.getValue();
            if (current >= target) {
                return entry.getKey();
            }
        }
        throw new RuntimeException("Roulette generation error. This should never happen");
    }

}
