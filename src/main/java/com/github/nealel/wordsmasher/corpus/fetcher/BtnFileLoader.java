package com.github.nealel.wordsmasher.corpus.fetcher;

import com.github.nealel.wordsmasher.corpus.FileCorpusLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class BtnFileLoader {
    private static final String RAW_NAMES = "/data/btn_raw/btn_givennames.txt";

    public static List<String> loadRaw(String from, String to) {
        Scanner scanner = new Scanner(FileCorpusLoader.class.getResourceAsStream(RAW_NAMES), StandardCharsets.UTF_8)
                .useDelimiter("\\n");

        List<String> names = new ArrayList<>();
        while (scanner.hasNext()) {
            String name = scanner.next();
            if (isValid(from, to, name)) {
                names.add(name.split("\\s+")[0]);
            }
        }

        log.info("Loaded {} names: {}", names.size(), Strings.join(names, ','));
        return names;
    }

    private static boolean isValid(String from, String to, String name) {
        return !name.startsWith("#")
                && !Strings.isEmpty(name)
                && name.toLowerCase().compareTo(from) >= 0
                && name.toLowerCase().compareTo(to) < 0;
    }
}
