package com.github.nealel.wordsmasher.model;

import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TransitionCountMatrixTest {

    @Test
    public void oneWord_chunkSizeOne() {
        TransitionCountMatrix actual = new TransitionCountMatrix(Set.of("apple"), 1, 1);

        assertCounts(actual, "^", Map.of("a", 1));
        assertCounts(actual, "a", Map.of("p", 1));
        assertCounts(actual, "p", Map.of("p", 1, "l", 1));
        assertCounts(actual, "l", Map.of("e", 1));
        assertCounts(actual, "e", Map.of("$", 1));
    }

    @Test
    public void oneWord_chunkSizeThree() {
        TransitionCountMatrix actual = new TransitionCountMatrix(Set.of("apple"), 3, 1);

        assertCounts(actual, "^", Map.of("app", 1));
        assertCounts(actual, "app", Map.of("l", 1));
        assertCounts(actual, "ppl", Map.of("e", 1));
        assertCounts(actual, "ple", Map.of("$", 1));
    }

    @Test
    public void threeWords_chunkSizeOne() {
        TransitionCountMatrix actual = new TransitionCountMatrix(Set.of("apple", "ant", "want"), 1,1);

        assertCounts(actual, "^", Map.of("a", 2, "w", 1));
        assertCounts(actual, "a", Map.of("p", 1, "n", 2));
        assertCounts(actual, "p", Map.of("p", 1, "l", 1));
        assertCounts(actual, "l", Map.of("e", 1));
        assertCounts(actual, "e", Map.of("$", 1));
        assertCounts(actual, "n", Map.of("t", 2));
        assertCounts(actual, "t", Map.of("$", 2));
    }

    private static void assertCounts(TransitionCountMatrix matrix, String from, Map expected) {
        Map actual = matrix.getCounts(from).asMap();
        assertThat(actual.entrySet(), everyItem(is(in(expected.entrySet()))));
        assertThat(expected.entrySet(), everyItem(is(in(actual.entrySet()))));
    }

}