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

package io.github.emilyydev.emmyson.simple.data;

import io.github.emilyydev.emmyson.data.JsonArray;
import io.github.emilyydev.emmyson.data.JsonData;
import io.github.emilyydev.emmyson.simple.util.LinkedList;
import net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class JsonArrayImpl implements JsonArray {

  private static final long serialVersionUID = 3110228433630368656L;

  public static JsonArray empty() {
    return Empty.INSTANCE;
  }

  public static JsonArray emptyOrCreate(final LinkedList<JsonData> elements) {
    return elements.isEmpty() ? Empty.INSTANCE : new JsonArrayImpl(elements);
  }

  private final LinkedList<JsonData> elements;
  private transient volatile JavaList javaList = null;

  private JsonArrayImpl(final LinkedList<JsonData> elements) {
    this.elements = elements;
  }

  @Override
  public final boolean isEmpty() {
    return this.elements.isEmpty();
  }

  @Override
  public final int size() {
    return this.elements.size();
  }

  @Override
  public final JsonData get(final int index) {
    return this.elements.get(index);
  }

  @Override
  public final JsonArray remove(final int index) {
    return emptyOrCreate(this.elements.remove(index));
  }

  @Override
  public final JsonArray insert(final int index, final JsonData element) {
    return new JsonArrayImpl(this.elements.insert(index, element));
  }

  @Override
  public final JsonArray append(final JsonData jsonData) {
    return new JsonArrayImpl(this.elements.append(jsonData));
  }

  @Override
  public final JsonArray appendAll(final Collection<? extends JsonData> elements) {
    if (elements instanceof JavaList) {
      return emptyOrCreate(this.elements.appendAll(((JavaList) elements).owningArray.elements));
    } else {
      return emptyOrCreate(this.elements.appendAll(elements));
    }
  }

  @Override
  public final JsonArray appendAll(final JsonArray elements) {
    if (elements instanceof JsonArrayImpl) {
      return emptyOrCreate(this.elements.appendAll(((JsonArrayImpl) elements).elements));
    } else {
      return emptyOrCreate(this.elements.appendAll(elements.asList()));
    }
  }

  @Override
  public final JsonArray prepend(final JsonData jsonData) {
    return new JsonArrayImpl(this.elements.prepend(jsonData));
  }

  @Override
  public final JsonArray prependAll(final Collection<? extends JsonData> elements) {
    if (elements instanceof JavaList) {
      return emptyOrCreate(this.elements.prependAll(((JavaList) elements).owningArray.elements));
    } else {
      return emptyOrCreate(this.elements.prependAll(elements));
    }
  }

  @Override
  public final JsonArray prependAll(final JsonArray elements) {
    if (elements instanceof JsonArrayImpl) {
      return emptyOrCreate(this.elements.prependAll(((JsonArrayImpl) elements).elements));
    } else {
      return emptyOrCreate(this.elements.prependAll(elements.asList()));
    }
  }

  @Override
  public final Stream<JsonData> stream() {
    return this.elements.stream();
  }

  @Override
  public final List<? extends JsonData> asList() {
    if (this.javaList == null) {
      synchronized (this) {
        if (this.javaList == null) {
          this.javaList = new JavaList();
        }
      }
    }

    return this.javaList;
  }

  @Override
  public final boolean equals(final Object other) {
    if (this == other) { return true; }
    if (!(other instanceof JsonArray)) { return false; }
    final JsonArray that = (JsonArray) other;
    return that instanceof JsonArrayImpl && this.elements.equals(((JsonArrayImpl) that).elements)
           || size() == that.size() && asList().equals(that.asList());
  }

  @Override
  public final int hashCode() {
    return this.elements.hashCode();
  }

  @Override
  public final String toString() {
    return examine(StringExaminer.simpleEscaping());
  }

  public final class JavaList implements List<JsonData> {

    public final JsonArrayImpl owningArray = JsonArrayImpl.this;
    private final List<JsonData> delegate = JsonArrayImpl.this.stream()
        .collect(collectingAndThen(toList(), Collections::unmodifiableList));

    @Override public boolean equals(final Object o) { return o == this || o instanceof List && this.delegate.equals(o); }
    @Override public int hashCode() { return JsonArrayImpl.this.elements.hashCode(); }
    @Override public String toString() { return "JavaList(" + JsonArrayImpl.this + ')'; }

    @Override public int size() { return this.delegate.size(); }
    @Override public boolean isEmpty() { return this.delegate.isEmpty(); }
    @Override public boolean contains(final Object o) { return this.delegate.contains(o); }
    @Override public @NotNull Iterator<JsonData> iterator() { return this.delegate.iterator(); }
    @Override public void forEach(final Consumer<? super JsonData> action) { this.delegate.forEach(action); }
    @Override public Object @NotNull [] toArray() { return this.delegate.toArray(); }
    @Override @SuppressWarnings("SuspiciousToArrayCall") public <T> T @NotNull [] toArray(final T @NotNull [] a) { return this.delegate.toArray(a); }
    @Override public boolean add(final JsonData jsonData) { return this.delegate.add(jsonData); }
    @Override public boolean remove(final Object o) { return this.delegate.remove(o); }
    @Override public boolean containsAll(final @NotNull Collection<?> c) { return this.delegate.containsAll(c); }
    @Override public boolean addAll(final @NotNull Collection<? extends JsonData> c) { return this.delegate.addAll(c); }
    @Override public boolean addAll(final int index, final @NotNull Collection<? extends JsonData> c) { return this.delegate.addAll(index, c); }
    @Override public boolean removeAll(final @NotNull Collection<?> c) { return this.delegate.removeAll(c); }
    @Override public boolean removeIf(final Predicate<? super JsonData> filter) { return this.delegate.removeIf(filter); }
    @Override public boolean retainAll(final @NotNull Collection<?> c) { return this.delegate.retainAll(c); }
    @Override public void replaceAll(final UnaryOperator<JsonData> operator) { this.delegate.replaceAll(operator); }
    @Override public void sort(final Comparator<? super JsonData> c) { this.delegate.sort(c); }
    @Override public void clear() { this.delegate.clear(); }
    @Override public JsonData get(final int index) { return this.delegate.get(index); }
    @Override public JsonData set(final int index, final JsonData element) { return this.delegate.set(index, element); }
    @Override public void add(final int index, final JsonData element) { this.delegate.add(index, element); }
    @Override public JsonData remove(final int index) { return this.delegate.remove(index); }
    @Override public int indexOf(final Object o) { return this.delegate.indexOf(o); }
    @Override public int lastIndexOf(final Object o) { return this.delegate.lastIndexOf(o); }
    @Override public @NotNull ListIterator<JsonData> listIterator() { return this.delegate.listIterator(); }
    @Override public @NotNull ListIterator<JsonData> listIterator(final int index) { return this.delegate.listIterator(index); }
    @Override public @NotNull List<JsonData> subList(final int fromIndex, final int toIndex) { return this.delegate.subList(fromIndex, toIndex); }
    @Override public Spliterator<JsonData> spliterator() { return this.delegate.spliterator(); }
    @Override public Stream<JsonData> stream() { return this.delegate.stream(); }
    @Override public Stream<JsonData> parallelStream() { return this.delegate.parallelStream(); }
  }

  private static final class Empty extends JsonArrayImpl {

    private static final long serialVersionUID = -2150238115277480083L;

    private static final JsonArray INSTANCE = new Empty();

    private Empty() {
      super(LinkedList.empty());
    }

    private Object readResolve() {
      return INSTANCE;
    }
  }
}
