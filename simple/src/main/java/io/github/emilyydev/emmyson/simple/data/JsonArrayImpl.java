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
import net.kyori.examination.string.StringExaminer;

import java.util.AbstractList;
import java.util.List;
import java.util.stream.Stream;

public final class JsonArrayImpl extends AbstractList<JsonData> implements JsonArray {

  private static final long serialVersionUID = 3110228433630368656L;

  public static final JsonArray EMPTY = new JsonArrayImpl(List.of());

  private final List<JsonData> elements;

  public JsonArrayImpl(final List<JsonData> elements) {
    this.elements = elements;
  }

  @Override
  public int size() {
    return this.elements.size();
  }

  @Override
  public JsonData get(final int index) {
    return this.elements.get(index);
  }

  @Override
  public Stream<JsonData> stream() {
    return this.elements.stream();
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) { return true; }
    if (!(other instanceof JsonArray)) { return false; }
    return size() == ((JsonArray) other).size() && super.equals(other);
  }

  @Override
  public int hashCode() {
    return this.elements.hashCode();
  }

  @Override
  public String toString() {
    return examine(StringExaminer.simpleEscaping());
  }
}
