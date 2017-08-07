package org.justvit.docsearch.service;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.tuple.Pair;
import org.justvit.docsearch.model.*;
import org.justvit.docsearch.repository.DocumentRepository;
import org.justvit.docsearch.util.Nlp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of DocumentService
 */
@Service
public class DocumentServiceImpl implements DocumentService {

    @Value("${data.file}")
    private String dataFilePath;

    @Autowired
    private XDocumentLoader xdocLoader;

    @Autowired
    private XDocumentConverter xdocConverter;

    @Autowired
    private DocumentRepository docRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(Document document) {
        docRepo.add(document);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Document> search(SearchCriteria searchCriteria) {
        //** first, perform the simplest search in the repo, for words that may present
        Map<Document, Double> positives = docRepo.searchAnyWord(searchCriteria.anyWord());

        //** second, perform the simple negative search in the repo, for words that mustn't present
        Map<Document, Double> onlyPositives = dropDocsWithNegativeWords(positives, searchCriteria.noWord());

        //** search for words of exact phrases in the repo
        Map<Document, Double> strictPositives = dropDocsWithoutPhrases(onlyPositives, searchCriteria.anyPhrase());

        //** fourth, get rid of documents that contains phrases that mustn't occurr
        Map<Document, Double> onlyStrictPositives = dropDocsWithNegativePhrases(strictPositives, searchCriteria.noPhrase());

        return Nlp.sortByWeight(onlyStrictPositives).stream()
                .map(Pair::getKey)
                .collect(Collectors.toList());
    }

    /**
     * For every phrase, searches the repo for documents that contain any word of the phrase,
     * so the phrase is treated like a `bag of words`
     * @param phrases
     * @return set of the phrases mapped to found documents
     */
    private Map<Phrase, Map<Document, Double>> searchForSeparateWords(List<Phrase> phrases) {
        return phrases.stream()
                .map(phrase -> Pair.of(phrase, docRepo.searchAnyWord(phrase.tokens())))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    /**
     * Joins two sets of weighed documents into a new set,
     *      but drops weights of the second set, replaces them with zeroes
     * @param weighedDocs first set
     * @param zeroWeightDocs second set
     * @return joined set
     */
    private Map<Document, Double> joinWithZeroWeight(Map<Document, Double> weighedDocs, Map<Document, Double> zeroWeightDocs) {
        Set<Document> docSet = Sets.union(weighedDocs.keySet(), zeroWeightDocs.keySet());
        return docSet.stream()
                .map(doc -> Pair.of(doc, weighedDocs.getOrDefault(doc, 0.0)))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    /**
     * Searches the repository for documents with negative words and removes the found docs from {@code positives}
     * @param positives
     * @param negWords negative words
     * @return
     */
    private Map<Document, Double> dropDocsWithNegativeWords(Map<Document, Double> positives, List<Token> negWords) {
        if (!negWords.isEmpty()) {
            // search repo for docs with negative words...
            Map<Document, Double> negatives = docRepo.searchAnyWord(negWords);

            // noNegatives = positives - negatives
            Set<Document> noNegatives = Sets.difference(positives.keySet(), negatives.keySet());
            return positives.entrySet().stream()
                    .filter(wdoc -> noNegatives.contains(wdoc.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        return positives;
    }

    /**
     * Returns {@code positives} without documents that not contain the exact phrases
     * @param positives
     * @param exactPhrases
     * @return
     */
    private Map<Document, Double> dropDocsWithoutPhrases(Map<Document, Double> positives, List<Phrase> exactPhrases) {
        if (!exactPhrases.isEmpty()) {
            // search the repo for documents containing any words of the phrases...
            Map<Phrase, Map<Document, Double>> phrase2DocsWithWords = searchForSeparateWords(exactPhrases);

            // ...then separated them into ngrams and search these ngrams for the exact phrases
            Map<Phrase, Map<Document, Double>> phrase2docs = phrase2DocsWithWords.entrySet().stream()
                                .map(pwdocs -> {
                                    Phrase phrase = pwdocs.getKey();
                                    Map<Document, Double> candidates = joinWithZeroWeight(positives, pwdocs.getValue());
                                    Map<Document, Double> stricts
                                            = candidates.entrySet().stream()
                                            .filter(wdoc -> wdoc.getKey().ngramStream(phrase.wordCount())
                                                                         .anyMatch(ngram -> ngram.equals(phrase)))
                                            // assign the heaviest weight to docs that contain exact phrase
                                            .map(wdoc -> Pair.of(wdoc.getKey(), 1.0))
                                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                                    return Pair.of(phrase, stricts);
                                })
                                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

            return phrase2docs.entrySet().stream()
                            .map(Map.Entry::getValue)
                            .reduce(positives, (a,b) -> {
                                Set<Document> docs = Sets.intersection(a.keySet(), b.keySet());
                                return docs.stream()
                                            .map(doc -> {
                                                double w1 = a.getOrDefault(doc, 0.0);
                                                double w2 = b.getOrDefault(doc, 0.0);
                                                return Pair.of(doc, Math.max(w1, w2));
                                            })
                                            .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
                            });
        }

        return positives;
    }

    /**
     * Returns {@code positives} without documents containing the negative phrases
     * @param positives
     * @param negPhrases
     * @return
     */
    private Map<Document, Double> dropDocsWithNegativePhrases(Map<Document, Double> positives, List<Phrase> negPhrases) {
        return positives.entrySet().stream()
                .filter(wdoc -> negPhrases.stream()
                        .noneMatch(phrase -> wdoc.getKey().ngramStream(phrase.wordCount())
                                .anyMatch(ngram -> ngram.equals(phrase))
                        )
                )
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Initializes the document repository with data from the JSON file
     */
    @PostConstruct
    private void initRepo() {
        URL dataUrl = ClassLoader.getSystemClassLoader().getResource(dataFilePath);
        if (dataUrl == null){
            throw new RuntimeException("Could not find data file: " + dataFilePath);
        }

        List<XDocument> xdocs;
        try {
            xdocs = xdocLoader.load(new FileReader(dataUrl.getPath()));
        } catch (IOException e) {
            throw new RuntimeException("Could not read data file: " + dataFilePath);
        }

        xdocs.forEach(xdoc -> this.add(xdocConverter.convert(xdoc)));
        docRepo.prepareForSearching();
    }
}
