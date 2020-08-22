package com.github.nealel.wordsmasher.generator;

import com.github.nealel.wordsmasher.api.BatchRequestDto;
import com.github.nealel.wordsmasher.api.SourceSpecification;
import com.github.nealel.wordsmasher.corpus.FileCorpusLoader;
import com.github.nealel.wordsmasher.model.TransitionCountMatrix;
import com.github.nealel.wordsmasher.model.WeightedCompositeMatrix;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.nealel.wordsmasher.model.TransitionCountMatrix.END_SYMBOL;
import static com.github.nealel.wordsmasher.model.TransitionCountMatrix.START_SYMBOL;
import static java.util.stream.Collectors.toList;

@Component
@Slf4j
public class BatchGenerator {
    private final int maxAttemptsPerWord;
    private final Cache<SourceSpecification, TransitionCountMatrix> cacheCountsMatrix = CacheBuilder
            .newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .maximumSize(500)
            .build();

    private final FileCorpusLoader fileCorpusLoader;

    public BatchGenerator(@Value("${wordsmasher.generator.maxattempts:100}") int maxAttemptsPerWord,
                          FileCorpusLoader fileCorpusLoader) {
        this.fileCorpusLoader = fileCorpusLoader;
        this.maxAttemptsPerWord = maxAttemptsPerWord;
    }

    public List<String> generateBatch(BatchRequestDto request) throws IOException, ExecutionException {
        Map<TransitionCountMatrix, Double> countMatrices = calculateWeightedCounts(request);
        Set<String> inputWords = getAllInputWords(countMatrices);
        WeightedCompositeMatrix matrix = new WeightedCompositeMatrix(countMatrices);
        List<String> names = generateNames(request, inputWords, matrix);
        log.info("Generated words: {}", Strings.join(names, ','));
        return names;
    }

    private List<String> generateNames(BatchRequestDto request, Set<String> inputData, WeightedCompositeMatrix matrix) {
        return Stream.generate(() -> generateWord(request, matrix))
                .limit(request.getBatchSize() * maxAttemptsPerWord)
                .filter(Optional::isPresent)
                .distinct()
                .map(Optional::get)
                .filter(word -> !inputData.contains(word))
                .limit(request.getBatchSize())
                .collect(toList());
    }

    private Set<String> getAllInputWords(Map<TransitionCountMatrix, Double> countMatrices) {
        return countMatrices.keySet().stream()
                .map(TransitionCountMatrix::getWords)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    private Map<TransitionCountMatrix, Double> calculateWeightedCounts(BatchRequestDto request) throws ExecutionException {
        Map<TransitionCountMatrix, Double> countMatrices = new HashMap<>();
        for (SourceSpecification source : request.getSourceSpecifications()) {
            TransitionCountMatrix matrix = cacheCountsMatrix.get(source, () -> loadMatrix(request, source));
            double normalizedWeight = source.getWeight() / matrix.getWords().size();
            countMatrices.put(matrix, normalizedWeight);
        }
        return countMatrices;
    }

    private TransitionCountMatrix loadMatrix(BatchRequestDto request, SourceSpecification source) throws IOException {
        Set<String> words = fileCorpusLoader.loadCorpus(source.getFilename(), request.getChunkSize());
        return new TransitionCountMatrix(words, request.getChunkSize());
    }

    private Optional<String> generateWord(BatchRequestDto request, WeightedCompositeMatrix matrix) {
        StringBuilder word = new StringBuilder(matrix.getRandomNextLetter(START_SYMBOL));

        while (word.length() < request.getMaxWordLength()) {
            String previousChunk = word.substring(word.length() - request.getChunkSize());
            String nextLetter = matrix.getRandomNextLetter(previousChunk);
            if (nextLetter.equals(END_SYMBOL)) {
                if (word.length() > request.getMinWordLength()) {
                    return Optional.of(word.toString());
                }
                return Optional.empty();
            }
            word.append(nextLetter);
        }
        return Optional.empty();
    }

}
