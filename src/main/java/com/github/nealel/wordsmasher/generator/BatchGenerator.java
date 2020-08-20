package com.github.nealel.wordsmasher.generator;

import com.github.nealel.wordsmasher.corpus.FileCorpusLoader;
import com.github.nealel.wordsmasher.model.TransitionMatrix;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Stream;

import static com.github.nealel.wordsmasher.model.TransitionMatrix.END_SYMBOL;
import static com.github.nealel.wordsmasher.model.TransitionMatrix.START_SYMBOL;
import static java.util.stream.Collectors.toList;

@Component
@Slf4j
public class BatchGenerator {
    private final int chunkSize;
    private final int minWordLength;
    private final int maxWordLength;
    private final int maxAttemptsPerWord;
    private final int batchSize;
    private final Set<String> inputData;
    private final TransitionMatrix matrix;

    public BatchGenerator(@Value("${wordsmasher.generator.sourcefile}")String file,
                          @Value("${wordsmasher.generator.chunksize:3}") int chunkSize,
                          @Value("${wordsmasher.generator.length.min:3}") int minWordLength,
                          @Value("${wordsmasher.generator.length.max:12}") int maxWordLength,
                          @Value("${wordsmasher.generator.maxattempts:100}") int maxAttemptsPerWord,
                          @Value("${wordsmasher.generator.batchsize:100}") int batchSize) {
        this.chunkSize = chunkSize;
        this.minWordLength = minWordLength;
        this.maxWordLength = maxWordLength;
        this.maxAttemptsPerWord = maxAttemptsPerWord;
        this.batchSize = batchSize;

        inputData = FileCorpusLoader.loadCorpus(file, this.chunkSize);
        matrix = new TransitionMatrix(inputData, this.chunkSize);
    }

    @PostConstruct
    public void generateBatch() {
        List<String> names = Stream.generate(this::generateWord)
                .limit(batchSize * maxAttemptsPerWord)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(word -> !inputData.contains(word))
                .limit(batchSize)
                .collect(toList());
        log.info("Generated words: {}", Strings.join(names, ','));
    }

    private Optional<String> generateWord() {
        String word = matrix.getProbabilities(START_SYMBOL).randomNextLetter();

        while (word.length() < maxWordLength) {
            String previousChunk = word.substring(word.length() - chunkSize);
            String nextLetter = matrix.getProbabilities(previousChunk).randomNextLetter();
            if (nextLetter.equals(END_SYMBOL)) {
                if (word.length() > minWordLength) {
                    return Optional.of(word);
                }
                return Optional.empty();
            }
            word += nextLetter;
        }
        return Optional.empty();
    }

}
