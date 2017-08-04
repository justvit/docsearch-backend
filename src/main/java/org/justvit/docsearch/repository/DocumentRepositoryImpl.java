package org.justvit.docsearch.repository;

import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.tuple.Pair;
import org.justvit.docsearch.model.Document;
import org.justvit.docsearch.model.Token;
import org.justvit.docsearch.util.MultisetCollector;
import org.justvit.docsearch.util.Nlp;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of Document repository based on the TF-IDF approach.
 * Makes:
 *      1. direct index (document to bag of words);
 *      2. inverted index (word to documents);
 *
 * Calculates:
 *      1. TF
 *      2. IDF
 *      3. TFxIDF
 */
@Repository
public class DocumentRepositoryImpl implements DocumentRepository {
    private static final MultisetCollector<String> MULTISET_COLLECTOR = new MultisetCollector<>();
    private static final Multiset<String> EMPTY_MULTISET = ImmutableMultiset.of();

    /** direct index (a document mapped to a bag of words) */
    private Map<Document, Multiset<String>> doc2words = new HashMap<>();

    /** inverted index (a word mapped to documents) */
    private Map<String, Set<Document>> word2docs = new HashMap<>();

    /** counts of words in every document */
    private Map<Document, Integer> wordCount = new HashMap<>();

    /** IDF for every word */
    private Map<String, Double> idf;

    /** TF-IDF for every word in every bag [doc => set(word, TFxIDF)] */
    private Map<Document, Map<String, Double>> docWordFreq;

    /** TF-IDF for every word in every document [word => set(doc, TFxIDF)] */
    private Map<String, Map<Document, Double>> wordDocFreq; // tf-idf for words

    /** is the repo is ready for searching? */
    private boolean ready;

    public boolean isReady(){
        return ready;
    }

    /**
     * {@inheritDoc}
     */
    public void add(Document document){
        ready = false;

        Multiset<String> words = document.wordStream()
                .map(w -> w.getBody().toLowerCase())
                .collect(MULTISET_COLLECTOR);

        doc2words.put(document, words);

        words.elementSet()
                .forEach(w -> word2docs.computeIfAbsent(w, k -> Sets.newHashSet()).add(document));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareForSearching() {
        // first, calculate TF for every word of every document
        Map<Document, Map<String, Double>> tf
                = doc2words.entrySet().stream()
                    // counting word in the document and cacheing the number
                    .peek(e -> wordCount.put(e.getKey(), countWordsInDoc(e.getKey())))
                    .map(e -> {
                        Document doc = e.getKey();
                        Multiset<String> words = e.getValue();

                        // calc TF for every word of the document
                        Map<String, Double> tf0 = words.entrySet().stream()
                                .map(we -> Pair.of(we.getElement(), ((double) we.getCount()) / wordCount.get(doc)))
                                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

                        return Pair.of(doc, tf0);
                    })
                    .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

        // second, calculate IDF for every word
        final int corpusSize = doc2words.keySet().size();

        idf = word2docs.entrySet().stream()
                .map(e -> {
                    String word = e.getKey();
                    int docCount = e.getValue().size();

                    return Pair.of(word, Math.log10(((double) corpusSize) / docCount));
                })
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

        // third, multiply TF and IDF
        this.docWordFreq = tf.entrySet().stream()
                .map(de -> {
                    de.setValue(de.getValue()
                                    .entrySet().stream()
                                        .map(we -> {
                                            String word = we.getKey();
                                            we.setValue(we.getValue() * idf.getOrDefault(word, 0.0));
                                            return we;
                                        })
                                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
                    return de;
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // fourth, create table [word => set(doc, freq)]
        wordDocFreq = new HashMap<>();
        docWordFreq.forEach((doc, value) -> value.entrySet().stream()
                                                    .map(we -> Pair.of(we.getKey(), we.getValue()))
                                                    .forEach(p -> wordDocFreq.computeIfAbsent(p.getKey(),
                                                                                              k -> new HashMap<>())
                                                                             .put(doc, p.getValue())
                                                    )
        );

        ready = true;
    }

    private int countWordsInDoc(Document document) {
        return doc2words.getOrDefault(document, EMPTY_MULTISET).entrySet().stream()
                .mapToInt(Multiset.Entry::getCount)
                .sum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countWords() {
        return word2docs.keySet().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countDocuments() {
        return doc2words.keySet().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Document, Double> searchAnyWord(List<Token> query) {
        if (!ready) {
            throw new DocumentRepositoryException("the document repository is not ready to work, you should prepare it first");
        }

        // split query into words
        List<String> qwords = query.stream()
                .filter(t -> t.getType() == Token.Type.WORD)
                .map(t -> t.getBody().toLowerCase())
                .collect(Collectors.toList());

        // find docs that contain the words
        Set<Document> relatedDocs = qwords.stream()
                .map(w -> word2docs.getOrDefault(w, Collections.emptySet()))
                .reduce(Collections.emptySet(), (a, b) -> Sets.union(a, b));

        // now, make vectors from the query for every related doc
        Map<String, Double> queryVector = qwords.stream()
                .map(w -> Pair.of(w, idf.getOrDefault(w, 0.0)))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));

        Map<String, Double> normQueryVector = Nlp.normalize(queryVector);

        return relatedDocs.stream()
                // ...and vectors from every doc
                .map(doc -> {
                            // create a vector for the document
                            Map<String, Double> docVector
                                    = docWordFreq.get(doc)
                                          .entrySet().stream()
                                                .filter(e -> qwords.contains(e.getKey()))
                                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                            Map<String, Double> normDocVector = Nlp.normalize(docVector);

                            double cosValue = Nlp.cosine(normQueryVector, normDocVector);
                            return Pair.of(doc, cosValue);
                        })
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }
}
