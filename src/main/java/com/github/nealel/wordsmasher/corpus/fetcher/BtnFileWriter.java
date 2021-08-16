package com.github.nealel.wordsmasher.corpus.fetcher;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Slf4j
public class BtnFileWriter {

    public static final String OUTPUT_PATH = "src/main/resources/data/btn_rich/";

    /**
     * Writes the name to a file, organized by culture and gender
     */
    public static void responseToFile(String name, JSONObject nameMetadata) {
        JSONArray usages = nameMetadata.getJSONArray("usages");
        for (Object usage : usages) {
            try {
                String filePath = getFilePath((JSONObject) usage);
                appendToFile(name, filePath);
            } catch (Exception e) {
                log.warn("error!", e);
            }
        }
    }

    public static void appendToFile(String name, String filePath) throws IOException {
        File file = new File(filePath);
        file.createNewFile();
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file, true), StandardCharsets.UTF_8))) {
            writer.append(name);
            writer.append("\n");
        }
    }

    private static String getFilePath(JSONObject usage) {
        String culture = usage.getString("usage_full");
        String gender = usage.getString("usage_gender");
        return OUTPUT_PATH + culture + "_" + gender + ".txt";
    }
}
