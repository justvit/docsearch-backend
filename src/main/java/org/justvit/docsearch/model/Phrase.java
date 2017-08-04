package org.justvit.docsearch.model;

import com.google.errorprone.annotations.Immutable;

import java.util.*;

/**
 * Phrase of a Document
 */
@Immutable
public class Phrase {
    private final List<Token> tokens;
    private int hash;

    public Phrase(Token... tokens) {
        this.tokens = Arrays.asList(tokens);
    }

    public Phrase(Collection<Token> tokens) {
        this.tokens = new ArrayList<>(tokens);
    }

    /**
     * List tokens
     * @return immutable list of tokens
     */
    public List<Token> tokens() {
        return Collections.unmodifiableList(tokens);
    }

    /**
     * Counts words (only words!) of the phrase
     * @return number of words in the phrase
     */
    public int wordCount(){
        return (int) tokens().stream()
                .filter(t -> t.getType() == Token.Type.WORD)
                .count();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Phrase that = (Phrase) o;

        if (this.hashCode() != that.hashCode()) return false;

        return tokens.equals(that.tokens);
    }

    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = tokens.hashCode();
        }
        return hash;
    }

    @Override
    public String toString() {
        return "Phrase{" + tokens + "}";
    }

}
