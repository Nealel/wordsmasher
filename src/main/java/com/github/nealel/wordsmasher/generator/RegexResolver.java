package com.github.nealel.wordsmasher.generator;

import java.util.regex.Pattern;

import static com.github.nealel.wordsmasher.generator.model.TransitionCountMatrix.END_SYMBOL;
import static com.github.nealel.wordsmasher.generator.model.TransitionCountMatrix.START_SYMBOL;

/**
 * A user-friendly regex that uses C for consonants, V for vowels, and X for either
 * C, V and X default to one-or-more matching (+)
 * but can be customized with * and {1,3}
 *
 */
public class RegexResolver {
    public static final String CONSONANT_SYMBOL = "C";
    public static final String VOWEL_SYMBOL = "V";
    public static final String ANY_SYMBOL = "X";

    private static final String CONSONANT_REGEX = "[a-z&&[^aeiou]]+";
    private static final String VOWEL_REGEX = "[aeiouy]+";
    private static final String ANY_LETTER_REGEX = ".+";

    public static Pattern toRegex(String simpleRegex) {
        String regex = simpleRegex.replaceAll(CONSONANT_SYMBOL, CONSONANT_REGEX)
                                .replaceAll(VOWEL_SYMBOL, VOWEL_REGEX)
                                .replaceAll(ANY_SYMBOL, ANY_LETTER_REGEX)
                                .replaceAll("\\+\\*", "*")
                                .replaceAll("\\+\\{", "{");

        return Pattern.compile(START_SYMBOL + regex + END_SYMBOL);
    }
}
