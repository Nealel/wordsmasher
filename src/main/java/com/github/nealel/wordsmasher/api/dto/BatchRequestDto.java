package com.github.nealel.wordsmasher.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class BatchRequestDto {
    private final int chunkSize;
    private final int minWordLength;
    private final int maxWordLength;
    private final int batchSize;
    private final List<SourceSpecification> sourceSpecifications;
    private String pattern;
}
