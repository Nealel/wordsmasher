package com.github.nealel.wordsmasher.generator;

import com.github.nealel.wordsmasher.api.BatchRequestDto;
import com.github.nealel.wordsmasher.api.SourceSpecificationDto;
import com.github.nealel.wordsmasher.corpus.FileCorpusLoader;
import com.github.nealel.wordsmasher.model.TransitionCountMatrix;
import com.github.nealel.wordsmasher.model.WeightedCompositeMatrix;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static com.github.nealel.wordsmasher.model.TransitionCountMatrix.END_SYMBOL;
import static com.github.nealel.wordsmasher.model.TransitionCountMatrix.START_SYMBOL;
import static java.util.stream.Collectors.toList;

@Component
@Slf4j
public class BatchGenerator {
    private final int maxAttemptsPerWord;
    private final FileCorpusLoader fileCorpusLoader;

    public BatchGenerator(@Value("${wordsmasher.generator.maxattempts:100}") int maxAttemptsPerWord,
                          FileCorpusLoader fileCorpusLoader) {
        this.fileCorpusLoader = fileCorpusLoader;
        this.maxAttemptsPerWord = maxAttemptsPerWord;
    }

    public List<String> generateBatch(BatchRequestDto request) throws IOException {
        Set<String> inputData = new HashSet<>();
        Set<TransitionCountMatrix> countMatrices = new HashSet<>();
        for (SourceSpecificationDto source : request.getSourceSpecifications()) {
            Set<String> words = fileCorpusLoader.loadCorpus(source.getFilename(), request.getChunkSize());
            inputData.addAll(words);
            double normalizedWeight = source.getWeight() / words.size();
            countMatrices.add(new TransitionCountMatrix(words, request.getChunkSize(), normalizedWeight));
        }
        WeightedCompositeMatrix matrix = new WeightedCompositeMatrix(countMatrices);

        List<String> names = Stream.generate(() -> generateWord(request, matrix))
                .limit(request.getBatchSize() * maxAttemptsPerWord)
                .filter(Optional::isPresent)
                .distinct()
                .map(Optional::get)
                .filter(word -> !inputData.contains(word))
                .limit(request.getBatchSize())
                .collect(toList());
        log.info("Generated words: {}", Strings.join(names, ','));
        return names;
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
