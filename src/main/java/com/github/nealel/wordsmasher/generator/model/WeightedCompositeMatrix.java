package com.github.nealel.wordsmasher.generator.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

public class WeightedCompositeMatrix {

    private final Map<String, TransitionProbabilities> probabilities = new HashMap<>();

    public WeightedCompositeMatrix(Map<TransitionCountMatrix, Double> matrices) {
        populateMatrix(matrices);
    }

    public TransitionProbabilities getProbabilities(String chunk) {
        return probabilities.get(chunk);
    }

    private void populateMatrix(Map<TransitionCountMatrix, Double> matrices) {
        Set<String> allChunks = matrices.keySet().stream()
                .map(TransitionCountMatrix::getChunks)
                .flatMap(Set::stream)
                .collect(toSet());

        for (String chunk : allChunks) {
            Map<String, Double> summedWeightedCounts = weightAndCombineCounts(matrices, chunk);
            TransitionProbabilities asProbabilities = new TransitionProbabilities(summedWeightedCounts);
            probabilities.put(chunk, asProbabilities);
        }
    }

    private Map<String, Double> weightAndCombineCounts(Map<TransitionCountMatrix, Double> matrices, String chunk) {
        return matrices.entrySet().stream()
                .map(m -> m.getKey().getWeightedCount(chunk, m.getValue()))
                .flatMap(m -> m.entrySet().stream())
                .collect(toMap(Entry::getKey, Entry::getValue, Double::sum));
    }
}
