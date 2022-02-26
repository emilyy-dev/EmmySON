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

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
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

  private JsonArrayImpl(final LinkedList<JsonData> elements) {
    this.elements = elements;
  }

  @Override
  public int size() {
    return this.elements.size();
  }

  @Override
  public JsonData get(final int index) {
    return stream().skip(index).findFirst().orElseThrow(NoSuchElementException::new);
  }

  @Override
  public JsonArray append(final JsonData jsonData) {
    return new JsonArrayImpl(this.elements.append(jsonData));
  }

  @Override
  public JsonArray appendAll(final Iterable<? extends JsonData> elements) {
    return new JsonArrayImpl(this.elements.appendAll(elements));
  }

  @Override
  public JsonArray prepend(final JsonData jsonData) {
    return new JsonArrayImpl(this.elements.prepend(jsonData));
  }

  @Override
  public JsonArray prependAll(final Iterable<? extends JsonData> elements) {
    return new JsonArrayImpl(this.elements.prependAll(elements));
  }

  @Override
  public Stream<JsonData> stream() {
    return this.elements.stream();
  }

  @Override
  public boolean isEmpty() {
    return this.elements.isEmpty();
  }

  @Override
  public List<? extends JsonData> asJavaList() {
    return stream().collect(collectingAndThen(toList(), Collections::unmodifiableList));
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) { return true; }
    if (!(other instanceof JsonArray)) { return false; }
    final JsonArray that = (JsonArray) other;
    return size() == that.size() && asJavaList().equals(that.asJavaList());
  }

  @Override
  public int hashCode() {
    return this.elements.hashCode();
  }

  @Override
  public String toString() {
    return examine(StringExaminer.simpleEscaping());
  }

  private static final class Empty extends JsonArrayImpl {

    private static final long serialVersionUID = -2150238115277480083L;

    private static final JsonArray INSTANCE = new Empty();

    private Empty() {
      super(LinkedList.empty());
    }

    @Override
    public JsonArray append(final JsonData jsonData) {
      return emptyOrCreate(LinkedList.single(jsonData));
    }

    @Override
    public JsonArray appendAll(final Iterable<? extends JsonData> elements) {
      return emptyOrCreate(LinkedList.ofAll(elements));
    }

    @Override
    public JsonArray prepend(final JsonData jsonData) {
      return emptyOrCreate(LinkedList.single(jsonData));
    }

    @Override
    public JsonArray prependAll(final Iterable<? extends JsonData> elements) {
      return emptyOrCreate(LinkedList.ofAll(elements));
    }

    private Object readResolve() {
      return INSTANCE;
    }
  }
}
