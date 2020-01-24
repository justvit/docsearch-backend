package org.justvit.docsearch.model;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Criteria to search documents
 */
public class SearchCriteria {
    /**
     * Separated words that documents must contain
     */
    private List<Token> anyWord = new ArrayList<>();

    /**
     * Exact phrases that document must contain
     */
    private List<Phrase> anyPhrase = new ArrayList<>();

    /**
     * Separated words that documents must NOT contain
     */
    private List<Token> noWord = new ArrayList<>();

    /**
     * Exact phrases that document must NOT contain
     */
    private List<Phrase> noPhrase = new ArrayList<>();

    private List<Token> split(Collection<Phrase> phrases) {
        return phrases.stream()
                .map(Phrase::tokens)
                .reduce(new ArrayList<>(), (a, b) -> {
                    a.addAll(b);
                    return a;
                });
    }

    public List<Token> anyWord() {
        return anyWord;
    }

    public SearchCriteria appendAnyWord(Collection<Phrase> phrases) {
        List<Token> newTokens = split(phrases);
        anyWord = Lists.newArrayList(Iterables.concat(anyWord, newTokens));
        return this;
    }

    public SearchCriteria appendAnyWord(Phrase p) {
        anyWord = Lists.newArrayList(Iterables.concat(anyWord, p.tokens()));
        return this;
    }

    public List<Phrase> anyPhrase() {
        return anyPhrase;
    }

    public SearchCriteria appendAnyPhrase(Phrase p) {
        anyPhrase.add(p);
        return this;
    }

    public SearchCriteria appendAnyPhrase(List<Phrase> phrases) {
        anyPhrase.addAll(phrases);
        return this;
    }

    public List<Token> noWord() {
        return noWord;
    }

    public SearchCriteria appendNoWord(Phrase p) {
        noWord = Lists.newArrayList(Iterables.concat(noWord, p.tokens()));
        return this;
    }

    public SearchCriteria appendNoWord(Collection<Phrase> ps) {
        List<Token> newTokens = split(ps);

        noWord = Lists.newArrayList(Iterables.concat(noWord, newTokens));
        return this;
    }

    public List<Phrase> noPhrase() {
        return noPhrase;
    }

    public SearchCriteria appendNoPhrase(Phrase p) {
        noPhrase.add(p);
        return this;
    }

    public SearchCriteria appendNoPhrase(List<Phrase> ps) {
        noPhrase.addAll(ps);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchCriteria that = (SearchCriteria) o;
        return Objects.equal(anyWord, that.anyWord) &&
                Objects.equal(anyPhrase, that.anyPhrase) &&
                Objects.equal(noWord, that.noWord) &&
                Objects.equal(noPhrase, that.noPhrase);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(anyWord, anyPhrase, noWord, noPhrase);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SearchCriteria{");
        sb.append("anyWord=").append(anyWord);
        sb.append(", anyPhrase=").append(anyPhrase);
        sb.append(", noWord=").append(noWord);
        sb.append(", noPhrase=").append(noPhrase);
        sb.append('}');
        return sb.toString();
    }
}
