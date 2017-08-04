package org.justvit.docsearch.repository;

import org.justvit.docsearch.model.Document;
import org.justvit.docsearch.model.Token;

import java.util.List;
import java.util.Map;

/**
 * Repository of documents
 */
public interface DocumentRepository {
    /**
     * Adds document to repository and includes it into search indices
     * @param document
     */
    void add(Document document);

    /**
     * Finds documents that contains any of the given words, ranked the found documents by the relevance
     * @param words words that documents may contain
     * @return set of pairs (document, relevance)
     */
    Map<Document, Double> searchAnyWord(List<Token> words);

    /**
     * Counts indexed words
     * @return
     */
    int countWords();

    /**
     * Counts indexed documents
     * @return
     */
    int countDocuments();

    /**
     * Prepares the repo for searching, i.e. creates necessary indexes, frequency tables, etc.
     * NB!: It's necessary to call it before searching!
     */
    void prepareForSearching();
}
