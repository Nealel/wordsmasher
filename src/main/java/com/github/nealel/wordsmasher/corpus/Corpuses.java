package com.github.nealel.wordsmasher.corpus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.nealel.wordsmasher.corpus.FileLoader.loadNamesFromFile;

/**
 * Contains the raw data for all files, organized by corpus name
 */
@Component
@Slf4j
public class Corpuses {
    public final int minCorpusSize;
    private final String fileroot;
    private final Map<String, Set<String>> corpuses = new HashMap<>(); // key is corpus's name, value is corpus contents

    public Corpuses(@Value("${generator.corpus.minsize:100}") int minCorpusSize,
                    @Value("${generator.corpus.fileroot:src/main/resources/data/current_corpus/}") String fileroot)
            throws IOException {
        this.minCorpusSize = minCorpusSize;
        this.fileroot = fileroot;
        log.info("loading corpus");
        loadAllCorpuses();
        log.info("loaded {} corpuses", corpuses.size());
    }

    public void loadAllCorpuses() throws IOException {
        loadNamesFromDirectory("");
    }

    /**
     * Recursively loads a directory and all its subdirectories and files into the corpuses map
     */
    public Set<String> loadNamesFromDirectory(String dir) throws IOException {
        File[] files = new File(fileroot + dir).listFiles();
        Set<String> namesForDir = new HashSet<>();

        for (File file : files) {
            if (file.isFile()) {
                Set<String> names = loadNamesFromFile(fileroot + dir + "/" + file.getName());
                if (names.size() >= minCorpusSize) {
                    addFileToCorpus(dir, namesForDir, file, names);
                }
                namesForDir.addAll(names);
            } else {
                namesForDir.addAll(loadNamesFromDirectory(dir + "/" + file.getName()));
            }
        }

        if (namesForDir.size() >= minCorpusSize) {
            corpuses.put(dirToPrettyName(dir), namesForDir);
        }
        return namesForDir;
    }

    private void addFileToCorpus(String dir, Set<String> namesForDir, File file, Set<String> names) {
        String fileName = file.getName().substring(0, file.getName().length() - 4);
        corpuses.put(dirToPrettyName(dir) + " > " + fileName, names);
        namesForDir.addAll(names);
    }

    /**
     * generates a user-facing source name based on a directory name
     * e.g. `/European/Romance` becomes `European > Romance`
     */
    private String dirToPrettyName(String dir) {
        return dir.replaceFirst("/", "").replace("/", " > ");
    }

    public List<String> getAvailableCorpuses() {
        return corpuses.keySet().stream()
                .sorted()
                .collect(Collectors.toList());
    }

    public Set<String> getContentsForCorpus(String file) {
        return corpuses.get(file);
    }
}
