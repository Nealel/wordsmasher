package com.github.nealel.wordsmasher.corpus;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class FileCorpusLoader {

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
