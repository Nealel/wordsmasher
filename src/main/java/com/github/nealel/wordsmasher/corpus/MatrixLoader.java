package com.github.nealel.wordsmasher.corpus;

import com.github.nealel.wordsmasher.api.BatchRequestDto;
import com.github.nealel.wordsmasher.api.SourceSpecification;
import com.github.nealel.wordsmasher.model.TransitionCountMatrix;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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

    private TransitionCountMatrix loadMatrix(BatchRequestDto request, SourceSpecification source) throws IOException {
        Set<String> words = fileCorpusLoader.loadCorpus(source.getFilename(), request.getChunkSize());
        return new TransitionCountMatrix(words, request.getChunkSize());
    }

}
