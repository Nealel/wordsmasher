package com.github.nealel.wordsmasher.model;

import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TransitionMatrixTest {

    @Test
    public void oneWord_chunkSizeOne() {
        TransitionMatrix actual = new TransitionMatrix(Set.of("apple"), 1);

        assertCounts(actual, "^", Map.of("a", 1));
        assertCounts(actual, "a", Map.of("p", 1));
        assertCounts(actual, "p", Map.of("p", 1, "l", 1));
        assertCounts(actual, "l", Map.of("e", 1));
        assertCounts(actual, "e", Map.of("$", 1));
    }

    @Test
    public void oneWord_chunkSizeThree() {
        TransitionMatrix actual = new TransitionMatrix(Set.of("apple"), 3);

        assertCounts(actual, "^", Map.of("app", 1));
        assertCounts(actual, "app", Map.of("l", 1));
        assertCounts(actual, "ppl", Map.of("e", 1));
        assertCounts(actual, "ple", Map.of("$", 1));
    }

    @Test
    public void threeWords_chunkSizeOne() {
        TransitionMatrix actual = new TransitionMatrix(Set.of("apple", "ant", "want"), 1);

        assertCounts(actual, "^", Map.of("a", 2, "w", 1));
        assertCounts(actual, "a", Map.of("p", 1, "n", 2));
        assertCounts(actual, "p", Map.of("p", 1, "l", 1));
        assertCounts(actual, "l", Map.of("e", 1));
        assertCounts(actual, "e", Map.of("$", 1));
        assertCounts(actual, "n", Map.of("t", 2));
        assertCounts(actual, "t", Map.of("$", 2));
    }

    @Test
    public void checkProbabilities() {
        TransitionMatrix actual = new TransitionMatrix(Set.of("apple", "bob"), 1);

        assertProbabilities(actual, "^", Map.of("a", 0.5, "b", 0.5));
        assertProbabilities(actual, "a", Map.of("p", 1d));
        assertProbabilities(actual, "p", Map.of("p", 0.5, "l", 0.5));
        assertProbabilities(actual, "l", Map.of("e", 1d));
        assertProbabilities(actual, "e", Map.of("$", 1d));
        assertProbabilities(actual, "b", Map.of("o", 0.5, "$", 0.5));
        assertProbabilities(actual, "o", Map.of("b", 1d));
    }

    private static void assertCounts(TransitionMatrix matrix, String from, Map expected) {
        Map actual = matrix.getCounts(from).asMap();
        assertThat(actual.entrySet(), everyItem(is(in(expected.entrySet()))));
        assertThat(expected.entrySet(), everyItem(is(in(actual.entrySet()))));
    }

    private static void assertProbabilities(TransitionMatrix matrix, String from, Map expected) {
        Map actual = matrix.getProbabilities(from).asMap();
        assertThat(actual.entrySet(), everyItem(is(in(expected.entrySet()))));
        assertThat(expected.entrySet(), everyItem(is(in(actual.entrySet()))));
    }
}