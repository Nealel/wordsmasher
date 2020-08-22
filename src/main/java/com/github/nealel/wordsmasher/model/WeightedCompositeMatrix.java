package com.github.nealel.wordsmasher.model;

import java.util.*;
import java.util.Map.Entry;

import static java.util.stream.Collectors.*;

public class WeightedCompositeMatrix {

    private final Map<String, TransitionProbabilities> probabilities = new HashMap<>();

    public WeightedCompositeMatrix(Set<TransitionCountMatrix> matrices) {
        populateMatrix(matrices);
    }

    public String getRandomNextLetter(String chunk) {
        return probabilities.get(chunk).randomNextLetter();
    }

    private void populateMatrix(Set<TransitionCountMatrix> matrices) {
        Set<String> allChunks = matrices.stream()
                .map(TransitionCountMatrix::getChunks)
                .flatMap(Set::stream)
                .collect(toSet());

        for (String chunk : allChunks) {
            Map<String, Double> summedWeightedCounts = weightAndCombineCounts(matrices, chunk);
            TransitionProbabilities asProbabilities = new TransitionProbabilities(summedWeightedCounts);
            probabilities.put(chunk, asProbabilities);
        }
    }

    private Map<String, Double> weightAndCombineCounts(Set<TransitionCountMatrix> matrices, String chunk) {
        return matrices.stream()
                .map(m -> m.getWeightedCount(chunk))
                .flatMap(m -> m.entrySet().stream())
                .collect(toMap(Entry::getKey, Entry::getValue, Double::sum));
    }
}
