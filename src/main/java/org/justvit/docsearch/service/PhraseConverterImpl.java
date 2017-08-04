package org.justvit.docsearch.service;

import org.justvit.docsearch.model.Phrase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Converts plain text to phrases
 */
@Service
public class PhraseConverterImpl implements PhraseConverter {
    private static final String PHRASE_DELIMITER = ", ";
    private static final Pattern PHRASE_DELIMITER_PATTERN = Pattern.compile("\\s*,\\s*");

    @Autowired
    private TokenConverter tokenConverter;

    @Override
    public List<Phrase> parseText(String text) {
        return Arrays.stream(PHRASE_DELIMITER_PATTERN.split(text))
                .map(s -> new Phrase(tokenConverter.parseText(s)))
                .collect(Collectors.toList());
    }

    @Override
    public String composeText(List<Phrase> phrases) {
        return phrases.stream()
                .map(p -> tokenConverter.composeText(p.tokens()))
                .reduce((a,b) -> a.concat(PHRASE_DELIMITER).concat(b))
                .orElse("");
    }

    @Override
    public String composeText(Phrase phrase) {
        return tokenConverter.composeText(phrase.tokens());
    }
}
