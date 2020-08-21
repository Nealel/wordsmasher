package com.github.nealel.wordsmasher.corpus.fetcher;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Slf4j
public class BtnFileWriter {

    public static void responseToFile(String name, JSONObject jsonObject) throws IOException {
        JSONArray usages = jsonObject.getJSONArray("usages");
        for (Object usage : usages) {
            String filePath = getFilePath((JSONObject) usage);
            appendToFile(name, filePath);
        }
    }

    private static void appendToFile(String name, String filePath) throws IOException {
        File file = new File(filePath);
        file.createNewFile();
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file, true), StandardCharsets.UTF_8))) {
            writer.append(name);
            writer.append("\\n");
        }
    }

    private static String getFilePath(JSONObject usage) {
        String language = usage.getString("usage_full");
        String gender = usage.getString("usage_gender");
        return "src/main/resources/data/btn_rich/" + language + "_" + gender + ".txt";
    }
}
