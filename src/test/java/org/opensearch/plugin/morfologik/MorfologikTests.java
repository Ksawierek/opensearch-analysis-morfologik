/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.plugin.morfologik;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.morfologik.MorfologikAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.opensearch.test.OpenSearchTestCase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MorfologikTests extends OpenSearchTestCase {

    // ── Plugin registration ──────────────────────────────────────────────────

    public void testPluginConstants() {
        assertEquals("morfologik", MorfologikPlugin.ANALYZER_NAME);
        assertEquals("morfologik_stem", MorfologikPlugin.FILTER_NAME);
    }

    public void testPluginRegistersAnalyzer() {
        MorfologikPlugin plugin = new MorfologikPlugin();
        assertEquals(1, plugin.getAnalyzers().size());
        assertTrue(plugin.getAnalyzers().containsKey(MorfologikPlugin.ANALYZER_NAME));
    }

    public void testPluginRegistersTokenFilter() {
        MorfologikPlugin plugin = new MorfologikPlugin();
        assertEquals(1, plugin.getTokenFilters().size());
        assertTrue(plugin.getTokenFilters().containsKey(MorfologikPlugin.FILTER_NAME));
    }

    // ── Lemmatization – nouns ────────────────────────────────────────────────

    /** "kotów" (genitive plural of "kot") → lemma "kot" */
    public void testLemmatizesGenitiveNoun() throws IOException {
        try (MorfologikAnalyzer analyzer = new MorfologikAnalyzer()) {
            assertContains(tokenize(analyzer, "kotów"), "kot");
        }
    }

    /** "pies" (nominative singular) should stay as-is */
    public void testNominativeNounUnchanged() throws IOException {
        try (MorfologikAnalyzer analyzer = new MorfologikAnalyzer()) {
            assertContains(tokenize(analyzer, "pies"), "pies");
        }
    }

    /** "psów" (genitive plural of "pies") → lemma "pies" */
    public void testLemmatizesGenitiveIrregularNoun() throws IOException {
        try (MorfologikAnalyzer analyzer = new MorfologikAnalyzer()) {
            assertContains(tokenize(analyzer, "psów"), "pies");
        }
    }

    // ── Lemmatization – verbs ────────────────────────────────────────────────

    /** "biegam" (1st person singular of "biegać") → lemma "biegać" */
    public void testLemmatizesVerb() throws IOException {
        try (MorfologikAnalyzer analyzer = new MorfologikAnalyzer()) {
            assertContains(tokenize(analyzer, "biegam"), "biegać");
        }
    }

    /** "chodzili" (past plural of "chodzić") → lemma "chodzić" */
    public void testLemmatizesPastVerb() throws IOException {
        try (MorfologikAnalyzer analyzer = new MorfologikAnalyzer()) {
            assertContains(tokenize(analyzer, "chodzili"), "chodzić");
        }
    }

    // ── Lemmatization – adjectives ───────────────────────────────────────────

    /** "szybkich" (genitive plural of "szybki") → lemma "szybki" */
    public void testLemmatizesAdjective() throws IOException {
        try (MorfologikAnalyzer analyzer = new MorfologikAnalyzer()) {
            assertContains(tokenize(analyzer, "szybkich"), "szybki");
        }
    }

    // ── Multi-word input ─────────────────────────────────────────────────────

    public void testMultipleWordsLemmatized() throws IOException {
        try (MorfologikAnalyzer analyzer = new MorfologikAnalyzer()) {
            List<String> tokens = tokenize(analyzer, "szybkich kotów");
            assertContains(tokens, "szybki");
            assertContains(tokens, "kot");
        }
    }

    // ── Edge cases ───────────────────────────────────────────────────────────

    /** Unknown words (not in dictionary) should still be returned as-is. */
    public void testUnknownWordPassedThrough() throws IOException {
        try (MorfologikAnalyzer analyzer = new MorfologikAnalyzer()) {
            List<String> tokens = tokenize(analyzer, "ksawierek");
            assertFalse("Unknown word should produce at least one token", tokens.isEmpty());
        }
    }

    public void testEmptyInputProducesNoTokens() throws IOException {
        try (MorfologikAnalyzer analyzer = new MorfologikAnalyzer()) {
            assertTrue(tokenize(analyzer, "").isEmpty());
        }
    }

    public void testWhitespaceOnlyProducesNoTokens() throws IOException {
        try (MorfologikAnalyzer analyzer = new MorfologikAnalyzer()) {
            assertTrue(tokenize(analyzer, "   ").isEmpty());
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static List<String> tokenize(Analyzer analyzer, String text) throws IOException {
        List<String> tokens = new ArrayList<>();
        try (TokenStream stream = analyzer.tokenStream("field", text)) {
            CharTermAttribute termAttr = stream.addAttribute(CharTermAttribute.class);
            stream.reset();
            while (stream.incrementToken()) {
                tokens.add(termAttr.toString());
            }
            stream.end();
        }
        return tokens;
    }

    private static void assertContains(List<String> tokens, String expected) {
        assertTrue("Expected token '" + expected + "' in " + tokens, tokens.contains(expected));
    }
}
