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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public interface LinkedList<T> extends Iterable<T>, Serializable {

  @SuppressWarnings("unchecked")
  static <T> LinkedList<T> empty() {
    return (LinkedList<T>) LinkedList$.EmptyNode.INSTANCE;
  }

  static <T> LinkedList<T> single(final T element) {
    return new LinkedList$.SingleNode<>(element);
  }

  static <T> LinkedList<T> ofAll(final Collection<? extends T> elements) {
    if (elements.isEmpty()) {
      return empty();
    } else if (elements.size() == 1) {
      return single(elements.iterator().next());
    } else {
      return new LinkedList$.SingleNode<>(elements);
    }
  }

  @SafeVarargs
  static <T> LinkedList<T> ofAll(final T... elements) {
    if (elements.length == 0) {
      return empty();
    } else if (elements.length == 1) {
      return single(elements[0]);
    } else {
      return new LinkedList$.SingleNode<>(elements);
    }
  }

  boolean isEmpty();
  int size();

  T get(int index);

  LinkedList<T> with(int index, T element);
  LinkedList<T> remove(int index);

  LinkedList<T> append(T element);
  LinkedList<T> appendAll(Collection<? extends T> elements);
  LinkedList<T> appendAll(LinkedList<? extends T> elements);

  LinkedList<T> prepend(T element);
  LinkedList<T> prependAll(Collection<? extends T> elements);
  LinkedList<T> prependAll(LinkedList<? extends T> elements);

  LinkedList<T> insert(int index, T element);

  Stream<T> stream();

  @Override
  default @NotNull Iterator<T> iterator() {
    return stream().iterator();
  }

  @Override
  default Spliterator<T> spliterator() {
    return Spliterators.spliterator(
        iterator(), size(),
        Spliterator.ORDERED
        | Spliterator.SIZED
        | Spliterator.NONNULL
        | Spliterator.IMMUTABLE
        | Spliterator.SUBSIZED
    );
  }

  default String asString() {
    return getClass().getSimpleName() + stream().map(Object::toString).collect(joining(",", "(", ")"));
  }
}

final class LinkedList$ {

  private LinkedList$() {
    throw new UnsupportedOperationException("you stink");
  }

  static final class EmptyNode<T> implements LinkedList<T> {

    static final LinkedList<?> INSTANCE = new EmptyNode<>();

    private static final long serialVersionUID = -8838500933544319294L;

    private static <T> T throwOutOfBounds(final int index) {
      throw new IndexOutOfBoundsException(index);
    }

    @Override
    public boolean isEmpty() {
      return true;
    }

    @Override
    public int size() {
      return 0;
    }

    @Override
    public T get(final int index) {
      return throwOutOfBounds(index);
    }

    @Override
    public LinkedList<T> with(final int index, final T element) {
      return throwOutOfBounds(index);
    }

    @Override
    public LinkedList<T> remove(final int index) {
      return throwOutOfBounds(index);
    }

    @Override
    public LinkedList<T> append(final T element) {
      return LinkedList.single(element);
    }

    @Override
    public LinkedList<T> appendAll(final Collection<? extends T> elements) {
      return LinkedList.ofAll(elements);
    }

    @Override
    @SuppressWarnings("unchecked")
    public LinkedList<T> appendAll(final LinkedList<? extends T> elements) {
      return (LinkedList<T>) elements;
    }

    @Override
    public LinkedList<T> prepend(final T element) {
      return LinkedList.single(element);
    }

    @Override
    public LinkedList<T> prependAll(final Collection<? extends T> elements) {
      return LinkedList.ofAll(elements);
    }

    @Override
    @SuppressWarnings("unchecked")
    public LinkedList<T> prependAll(final LinkedList<? extends T> elements) {
      return (LinkedList<T>) elements;
    }

    @Override
    public LinkedList<T> insert(final int index, final T element) {
      if (index != 0) { return throwOutOfBounds(index); }
      return LinkedList.single(element);
    }

    @Override
    public Stream<T> stream() {
      return Stream.empty();
    }

    @Override
    public @NotNull Iterator<T> iterator() {
      return Collections.emptyIterator();
    }

    @Override
    public Spliterator<T> spliterator() {
      return Spliterators.emptySpliterator();
    }

    @Override
    public void forEach(final Consumer<? super T> action) { }

    @Override
    public boolean equals(final Object obj) {
      return obj instanceof EmptyNode<?>;
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

  static abstract class AbstractNode<T> implements LinkedList<T> {

    private static boolean contentEquals(final AbstractNode<?> first, final AbstractNode<?> second) {
      final Iterator<?> it1 = first.iterator();
      final Iterator<?> it2 = second.iterator();
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
    public final LinkedList<T> with(final int index, final T element) {
      checkBounds(index);
      return new ReplacingNode<>(this, index, element);
    }

    @Override
    public final LinkedList<T> remove(final int index) {
      checkBounds(index);
      if (size() == 1) { return LinkedList.empty(); }
      return new RemovingNode<>(this, index);
    }

    @Override
    public final LinkedList<T> append(final T element) {
      return new LinkedNode<>(this, LinkedList.single(element));
    }

    @Override
    public final LinkedList<T> appendAll(final Collection<? extends T> elements) {
      if (elements.isEmpty()) { return this; }
      return new LinkedNode<>(this, LinkedList.ofAll(elements));
    }

    @Override
    public final LinkedList<T> appendAll(final LinkedList<? extends T> elements) {
      if (elements.isEmpty()) { return this; }
      return new LinkedNode<>(this, elements);
    }

    @Override
    public final LinkedList<T> prepend(final T element) {
      return new LinkedNode<>(LinkedList.single(element), this);
    }

    @Override
    public final LinkedList<T> prependAll(final Collection<? extends T> elements) {
      if (elements.isEmpty()) { return this; }
      return new LinkedNode<>(LinkedList.ofAll(elements), this);
    }

    @Override
    public final LinkedList<T> prependAll(final LinkedList<? extends T> elements) {
      if (elements.isEmpty()) { return this; }
      return new LinkedNode<>(elements, this);
    }

    @Override
    public final LinkedList<T> insert(final int index, final T element) {
      if (index == 0) { return prepend(element); }
      if (index == size()) { return append(element); }
      checkBounds(index);
      return new InsertingNode<>(this, index, element);
    }

    @Override
    public final void forEach(final Consumer<? super T> action) {
      iterator().forEachRemaining(action);
    }

    @Override
    public final boolean equals(final Object other) {
      return other instanceof AbstractNode<?>
             && size() == ((AbstractNode<?>) other).size()
             && contentEquals(this, (AbstractNode<?>) other);
    }

    @Override
    public final int hashCode() {
      return stream().mapToInt(Object::hashCode).reduce(1, (result, hashCode) -> result * 31 + hashCode);
    }

    @Override
    public final String toString() {
      return asString();
    }

    protected final void checkBounds(final int index) {
      if (index < 0 || index >= size()) { throw new IndexOutOfBoundsException(index); }
    }
  }

  // Not "single-element node", but rather not linked
  static final class SingleNode<T> extends AbstractNode<T> {

    private static final long serialVersionUID = 2796533320758197572L;

    private final List<T> elements;

    SingleNode(final T element) {
      this.elements = List.of(element);
    }

    SingleNode(final T[] elements) {
      this.elements = List.of(elements);
    }

    SingleNode(final Collection<? extends T> elements) {
      this.elements = elements.stream()
          .map(Objects::requireNonNull)
          .collect(collectingAndThen(toList(), Collections::unmodifiableList));
    }

    @Override
    public int size() {
      return this.elements.size();
    }

    @Override
    public T get(final int index) {
      checkBounds(index);
      return this.elements.get(index);
    }

    @Override
    public Stream<T> stream() {
      return this.elements.stream();
    }

    @Override
    public @NotNull Iterator<T> iterator() {
      return this.elements.iterator();
    }
  }

  static final class LinkedNode<T> extends AbstractNode<T> {

    private static final long serialVersionUID = -7140050832119318192L;

    private final LinkedList<? extends T> pre;
    private final LinkedList<? extends T> post;

    LinkedNode(final LinkedList<? extends T> pre, final LinkedList<? extends T> post) {
      this.pre = pre;
      this.post = post;
    }

    @Override
    public int size() {
      return this.pre.size() + this.post.size();
    }

    @Override
    public T get(final int index) {
      checkBounds(index);
      if (index < this.pre.size()) { return this.pre.get(index); }
      return this.post.get(index - this.pre.size());
    }

    @Override
    public Stream<T> stream() {
      return Stream.concat(this.pre.stream(), this.post.stream());
    }
  }

  static final class InsertingNode<T> extends AbstractNode<T> {

    private static final long serialVersionUID = 1142681987017348905L;

    private final LinkedList<? extends T> elements;
    private final int index;
    private final T element;

    InsertingNode(final LinkedList<? extends T> elements, final int index, final T element) {
      this.elements = elements;
      this.index = index;
      this.element = requireNonNull(element, "element");
    }

    @Override
    public int size() {
      return this.elements.size() + 1;
    }

    @Override
    public T get(final int index) {
      checkBounds(index);
      if (index == this.index) { return this.element; }
      if (index < this.index) { return this.elements.get(index); }
      return this.elements.get(index - 1);
    }

    @Override
    public Stream<T> stream() {
      return Stream.concat(
          Stream.concat(
              this.elements.stream().limit(this.index),
              Stream.of(this.element)
          ),
          this.elements.stream().skip(this.index)
      );
    }
  }

  static final class ReplacingNode<T> extends AbstractNode<T> {

    private static final long serialVersionUID = -914113675395169143L;

    private final LinkedList<? extends T> elements;
    private final int index;
    private final T element;

    ReplacingNode(final LinkedList<? extends T> elements, final int index, final T element) {
      this.elements = elements;
      this.index = index;
      this.element = requireNonNull(element, "element");
    }

    @Override
    public int size() {
      return this.elements.size();
    }

    @Override
    public T get(final int index) {
      checkBounds(index);
      if (index == this.index) { return this.element; }
      return this.elements.get(index);
    }

    @Override
    public Stream<T> stream() {
      return Stream.concat(
          Stream.concat(
              this.elements.stream().limit(this.index),
              Stream.of(this.element)
          ),
          this.elements.stream().skip(this.index + 1L)
      );
    }
  }

  static final class RemovingNode<T> extends AbstractNode<T> {

    private static final long serialVersionUID = -4044406630037453074L;

    private final LinkedList<? extends T> elements;
    private final int index;

    RemovingNode(final LinkedList<? extends T> elements, final int index) {
      this.elements = elements;
      this.index = index;
    }

    @Override
    public int size() {
      return this.elements.size() - 1;
    }

    @Override
    public T get(final int index) {
      checkBounds(index);
      if (index < this.index) { return this.elements.get(index); }
      return this.elements.get(index + 1);
    }

    @Override
    public Stream<T> stream() {
      return Stream.concat(
          this.elements.stream().limit(this.index),
          this.elements.stream().skip(this.index + 1L)
      );
    }
  }
}
