package org.justvit.docsearch.model;

import com.google.common.base.Objects;
import com.google.errorprone.annotations.Immutable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Document
 * Represents an immutable document to be stored in the database and be searchable
 */
@Immutable
public class Document {
    private Phrase name;
    private List<Phrase> types;
    private List<Phrase> designers;
    private int hash;

    public Document(Phrase name, List<Phrase> types, List<Phrase> designers) {
        this.name = name;
        this.types = types;
        this.designers = designers;
    }

    public Phrase getName() {
        return name;
    }

    public List<Phrase> getTypes() {
        return types;
    }

    public List<Phrase> getDesigners() {
        return designers;
    }

    public Stream<Token> wordStream() {
        Stream<Token> typeTokenStream = types.stream().flatMap(p -> p.tokens().stream());
        Stream<Token> designerTokenStream = designers.stream().flatMap(p -> p.tokens().stream());
        return Stream.concat(name.tokens().stream(), Stream.concat(typeTokenStream, designerTokenStream))
                .filter(t -> t.getType() == Token.Type.WORD);
    }

    public Stream<Phrase> ngramStream(int n) {
        Stream.Builder<Phrase> builder = Stream.builder();
        List<Token> tuple = new ArrayList<>(n);

        wordStream().forEach(w -> {
            if (tuple.size() < n) {
                tuple.add(w);
            } else {
                builder.add(new Phrase(tuple));
                ArrayList<Token> firstSkipped = new ArrayList<>(tuple.subList(1, n));
                tuple.clear();
                tuple.addAll(firstSkipped);
                tuple.add(w);
            }
        });

        builder.add(new Phrase(tuple));

        return builder.build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document that = (Document) o;

        if (this.hashCode() != that.hashCode()) return false;

        return Objects.equal(name, that.name) &&
                Objects.equal(types, that.types) &&
                Objects.equal(designers, that.designers);
    }

    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = Objects.hashCode(name, types, designers);
        }
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Document{");
        sb.append("name=").append(name);
        sb.append(", types=").append(types);
        sb.append(", designers=").append(designers);
        sb.append(", hash=").append(hash);
        sb.append('}');
        return sb.toString();
    }
}
