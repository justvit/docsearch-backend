package org.justvit.docsearch.service;

import org.justvit.docsearch.model.SearchCriteria;

/**
 * Parses user's search request into criteria
 */
public interface SearchRequestParser {
    SearchCriteria parse(String request) throws SearchRequestParsingException;
}
