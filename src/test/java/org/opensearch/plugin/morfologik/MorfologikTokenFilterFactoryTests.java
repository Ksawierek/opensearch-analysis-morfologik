/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.plugin.morfologik;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.opensearch.common.settings.Settings;
import org.opensearch.env.Environment;
import org.opensearch.env.TestEnvironment;
import org.opensearch.index.IndexSettings;
import org.opensearch.test.IndexSettingsModule;
import org.opensearch.test.OpenSearchTestCase;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class MorfologikTokenFilterFactoryTests extends OpenSearchTestCase {

    private IndexSettings indexSettings;
    private Environment env;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        indexSettings = IndexSettingsModule.newIndexSettings("test", Settings.EMPTY);
        Settings nodeSettings = Settings.builder()
            .put(Environment.PATH_HOME_SETTING.getKey(), createTempDir())
            .build();
        env = TestEnvironment.newEnvironment(nodeSettings);
    }

    // ── Constants ────────────────────────────────────────────────────────────

    public void testDictionaryParamConstant() {
        assertEquals("dictionary", MorfologikTokenFilterFactory.DICTIONARY_PARAM);
    }

    // ── Default (Polish) dictionary ──────────────────────────────────────────

    public void testDefaultDictionaryCreatesFactory() throws IOException {
        MorfologikTokenFilterFactory factory = new MorfologikTokenFilterFactory(
            indexSettings, env, MorfologikPlugin.FILTER_NAME, Settings.EMPTY
        );
        assertNotNull(factory);
    }

    public void testDefaultDictionaryCreatesTokenStream() throws IOException {
        MorfologikTokenFilterFactory factory = new MorfologikTokenFilterFactory(
            indexSettings, env, MorfologikPlugin.FILTER_NAME, Settings.EMPTY
        );
        WhitespaceTokenizer tokenizer = new WhitespaceTokenizer();
        tokenizer.setReader(new StringReader("kotów"));
        TokenStream stream = factory.create(tokenizer);
        assertNotNull(stream);
        stream.close();
    }

    /** Filter with default dictionary should lemmatize Polish words. */
    public void testDefaultDictionaryLemmatizes() throws IOException {
        MorfologikTokenFilterFactory factory = new MorfologikTokenFilterFactory(
            indexSettings, env, MorfologikPlugin.FILTER_NAME, Settings.EMPTY
        );
        WhitespaceTokenizer tokenizer = new WhitespaceTokenizer();
        tokenizer.setReader(new StringReader("kotów"));
        try (TokenStream stream = factory.create(tokenizer)) {
            List<String> tokens = drain(stream);
            assertTrue("Expected lemma 'kot' in " + tokens, tokens.contains("kot"));
        }
    }

    // ── Missing / invalid dictionary file ────────────────────────────────────

    public void testMissingDictionaryFileThrowsIOException() {
        Settings settings = Settings.builder()
            .put(MorfologikTokenFilterFactory.DICTIONARY_PARAM, "nonexistent.dict")
            .build();
        expectThrows(IOException.class, () ->
            new MorfologikTokenFilterFactory(indexSettings, env, MorfologikPlugin.FILTER_NAME, settings)
        );
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private static List<String> drain(TokenStream stream) throws IOException {
        List<String> tokens = new ArrayList<>();
        CharTermAttribute termAttr = stream.addAttribute(CharTermAttribute.class);
        stream.reset();
        while (stream.incrementToken()) {
            tokens.add(termAttr.toString());
        }
        stream.end();
        return tokens;
    }
}
