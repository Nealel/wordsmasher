package com.github.nealel.wordsmasher.generator.model;

import java.util.HashMap;
import java.util.Map;

/**
 * The raw transition counts for a given chunk
 * A transition is when a word transitions from a chunk to a new letter
 *
 * For example, if this is the TransitionCounts for the "th" chunk
 * and the data corpus is "the this thing"
 * the count for "e" will be 1 and for "i" will be 2
 */
public class TransitionCounts {
    private Map<String, Integer> transitionCounts = new HashMap<>();

    public void recordTransition(String to) {
        transitionCounts.put(to, transitionCounts.getOrDefault(to, 0) + 1);
    }

    public Map<String, Integer> asMap() {
        return transitionCounts;
    }

}
