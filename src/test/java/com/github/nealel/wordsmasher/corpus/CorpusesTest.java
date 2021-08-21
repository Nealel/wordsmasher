package com.github.nealel.wordsmasher.corpus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CorpusesTest {

    Corpuses corpuses;

    @BeforeEach
    void setUp() throws IOException {
        corpuses = new Corpuses(3, "src/test/resources/data");
    }
    

    @Test
    void whenListingCorpuses_topLevelParentIsPresent_andNamedCorrect() {
        assertThat(corpuses.getAvailableCorpuses(), hasItems("Europe", "Asia"));
    }

    @Test
    void whenListingCorpuses_midLevelParentIsPresent_andNamedCorrect() {
        assertThat(corpuses.getAvailableCorpuses(), hasItems(
                "Europe > English",
                "Asia > East > Japanese"));
    }

    @Test
    void whenListingCorpuses_individualFileIsPresent_andNamedCorrect() {
        assertThat(corpuses.getAvailableCorpuses(), hasItem("Europe > English > Numbers"));
    }

    @Test
    void whenListingCorpuses_topLevelParentsWhichAreTooSmall_areNotListed() {
        assertFalse(corpuses.getAvailableCorpuses().contains("American"));
    }

    @Test
    void whenListingCorpuses_midLevelParentsWhichAreTooSmall_areNotListed() {
        assertFalse(corpuses.getAvailableCorpuses().contains("American"));
        assertFalse(corpuses.getAvailableCorpuses().contains("American > Canadian"));
    }

    @Test
    void whenListingCorpuses_filesWhichAreTooSmall_areNotListed() {
        assertFalse(corpuses.getAvailableCorpuses().contains("American > Canadian > Names"));
    }

    @Test
    void whenListingFiles_allFilesAreCorrect_andInAlphabeticalOrder() {
        assertThat(corpuses.getAvailableCorpuses(), contains(
                "",
                "Asia",
                "Asia > East",
                "Asia > East > Japanese",
                "Asia > East > Japanese > Numbers",
                "Europe",
                "Europe > English",
                "Europe > English > Numbers",
                "Europe > French",
                "Europe > French > Numbers"));
    }

    @Test
    void whenRetrievingContentsOfCorpus_topLevelParents_containsAllChildren() {
        assertThat(corpuses.getContentsForCorpus("Europe"), containsInAnyOrder(
                "one", "two", "three",
                "un", "deux", "trois",
                "eins"
        ));
    }

    @Test
    void whenRetrievingContentsOfCorpus_midLevelParents_containsAllChildren() {
        assertThat(corpuses.getContentsForCorpus("Europe > English"), containsInAnyOrder(
                "one", "two", "three"
        ));
    }

    @Test
    void whenRetrievingContentsOfCorpus_individualFile_containsCorrectContent() {
        assertThat(corpuses.getContentsForCorpus("Europe > English > Numbers"), containsInAnyOrder(
                "one", "two", "three"
        ));
    }

}