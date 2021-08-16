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

    /**
     * loads a list of names from a file, filtering out invalid names and names that are out of the desired range
     */
    public static List<String> loadRaw(String fromRange, String toRange) {
        Scanner scanner = new Scanner(FileCorpusLoader.class.getResourceAsStream(RAW_NAMES), StandardCharsets.UTF_8)
                .useDelimiter("\\n");

        List<String> names = new ArrayList<>();
        while (scanner.hasNext()) {
            String name = scanner.next();
            if (isValid(fromRange, toRange, name)) {
                names.add(name.split("\\s+")[0]);
            }
        }

        log.info("Loaded {} names: {}", names.size(), Strings.join(names, ','));
        return names;
    }

    private static boolean isValid(String fromRange, String toRange, String name) {
        return !name.startsWith("#")
                && !Strings.isEmpty(name)
                && name.toLowerCase().compareTo(fromRange) >= 0
                && name.toLowerCase().compareTo(toRange) < 0;
    }
}
