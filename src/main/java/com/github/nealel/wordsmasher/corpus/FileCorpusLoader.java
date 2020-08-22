package com.github.nealel.wordsmasher.corpus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FileCorpusLoader {
    private final String fileroot;
    private final ResourcePatternResolver resourceResolver;

    public FileCorpusLoader(@Value("${generator.corpus.fileroot:/data/btn_rich/}") String fileroot,
                            ResourcePatternResolver resourceResolver) {
        this.fileroot = fileroot;
        this.resourceResolver = resourceResolver;
    }

    public List<String> getAvailableFiles() throws IOException {
        return Arrays.stream(resourceResolver.getResources("classpath:" + fileroot + "*.txt"))
                .map(Resource::getFilename)
                .map(s -> s.substring(0, s.length() - 4))
                .sorted()
                .collect(Collectors.toList());
    }

    public static Set<String> loadCorpus(String fileName, int minSize){
        Scanner scanner = new Scanner(FileCorpusLoader.class.getResourceAsStream(fileName), StandardCharsets.UTF_8)
                .useDelimiter("\\s+");

        Set<String> words = new HashSet<>();
        int unsuitableWords = 0;
        while (scanner.hasNext()) {
            String word = scanner.next();
            if (isSuitable(minSize, word)) {
                words.add(word.toLowerCase());
            } else {
                unsuitableWords++;
            }
        }

        log.info("Loaded {} words from file {}. {} words were discarded as unsuitable",
                fileName, words.size(), unsuitableWords);
        return words;
    }

    private static boolean isSuitable(int minSize, String word) {
        return word.matches("[a-zA-Z]+") && word.length() >= minSize;
    }
}
