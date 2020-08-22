package com.github.nealel.wordsmasher.generator;

import java.util.regex.Pattern;

import static com.github.nealel.wordsmasher.model.TransitionCountMatrix.END_SYMBOL;
import static com.github.nealel.wordsmasher.model.TransitionCountMatrix.START_SYMBOL;

public class RegexResolver {
    private static final String CONSONANT = "[a-z&&[^aeiou]]";
    private static final String VOWEL = "[aeiou]";
    private static final String ANY = ".";
    public static final String PLUS = "+";

    public static Pattern toRegex(String simpleRegex) {
        String regex = simpleRegex.replaceAll("c", CONSONANT)
                .replaceAll("C", CONSONANT + PLUS)
                .replaceAll("v", VOWEL)
                .replaceAll("C", VOWEL + PLUS)
                .replaceAll("x", ANY)
                .replaceAll("X", ANY + PLUS);

        return Pattern.compile(START_SYMBOL + regex + END_SYMBOL);
    }
}
