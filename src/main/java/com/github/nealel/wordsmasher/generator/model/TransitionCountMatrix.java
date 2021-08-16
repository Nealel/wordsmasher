package com.github.nealel.wordsmasher.generator.model;

import java.util.*;

import static java.util.stream.Collectors.toMap;

/**
 * Builds a Matrix count of word transitions from a set of input words
 */
public class TransitionCountMatrix {
    public static final String START_SYMBOL = "^";
    public static final String END_SYMBOL = "$";
    private final int chunkSize;
    private final Map<String, TransitionCounts> counts = new HashMap<>();
    private Set<String> words;

    public TransitionCountMatrix(Set<String> words, int chunkSize) {
        this.chunkSize = chunkSize;
        this.words = words;
        words.forEach(this::recordTransitions);
    }

    public Set<String> getWords() {
        return words;
    }

    public TransitionCounts getCounts(String from) {
        return counts.get(from);
    }

    public Set<String> getChunks() {
        return counts.keySet();
    }

    public Map<String, Double> getWeightedCount(String chunk, Double weight) {
        TransitionCounts counts = this.counts.get(chunk);
        if (counts != null) {
            return counts.asMap().entrySet()
                .stream()
                .collect(toMap(Map.Entry::getKey, e -> e.getValue() * weight));
        }
        return new HashMap<>();
    }

    private void recordTransitions(String word) {
        recordTransition(START_SYMBOL, chunkAt(word, 0));
        int maxIndex = word.length() - chunkSize;
        for (int i = 0; i < maxIndex; i++) {
            recordTransition(chunkAt(word, i), String.valueOf(word.charAt(i + chunkSize)));
        }
        recordTransition(chunkAt(word, maxIndex), END_SYMBOL);

    }

    private void recordTransition(String from, String to) {
        counts.putIfAbsent(from, new TransitionCounts());
        counts.get(from).recordTransition(to);
    }

    private String chunkAt(String word, int startIndex) {
        return word.substring(startIndex, startIndex + chunkSize);
    }

}
