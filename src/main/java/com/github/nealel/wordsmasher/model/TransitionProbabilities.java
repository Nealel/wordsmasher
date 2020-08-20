package com.github.nealel.wordsmasher.model;

import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class TransitionProbabilities {
    private static final Random RANDOM = new Random();
    private final Map<String, Double> transitionProbabilities;

    public TransitionProbabilities(TransitionCounts counts) {
        double total = counts.asMap().values().stream().mapToInt(Integer::intValue).sum();
        transitionProbabilities = counts.asMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() / total));
    }

    public Map<String, Double> asMap() {
        return transitionProbabilities;
    }

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
