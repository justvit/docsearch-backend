package org.justvit.docsearch.service;

import org.justvit.docsearch.model.Document;
import org.justvit.docsearch.model.SearchCriteria;

import java.util.List;

/**
 * Service to deal with documents
 */
public interface DocumentService {
    /**
     * Includes the document into processing
     * @param document
     */
    void add(Document document);

    /**
     * List documents that match to the search criteria, sorted by relevance
     * @param searchCriteria
     * @return
     */
    List<Document> search(SearchCriteria searchCriteria);
}
