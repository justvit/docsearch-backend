package org.justvit.docsearch.service;

import org.justvit.docsearch.model.XDocument;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * Loads XDocuments from external source
 */
public interface XDocumentLoader {
    List<XDocument> load(Reader source) throws IOException;
}
