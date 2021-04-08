package com.github.nealel.wordsmasher.generator;

import com.github.nealel.wordsmasher.api.dto.BatchRequestDto;
import com.github.nealel.wordsmasher.generator.model.WeightedCompositeMatrix;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.regex.Pattern;

import static com.github.nealel.wordsmasher.generator.model.TransitionCountMatrix.END_SYMBOL;
import static com.github.nealel.wordsmasher.generator.model.TransitionCountMatrix.START_SYMBOL;

@Slf4j
public class WordGenerator {
    private static final Random RANDOM = new Random();
    private final Pattern pattern;
    private final BatchRequestDto request;
    private final WeightedCompositeMatrix matrix;

    public WordGenerator(BatchRequestDto request, WeightedCompositeMatrix matrix) {
        this.request = request;
        this.matrix = matrix;
        this.pattern = RegexResolver.toRegex(request.getPattern());
    }

    public Optional<String> nextWord() {
        StringBuilder word = new StringBuilder(nextLetter(matrix, START_SYMBOL));

        while (word.length() < request.getMaxWordLength()) {
            String previousChunk = word.substring(word.length() - request.getChunkSize());
            String nextLetter = nextLetter(matrix, previousChunk);
            if (nextLetter.equals(END_SYMBOL)) {
                if (word.length() > request.getMinWordLength() && pattern.matcher(word.toString().toLowerCase()).find()) {
                    return Optional.of(word.toString());
                }
                return Optional.empty();
            }
            word.append(nextLetter);
        }
        return Optional.empty();
    }

    /**
     * Uses random weighted roulette selection to generate the next letter, based on relative probabilities
     */
    private String nextLetter(WeightedCompositeMatrix matrix, String chunk) {
        Map<String, Double> probabilities = matrix.getProbabilities(chunk).asMap();
        double target = RANDOM.nextDouble();
        double current = 0d;
        for (Map.Entry<String, Double> entry : probabilities.entrySet()) {
            current += entry.getValue();
            if (current >= target) {
                return entry.getKey();
            }
        }
        throw new RuntimeException("Roulette generation error. This should never happen");
    }

}
