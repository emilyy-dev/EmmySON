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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;

public interface LinkedList<T> extends Iterable<T> {

  @SuppressWarnings("unchecked")
  static <T> LinkedList<T> empty() {
    return (LinkedList<T>) Empty.INSTANCE;
  }

  static <T> LinkedList<T> single(final T element) {
    return new Single<>(element);
  }

  @SuppressWarnings("unchecked")
  static <T> LinkedList<T> ofAll(final Iterable<? extends T> elements) {
    if (elements instanceof LinkedList) {
      return (LinkedList<T>) elements;
    } else if (elements instanceof Collection) {
      final Collection<? extends T> collection = (Collection<? extends T>) elements;
      if (collection.isEmpty()) {
        return empty();
      } else {
        return new Single<>(Collections.unmodifiableList(new ArrayList<>(collection)));
      }
    } else {
      final ArrayList<T> list = new ArrayList<>();
      elements.forEach(list::add);
      if (list.isEmpty()) {
        return empty();
      } else {
        return new Single<>(Collections.unmodifiableList(list));
      }
    }
  }

  @SafeVarargs
  static <T> LinkedList<T> ofAll(final T... elements) {
    if (elements.length == 0) {
      return empty();
    } else {
      return new Single<>(elements);
    }
  }

  default boolean isEmpty() {
    return false;
  }

  int size();

  LinkedList<T> append(final T element);

  LinkedList<T> appendAll(Iterable<? extends T> elements);

  LinkedList<T> prepend(final T element);

  LinkedList<T> prependAll(Iterable<? extends T> elements);

  Stream<T> stream();

  @Override
  default @NotNull Iterator<T> iterator() {
    return stream().iterator();
  }

  @Override
  default Spliterator<T> spliterator() {
    return Spliterators.spliterator(
        iterator(), size(), Spliterator.SIZED | Spliterator.IMMUTABLE | Spliterator.NONNULL | Spliterator.ORDERED
    );
  }
}

final class Empty<T> implements LinkedList<T> {

  static final LinkedList<?> INSTANCE = new Empty<>();

  @Override
  public boolean isEmpty() {
    return true;
  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public LinkedList<T> append(final T element) {
    return LinkedList.single(element);
  }

  @Override
  public LinkedList<T> appendAll(final Iterable<? extends T> elements) {
    return LinkedList.ofAll(elements);
  }

  @Override
  public LinkedList<T> prepend(final T element) {
    return LinkedList.single(element);
  }

  @Override
  public LinkedList<T> prependAll(final Iterable<? extends T> elements) {
    return LinkedList.ofAll(elements);
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
}

// Not "single node", but rather not linked
final class Single<T> implements LinkedList<T> {

  private final List<T> elements;

  Single(final T element) {
    this.elements = List.of(element);
  }

  Single(final T[] elements) {
    this.elements = List.of(elements);
  }

  Single(final List<T> elements) {
    this.elements = elements;
  }

  @Override
  public int size() {
    return this.elements.size();
  }

  @Override
  public LinkedList<T> append(final T element) {
    return new Linked<>(this, new Single<>(element));
  }

  @Override
  public LinkedList<T> appendAll(final Iterable<? extends T> elements) {
    return new Linked<>(this, LinkedList.ofAll(elements));
  }

  @Override
  public LinkedList<T> prepend(final T element) {
    return new Linked<>(new Single<>(element), this);
  }

  @Override
  public LinkedList<T> prependAll(final Iterable<? extends T> elements) {
    return new Linked<>(LinkedList.ofAll(elements), this);
  }

  @Override
  public Stream<T> stream() {
    return this.elements.stream();
  }

  @Override
  public @NotNull Iterator<T> iterator() {
    return this.elements.iterator();
  }

  @Override
  public Spliterator<T> spliterator() {
    return this.elements.spliterator();
  }
}

final class Linked<T> implements LinkedList<T> {

  private final LinkedList<? extends T> pre;
  private final LinkedList<? extends T> post;

  Linked(final LinkedList<? extends T> pre, final LinkedList<? extends T> post) {
    this.pre = pre;
    this.post = post;
  }

  @Override
  public int size() {
    return this.pre.size() + this.post.size();
  }

  @Override
  public LinkedList<T> append(final T element) {
    return new Linked<>(this, new Single<>(element));
  }

  @Override
  public LinkedList<T> appendAll(final Iterable<? extends T> elements) {
    return new Linked<>(this, LinkedList.ofAll(elements));
  }

  @Override
  public LinkedList<T> prepend(final T element) {
    return new Linked<>(new Single<>(element), this);
  }

  @Override
  public LinkedList<T> prependAll(final Iterable<? extends T> elements) {
    return new Linked<>(LinkedList.ofAll(elements), this);
  }

  @Override
  public Stream<T> stream() {
    return Stream.concat(this.pre.stream(), this.post.stream());
  }
}
