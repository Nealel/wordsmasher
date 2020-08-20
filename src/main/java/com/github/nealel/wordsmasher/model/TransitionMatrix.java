package com.github.nealel.wordsmasher.model;

import java.util.*;

public class TransitionMatrix {
    public static final String START_SYMBOL = "^";
    public static final String END_SYMBOL = "$";
    private final int chunkSize;
    private final Map<String, TransitionCounts> counts = new HashMap<>();
    private final Map<String, TransitionProbabilities> probabilities = new HashMap<>();

    public TransitionMatrix(Set<String> words, int chunkSize) {
        this.chunkSize = chunkSize;
        words.forEach(this::getWordTransitions);
        for (String chunk : counts.keySet()) {
            probabilities.put(chunk, new TransitionProbabilities(counts.get(chunk)));
        }
    }

    public TransitionCounts getCounts(String from) {
        return counts.get(from);
    }

    public TransitionProbabilities getProbabilities(String from) {
        return probabilities.get(from);
    }

    private void getWordTransitions(String word) {
        recordFor(START_SYMBOL, chunkAt(word, 0));
        int maxIndex = word.length() - chunkSize;
        for (int i = 0; i < maxIndex; i++) {
            recordFor(chunkAt(word, i), String.valueOf(word.charAt(i + chunkSize)));
        }
        recordFor(chunkAt(word, maxIndex), END_SYMBOL);

    }

    private void recordFor(String from, String to) {
        counts.putIfAbsent(from, new TransitionCounts());
        counts.get(from).recordTransition(to);
    }

    private String chunkAt(String word, int i) {
        return word.substring(i, i + chunkSize);
    }

}
