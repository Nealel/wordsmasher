package com.github.nealel.wordsmasher.model;

import java.util.HashMap;
import java.util.Map;

public class TransitionCounts {
    private Map<String, Integer> transitionCounts = new HashMap<>();

    public void recordTransition(String to) {
        transitionCounts.put(to, transitionCounts.getOrDefault(to, 0) + 1);
    }

    public Map<String, Integer> asMap() {
        return transitionCounts;
    }

}
