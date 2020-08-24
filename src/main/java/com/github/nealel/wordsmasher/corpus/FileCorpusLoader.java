package com.github.nealel.wordsmasher.corpus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FileCorpusLoader {
    private final String fileroot;
    private final ResourcePatternResolver resourceResolver;
    private final Map<String, Set<String>> corpus = new HashMap<>();

    public FileCorpusLoader(@Value("${generator.corpus.fileroot:src/main/resources/data/btn_rich/}") String fileroot,
                            ResourcePatternResolver resourceResolver) throws IOException {
        this.fileroot = fileroot;
        this.resourceResolver = resourceResolver;
        log.info("loading corpus");
        loadDirectory("");
        log.info("loaded corpus");
    }

    public Set<String> loadDirectory(String dir) throws IOException {
        File[] files = new File(fileroot + dir).listFiles();
        Set<String> namesForDir = new HashSet<>();
        for (File file : files) {
            if (file.isFile()) {
                Set<String> names = loadCorpus(dir + "/" + file.getName(), 2);
                if (names.size() > 100) {
                    corpus.put(dir.replaceFirst("/", "").replaceAll("/", " > ") + " > " + file.getName().substring(0, file.getName().length() - 4), names);
                    namesForDir.addAll(names);
                }
            } else {
                namesForDir.addAll(loadDirectory(dir + "/" + file.getName()));
            }
        }
        corpus.put(dirToPrettyName(dir), namesForDir);
        return namesForDir;
    }

    private String dirToPrettyName(String dir) {
        return dir.replaceFirst("/", "").replace("/", " > ");
    }

    public List<String> getAvailableFiles() {
        return corpus.keySet().stream()
                .sorted()
                .collect(Collectors.toList());
    }

    public Set<String> getFile(String file) {
        return corpus.get(file);
    }

    public Set<String> loadCorpus(String fileName, int minSize) throws IOException {
        File file = new File(fileroot + fileName);
        Scanner scanner = new Scanner(file)
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

    private boolean isSuitable(int minSize, String word) {
        return word.matches("[a-zA-Z]+") && word.length() >= minSize;
    }
}
