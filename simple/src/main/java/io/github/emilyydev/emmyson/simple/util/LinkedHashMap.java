//
// Simple implementation for the EmmySON API
// Copyright (C) 2022  emilyy-dev
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.
//

package io.github.emilyydev.emmyson.simple.util;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

public interface LinkedHashMap<K, V> extends Iterable<Map.Entry<K, V>>, Serializable {

  @SuppressWarnings("unchecked")
  static <K, V> LinkedHashMap<K, V> empty() {
    return (LinkedHashMap<K, V>) LinkedHashMap$.EmptyNode.INSTANCE;
  }

  static <K, V> LinkedHashMap<K, V> single(final K key, final V value) {
    return new LinkedHashMap$.SingleNode<>(key, value);
  }

  static <K, V> LinkedHashMap<K, V> single(final Map.Entry<? extends K, ? extends V> entry) {
    return new LinkedHashMap$.SingleNode<>(entry);
  }

  static <K, V> LinkedHashMap<K, V> ofAll(final Map<? extends K, ? extends V> elements) {
    if (elements.isEmpty()) {
      return empty();
    } else if (elements.size() == 1) {
      return single(elements.entrySet().iterator().next());
    } else {
      return new LinkedHashMap$.SingleNode<>(elements);
    }
  }

  static <K, V> LinkedHashMap<K, V> ofAll(final Collection<? extends Map.Entry<? extends K, ? extends V>> elements) {
    if (elements.isEmpty()) {
      return empty();
    } else if (elements.size() == 1) {
      return single(elements.iterator().next());
    } else {
      return new LinkedHashMap$.SingleNode<>(elements);
    }
  }

  @SafeVarargs
  static <K, V> LinkedHashMap<K, V> ofAll(final Map.Entry<? extends K, ? extends V>... elements) {
    if (elements.length == 0) {
      return empty();
    } else if (elements.length == 1) {
      return single(elements[0]);
    } else {
      return new LinkedHashMap$.SingleNode<>(elements);
    }
  }

  boolean isEmpty();
  int size();

  Optional<V> get(K key);

  LinkedHashMap<K, V> withMapping(K key, V value);
  LinkedHashMap<K, V> remove(K key);

  Stream<Map.Entry<K, V>> stream();

  @Override
  default @NotNull Iterator<Map.Entry<K, V>> iterator() {
    return stream().iterator();
  }

  @Override
  default Spliterator<Map.Entry<K, V>> spliterator() {
    return Spliterators.spliterator(
        iterator(), size(),
        Spliterator.ORDERED
        | Spliterator.SIZED
        | Spliterator.NONNULL
        | Spliterator.IMMUTABLE
        | Spliterator.SUBSIZED
        | Spliterator.DISTINCT
    );
  }

  void forEach(BiConsumer<? super K, ? super V> action);

  default String asString() {
    return getClass().getSimpleName();
  }
}

final class LinkedHashMap$ {

  private LinkedHashMap$() {
    throw new UnsupportedOperationException("you stink");
  }

  static final class EmptyNode<K, V> implements LinkedHashMap<K, V> {

    static final LinkedHashMap<?, ?> INSTANCE = new EmptyNode<>();

    private static final long serialVersionUID = 6522115239312680204L;

    private EmptyNode() { }

    @Override
    public boolean isEmpty() {
      return true;
    }

    @Override
    public int size() {
      return 0;
    }

    @Override
    public Optional<V> get(final K key) {
      requireNonNull(key, "key");
      return Optional.empty();
    }

    @Override
    public LinkedHashMap<K, V> withMapping(final K key, final V value) {
      return LinkedHashMap.single(key, value);
    }

    @Override
    public LinkedHashMap<K, V> remove(final K key) {
      requireNonNull(key, "key");
      return this;
    }

    @Override
    public Stream<Map.Entry<K, V>> stream() {
      return Stream.empty();
    }

    @Override
    public @NotNull Iterator<Map.Entry<K, V>> iterator() {
      return Collections.emptyIterator();
    }

    @Override
    public Spliterator<Map.Entry<K, V>> spliterator() {
      return Spliterators.emptySpliterator();
    }

    @Override
    public void forEach(final BiConsumer<? super K, ? super V> action) { }

    @Override
    public boolean equals(final Object obj) {
      return obj instanceof EmptyNode<?, ?>;
    }

    @Override
    public int hashCode() {
      return 0;
    }

    @Override
    public String toString() {
      return asString();
    }

    private Object readResolve() {
      return INSTANCE;
    }
  }

  static abstract class AbstractNode<K, V> implements LinkedHashMap<K, V> {

    private static boolean contentEquals(final AbstractNode<?, ?> first, final AbstractNode<?, ?> second) {
      final Iterator<? extends Map.Entry<?, ?>> it1 = first.iterator();
      final Iterator<? extends Map.Entry<?, ?>> it2 = second.iterator();
      while (it1.hasNext() && it2.hasNext()) {
        if (!it1.next().equals(it2.next())) {
          return false;
        }
      }
      return true;
    }

    @Override
    public final boolean isEmpty() {
      return false;
    }

    @Override
    public final LinkedHashMap<K, V> withMapping(final K key, final V value) {
      return get(key).map(existingValue -> {
        if (value.equals(existingValue)) { return this; }
        return new ReplacingNode<>(this, key, value);
      }).orElseGet(() -> new LinkedNode<>(this, key, value));
    }

    @Override
    public final LinkedHashMap<K, V> remove(final K key) {
      return get(key).map($ -> {
        if (size() == 1) { return LinkedHashMap.<K, V>empty(); }
        return new RemovingNode<>(this, key);
      }).orElse(this);
    }

    @Override
    public final void forEach(final BiConsumer<? super K, ? super V> action) {
      forEach(entry -> action.accept(entry.getKey(), entry.getValue()));
    }

    @Override
    public final void forEach(final Consumer<? super Map.Entry<K, V>> action) {
      iterator().forEachRemaining(action);
    }

    @Override
    public final boolean equals(final Object other) {
      return other instanceof AbstractNode<?, ?>
             && size() == ((AbstractNode<?, ?>) other).size()
             && contentEquals(this, (AbstractNode<?, ?>) other);
    }

    @Override
    public final int hashCode() {
      return stream().mapToInt(Map.Entry::hashCode).reduce(1, (result, hashCode) -> result * 31 + hashCode);
    }

    @Override
    public final String toString() {
      return asString();
    }
  }

  // Not "single-element node", but rather not linked
  static final class SingleNode<K, V> extends AbstractNode<K, V> {

    private static final long serialVersionUID = -6854208052858189573L;

    private final Map<K, V> elements;

    SingleNode(final K key, final V value) {
      this.elements = Map.of(key, value);
    }

    SingleNode(final Map.Entry<? extends K, ? extends V> entry) {
      this.elements = Map.of(entry.getKey(), entry.getValue());
    }

    @SafeVarargs
    SingleNode(final Map.Entry<? extends K, ? extends V>... entries) {
      this(Arrays.asList(entries));
    }

    SingleNode(final Map<? extends K, ? extends V> elements) {
      this(elements.entrySet());
    }

    SingleNode(final Collection<? extends Map.Entry<? extends K, ? extends V>> elements) {
      this.elements = elements.stream()
          .collect(collectingAndThen(
              toMap(Map.Entry::getKey, Map.Entry::getValue, (lhs, rhs) -> lhs, java.util.LinkedHashMap::new),
              Collections::unmodifiableMap
          ));
    }

    @Override
    public int size() {
      return this.elements.size();
    }

    @Override
    public Optional<V> get(final K key) {
      requireNonNull(key, "key");
      return Optional.ofNullable(this.elements.get(key));
    }

    @Override
    public Stream<Map.Entry<K, V>> stream() {
      return this.elements.entrySet().stream();
    }

    @Override
    public @NotNull Iterator<Map.Entry<K, V>> iterator() {
      return this.elements.entrySet().iterator();
    }
  }

  static final class LinkedNode<K, V> extends AbstractNode<K, V> {

    private static final long serialVersionUID = 396087743908116680L;

    private final LinkedHashMap<K, V> elements;
    private final int hashCode;
    private final K key;
    private final V value;

    LinkedNode(final LinkedHashMap<K, V> elements, final K key, final V value) {
      this.elements = elements;
      this.hashCode = key.hashCode();
      this.key = key;
      this.value = requireNonNull(value, "value");
    }

    @Override
    public int size() {
      return this.elements.size() + 1;
    }

    @Override
    public Optional<V> get(final K key) {
      if (key.hashCode() == this.hashCode && key.equals(this.key)) { return Optional.of(this.value); }
      return this.elements.get(key);
    }

    @Override
    public Stream<Map.Entry<K, V>> stream() {
      return Stream.concat(
          this.elements.stream(),
          Stream.of(Map.entry(this.key, this.value))
      );
    }
  }

  static final class ReplacingNode<K, V> extends AbstractNode<K, V> {

    private static final long serialVersionUID = 7566370458510469889L;

    private final LinkedHashMap<K, V> elements;
    private final int hashCode;
    private final K key;
    private final V value;

    ReplacingNode(final LinkedHashMap<K, V> elements, final K key, final V value) {
      this.elements = elements;
      this.hashCode = key.hashCode();
      this.key = key;
      this.value = requireNonNull(value, "value");
    }

    @Override
    public int size() {
      return this.elements.size();
    }

    @Override
    public Optional<V> get(final K key) {
      if (key.hashCode() == this.hashCode && key.equals(this.key)) { return Optional.of(this.value); }
      return this.elements.get(key);
    }

    @Override
    public Stream<Map.Entry<K, V>> stream() {
      return Stream.concat(
          Stream.concat(
              this.elements.stream().takeWhile(entry -> {
                final K key = entry.getKey();
                return key.hashCode() != this.hashCode || !key.equals(this.key);
              }),
              Stream.of(Map.entry(this.key, this.value))
          ),
          this.elements.stream().dropWhile(entry -> {
            final K key = entry.getKey();
            return key.hashCode() != this.hashCode || !key.equals(this.key);
          }).skip(1L)
      );
    }
  }

  static final class RemovingNode<K, V> extends AbstractNode<K, V> {

    private static final long serialVersionUID = 1163848859587457585L;

    private final LinkedHashMap<K, V> elements;
    private final int hashCode;
    private final K key;

    RemovingNode(final LinkedHashMap<K, V> elements, final K key) {
      this.elements = elements;
      this.hashCode = key.hashCode();
      this.key = key;
    }

    @Override
    public int size() {
      return this.elements.size() - 1;
    }

    @Override
    public Optional<V> get(final K key) {
      if (key.hashCode() == this.hashCode && key.equals(this.key)) { return Optional.empty(); }
      return this.elements.get(key);
    }

    @Override
    public Stream<Map.Entry<K, V>> stream() {
      return Stream.concat(
          this.elements.stream().takeWhile(entry -> {
            final K key = entry.getKey();
            return key.hashCode() != this.hashCode || !key.equals(this.key);
          }),
          this.elements.stream().dropWhile(entry -> {
            final K key = entry.getKey();
            return key.hashCode() != this.hashCode || !key.equals(this.key);
          }).skip(1L)
      );
    }
  }
}
