/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.plugin.morfologik;

import org.apache.lucene.analysis.morfologik.MorfologikAnalyzer;
import org.opensearch.common.settings.Settings;
import org.opensearch.index.IndexSettings;
import org.opensearch.test.IndexSettingsModule;
import org.opensearch.test.OpenSearchTestCase;

public class MorfologikAnalyzerProviderTests extends OpenSearchTestCase {

    private static MorfologikAnalyzerProvider newProvider() {
        IndexSettings indexSettings = IndexSettingsModule.newIndexSettings("test", Settings.EMPTY);
        return new MorfologikAnalyzerProvider(indexSettings, MorfologikPlugin.ANALYZER_NAME, Settings.EMPTY);
    }

    public void testGetReturnsNonNull() {
        assertNotNull(newProvider().get());
    }

    public void testGetReturnsMorfologikAnalyzer() {
        assertTrue(newProvider().get() instanceof MorfologikAnalyzer);
    }

    /** Provider must return the same instance on every call (no re-creation). */
    public void testGetReturnsSameInstance() {
        MorfologikAnalyzerProvider provider = newProvider();
        assertSame(provider.get(), provider.get());
    }
}
