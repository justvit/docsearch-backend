package org.justvit.docsearch.model;

import com.google.common.base.Objects;
import com.google.errorprone.annotations.Immutable;

/**
 * Tokens from which a text made of
 */
@Immutable
public class Token {

    public enum Type { WORD, DELIMITER }

    private final String body;
    private final Type type;
    private int hash;

    public Token(String body, Type type) {
        this.body = body;
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Token that = (Token) o;
        if (this.hashCode() != that.hashCode()) return false;
        return Objects.equal(body, that.body) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = Objects.hashCode(body, type);
        }
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Token(");
        sb.append("\"").append(body).append('\"');
        sb.append(", ").append(type);
        sb.append(')');
        return sb.toString();
    }
}
