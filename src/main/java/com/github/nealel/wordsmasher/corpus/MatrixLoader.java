package com.github.nealel.wordsmasher.corpus;

import com.github.nealel.wordsmasher.api.dto.BatchRequestDto;
import com.github.nealel.wordsmasher.api.dto.SourceSpecification;
import com.github.nealel.wordsmasher.generator.model.TransitionCountMatrix;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MatrixLoader {
    private final Cache<String, TransitionCountMatrix> cacheCountsMatrix = CacheBuilder
            .newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .maximumSize(500)
            .build();

    @Autowired
    private FileCorpusLoader fileCorpusLoader;

    public Map<TransitionCountMatrix, Double> getWeightedCounts(BatchRequestDto request) throws ExecutionException {
        Map<TransitionCountMatrix, Double> countMatrices = new HashMap<>();
        for (SourceSpecification source : request.getSourceSpecifications()) {
            TransitionCountMatrix matrix = cacheCountsMatrix.get(source.getFilename() + ":" + request.getChunkSize(), () -> loadMatrix(request, source));
            double normalizedWeight = source.getWeight() / matrix.getWords().size();
            countMatrices.put(matrix, normalizedWeight);
        }
        return countMatrices;
    }

    private TransitionCountMatrix loadMatrix(BatchRequestDto request, SourceSpecification source) {
        Set<String> words = fileCorpusLoader.getFile(source.getFilename())
                .stream()
                .filter(w -> w.length() >= request.getChunkSize())
                .collect(Collectors.toSet());
        return new TransitionCountMatrix(words, request.getChunkSize());
    }

}
