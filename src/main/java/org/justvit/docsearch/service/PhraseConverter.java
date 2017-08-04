package org.justvit.docsearch.service;

import org.justvit.docsearch.model.Phrase;
import org.justvit.docsearch.model.Token;

import java.util.List;

/**
 * Converts text into a list of phrases and vice-versa
 */
public interface PhraseConverter {

    List<Phrase> parseText(String text);

    String composeText(List<Phrase> phrases);

    String composeText(Phrase phrase);
}
