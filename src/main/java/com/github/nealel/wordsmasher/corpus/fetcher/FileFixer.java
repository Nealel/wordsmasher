package com.github.nealel.wordsmasher.corpus.fetcher;

import com.github.nealel.wordsmasher.corpus.FileCorpusLoader;
import org.apache.logging.log4j.util.Strings;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

public class FileFixer {

    public static final String DIR = "src/main/resources/data/btn_rich/small/";

    public static void main(String[] args) throws IOException {
        File dir = new File(DIR);
        List<File> files = Arrays.stream(dir.listFiles())
                .filter(File::isFile)
//                .filter(f -> lines(f) < 200)
                .sorted()
//                .filter(f -> f.getName().endsWith("_u.txt"))
                .filter(f -> f.getName().endsWith("_m.txt") || f.getName().endsWith("_f.txt") || f.getName().endsWith("_u.txt"))
                .collect(Collectors.toList());
//
        String summary = files.stream()
                .sorted(Comparator.comparing(File::getName))
//                .sorted(Comparator.comparingInt(FileFixer::lines))
                .map(f -> lines(f) + ": " + f.getName())
                .collect(joining("\n"));

        System.out.println(summary);
        System.out.println(files.size());
        for (File file : files) {
//            System.out.println(file.getName());
//            String rootFilename = file.getName().substring(0, file.getName().length() - 6);
//            String to = DIR + rootFilename + getSuffix(file) + ".txt";
//            copyTo(file, to);
            file.delete();
        }
    }

    static String getSuffix(File file) {
        String filename = file.getName();
        if (filename.endsWith("_f.txt")) {
            return " (Female)";
        }
        if (filename.endsWith("_m.txt")) {
            return " (Male)";
        }
        if (filename.endsWith("_u.txt")) {
            return "";
        }
        else return "";
    }

    private static void copyTo(File file, String to) throws IOException {
        Scanner scanner = new Scanner(new FileInputStream(file), StandardCharsets.UTF_8)
                .useDelimiter("\\n");

        Set<String> names = new HashSet<>();
        while (scanner.hasNext()) {
            names.add(scanner.next());
        }
        for (String name : names) {
            BtnFileWriter.appendToFile(name, to);
        }
    }

    private static int lines(File file) {
        try {
            Scanner scanner = new Scanner(new FileInputStream(file), StandardCharsets.UTF_8)
                    .useDelimiter("\\n");

            int i = 0;
            while (scanner.hasNext()) {
                String name = scanner.next();
                i++;
            }
            return i;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
