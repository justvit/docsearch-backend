package org.justvit.docsearch.service;

import com.google.common.base.Preconditions;
import org.justvit.docsearch.model.Token;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.justvit.docsearch.model.Token.Type.DELIMITER;
import static org.justvit.docsearch.model.Token.Type.WORD;

/**
 * Implementation of TokenConverter
 */
@Service
public class TokenConverterImpl implements TokenConverter {
    private static final Pattern TEXT_TO_TOKENS_REGEX = Pattern.compile("(\\w*)(\\W*)");
    private static final String EMPTY_STRING = "";

    @Override
    public List<Token> parseText(String text) {
        Preconditions.checkNotNull(text, "argument could not be null");

        List<Token> result = new ArrayList<>();
        Matcher matcher = TEXT_TO_TOKENS_REGEX.matcher(text);
        while (matcher.find()) {
            String word = matcher.group(1);
            String delimiter = matcher.group(2);
            if (word.length() > 0) {
                result.add(new Token(word, WORD));
            }
            if (delimiter.length() > 0) {
                result.add(new Token(delimiter, DELIMITER));
            }
        }

        return result;
    }

    @Override
    public String composeText(List<Token> tokens) {
        Preconditions.checkNotNull(tokens);

        return tokens.stream()
                .map(t -> t.getBody())
                .reduce(EMPTY_STRING, (a, b) -> a.concat(b));
    }
}
