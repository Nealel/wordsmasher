package com.github.nealel.wordsmasher.corpus;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

@Slf4j
public class FileLoader {

    public static final int MIN_SIZE = 2;

    public static Set<String> loadCorpus(String fileName) throws IOException {
        Scanner scanner = new Scanner(new File(fileName))
                                    .useDelimiter("\\s+");

        Set<String> words = new HashSet<>();
        int unsuitableWords = 0;

        while (scanner.hasNext()) {
            String word = scanner.next();
            if (isSuitable(word)) {
                words.add(word.toLowerCase());
            } else {
                unsuitableWords++;
            }
        }

        log.info("Loaded {} words from file {}. {} words were discarded as unsuitable",
                fileName, words.size(), unsuitableWords);
        return words;
    }

    private static boolean isSuitable(String word) {
        return word.matches("[a-zA-Z]+") && word.length() >= MIN_SIZE;
    }
}
