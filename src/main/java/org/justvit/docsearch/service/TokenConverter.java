package org.justvit.docsearch.service;

import org.justvit.docsearch.model.Token;

import java.util.List;

/**
 * Converts text into a list of tokens and vice-versa
 */
public interface TokenConverter {

    List<Token> parseText(String text);

    String composeText(List<Token> tokens);
}
