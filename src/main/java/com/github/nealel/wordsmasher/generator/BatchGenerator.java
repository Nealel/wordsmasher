package com.github.nealel.wordsmasher.generator;

import com.github.nealel.wordsmasher.api.dto.BatchRequest;
import com.github.nealel.wordsmasher.corpus.MatrixLoader;
import com.github.nealel.wordsmasher.generator.model.TransitionCountMatrix;
import com.github.nealel.wordsmasher.generator.model.WeightedCompositeMatrix;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Component
@Slf4j
public class BatchGenerator {
    private final int maxAttemptsPerWord;
    private final MatrixLoader matrixLoader;

    public BatchGenerator(@Value("${wordsmasher.generator.maxattempts:100}") int maxAttemptsPerWord,
                          MatrixLoader matrixLoader) {
        this.maxAttemptsPerWord = maxAttemptsPerWord;
        this.matrixLoader = matrixLoader;
    }

    public List<String> generateBatch(BatchRequest request) throws ExecutionException {
        Map<TransitionCountMatrix, Double> countMatrices = matrixLoader.getWeightedCounts(request);
        Set<String> inputWords = getAllInputWords(countMatrices);
        WeightedCompositeMatrix matrix = new WeightedCompositeMatrix(countMatrices);
        List<String> names = generateNames(request, inputWords, matrix);
        log.info("Generated words: {}", Strings.join(names, ','));
        return names;
    }

    private List<String> generateNames(BatchRequest request, Set<String> inputData, WeightedCompositeMatrix matrix) {
        WordGenerator generator = new WordGenerator(request, matrix);
        return Stream.generate(generator::nextWord)
                .limit(request.getBatchSize() * maxAttemptsPerWord)
                .filter(Optional::isPresent)
                .distinct()
                .map(Optional::get)
//                .filter(word -> !inputData.contains(word))
                .limit(request.getBatchSize())
                .collect(toList());
    }

    private Set<String> getAllInputWords(Map<TransitionCountMatrix, Double> countMatrices) {
        return countMatrices.keySet().stream()
                .map(TransitionCountMatrix::getWords)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

}
