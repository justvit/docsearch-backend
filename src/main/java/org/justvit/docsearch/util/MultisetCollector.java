package org.justvit.docsearch.util;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Stream collector into Google Guava Multiset a.k.a. "bag"
 * @param <T> type of an element
 */
public class MultisetCollector<T> implements Collector<T, Multiset<T>, Multiset<T>> {

    @Override
    public Supplier<Multiset<T>> supplier() {
        return HashMultiset::create;
    }

    @Override
    public BiConsumer<Multiset<T>, T> accumulator() {
        return (set, e) -> set.add(e, 1);
    }

    @Override
    public BinaryOperator<Multiset<T>> combiner() {
        return (set1, set2) -> {
            set1.addAll(set2);
            return set1;
        };
    }

    @Override
    public Function<Multiset<T>, Multiset<T>> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH));
    }
}