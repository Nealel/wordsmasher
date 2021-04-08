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

import static com.github.nealel.wordsmasher.corpus.FileLoader.loadCorpus;

@Component
@Slf4j
public class FileCorpusLoader {
    public static final int MIN_CORPUS_SIZE = 100;
    private final String fileroot;
    private final Map<String, Set<String>> corpus = new HashMap<>();

    public FileCorpusLoader(@Value("${generator.corpus.fileroot:src/main/resources/data/btn_rich/}") String fileroot)
            throws IOException {
        this.fileroot = fileroot;
        log.info("loading corpus");
        loadDirectory("");
        log.info("loaded corpus");
    }

    public Set<String> loadDirectory(String dir) throws IOException {
        File[] files = new File(fileroot + dir).listFiles();
        Set<String> namesForDir = new HashSet<>();

        for (File file : files) {
            if (file.isFile()) {
                Set<String> names = loadCorpus(fileroot + dir + "/" + file.getName());
                if (names.size() > MIN_CORPUS_SIZE) {
                    addFileToCorpus(dir, namesForDir, file, names);
                }
            } else {
                namesForDir.addAll(loadDirectory(dir + "/" + file.getName()));
            }
        }

        corpus.put(dirToPrettyName(dir), namesForDir);
        return namesForDir;
    }

    private void addFileToCorpus(String dir, Set<String> namesForDir, File file, Set<String> names) {
        String fileName = file.getName().substring(0, file.getName().length() - 4);
        corpus.put(dirToPrettyName(dir) + fileName, names);
        namesForDir.addAll(names);
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
}
