package org.justvit.docsearch.service;

import org.justvit.docsearch.model.Document;
import org.justvit.docsearch.model.XDocument;

/**
 * Converts XDocument to Document and vise-versa
 */
public interface XDocumentConverter {
    /**
     * XDocument => Document
     */
    Document convert(XDocument xdocument);

    /**
     * Document => XDocument
     */
    XDocument convert(Document document);
}
