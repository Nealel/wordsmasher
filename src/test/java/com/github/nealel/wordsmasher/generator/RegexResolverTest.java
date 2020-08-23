package com.github.nealel.wordsmasher.generator;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class RegexResolverTest {

    @Test
    public void CVC() {
        Pattern regex = RegexResolver.toRegex("CVC");
        assertThat("bob", matchesPattern(regex));
        assertThat("phill", matchesPattern(regex));
        assertThat("paul", matchesPattern(regex));
    }

    @Test
    public void X() {
        Pattern regex = RegexResolver.toRegex("X");
        assertThat("abcdefg", matchesPattern(regex));
        assertThat("a", matchesPattern(regex));
    }

    @Test
    public void aX() {
        Pattern regex = RegexResolver.toRegex("aX");
        assertThat("anya", matchesPattern(regex));
        assertThat("anastasia", matchesPattern(regex));
        assertThat("alastair", matchesPattern(regex));
    }

    @Test
    public void cCVy() {
        Pattern regex = RegexResolver.toRegex("cVCy");
        assertThat("cindy", matchesPattern(regex));
        assertThat("carly", matchesPattern(regex));
    }

    @Test
    public void annVSTAR() {
        Pattern regex = RegexResolver.toRegex("annV*");
        assertThat("ann", matchesPattern(regex));
        assertThat("annie", matchesPattern(regex));
        assertThat("anne", matchesPattern(regex));
    }

    @Test
    public void annV1() {
        Pattern regex = RegexResolver.toRegex("annV{1}");
        assertThat("anne", matchesPattern(regex));
        assertThat("anny", matchesPattern(regex));
    }

    @Test
    public void regexWithNumbers() {
        Pattern regex = RegexResolver.toRegex("C{2,3}V{1,2}C{1}V*");
        assertThat("shauna", matchesPattern(regex));
        assertThat("chris", matchesPattern(regex));
    }

}