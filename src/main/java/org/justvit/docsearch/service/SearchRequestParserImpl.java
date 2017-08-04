package org.justvit.docsearch.service;

import org.justvit.docsearch.model.Phrase;
import org.justvit.docsearch.model.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Implementation of the search request parser
 * that supports such kinds of requests:
 * <pre>
 *  1. simple:
 *      Lisp Common
 *
 *  2. with exact part:
 *      Interpreted "Thomas Eugene"
 *
 *  3. with negation:
 *      john -array
 * </pre>
 */
@Service
public class SearchRequestParserImpl implements SearchRequestParser {
    private static final Pattern DELIMITER_PATTERN = Pattern.compile("\\s*(?=-)|(?<=-)\\s*|\\s*(?=\"\\w)|(?<=\\w\")\\s*");
    private static final Pattern SIMPLE_PATTERN = Pattern.compile("[\\w\\s]+");
    private static final String QUOTE = "\"";
    private static final String MINUS = "-";
    private static final String EMPTY = "";

    boolean isPositive = true;

    @Autowired
    private PhraseConverter phraseConverter;

    @Override
    public SearchCriteria parse(String request) throws SearchRequestParsingException {
        SearchCriteria criteria = new SearchCriteria();
        Scanner scanner = new Scanner(request).useDelimiter(DELIMITER_PATTERN);

        while (scanner.hasNext()){
            String piece = scanner.next();

            if (piece.length() < 1) continue;

            if (SIMPLE_PATTERN.matcher(piece).matches()) {
                List<Phrase> ps = phraseConverter.parseText(piece);
                if (isPositive){
                    criteria.appendAnyWord(ps);
                } else {
                    criteria.appendNoWord(ps);
                    isPositive = true;
                }
            } else if (piece.equals(MINUS)){
                isPositive = false;
            } else if (piece.startsWith(QUOTE) && piece.endsWith(QUOTE)) {
                List<Phrase> ps = phraseConverter.parseText(piece.replaceAll(QUOTE, EMPTY));
                if (isPositive) {
                    criteria.appendAnyPhrase(ps);
                } else {
                    criteria.appendNoPhrase(ps);
                    isPositive = true;
                }
            } else {
                throw new SearchRequestParsingException("error in the search request: " + request);
            }
        }
        if (!isPositive) {
            throw new SearchRequestParsingException("search request ended unexpectedly: " + request);
        }

        return criteria;
    }
}
