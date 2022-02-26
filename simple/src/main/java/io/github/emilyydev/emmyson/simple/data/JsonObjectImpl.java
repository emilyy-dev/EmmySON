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

import io.github.emilyydev.emmyson.data.JsonData;
import io.github.emilyydev.emmyson.data.JsonObject;
import io.github.emilyydev.emmyson.data.JsonString;
import net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class JsonObjectImpl extends AbstractMap<JsonString, JsonData> implements JsonObject {

  private static final long serialVersionUID = 2756192412730463825L;

  public static JsonObject empty() {
    return Empty.INSTANCE;
  }

  public static JsonObject emptyOrCreate(final Map<JsonString, JsonData> elements) {
    return elements.isEmpty() ? Empty.INSTANCE : new JsonObjectImpl(elements);
  }

  private final Map<JsonString, JsonData> elements;
  private transient volatile EntrySet entrySet = null;

  private JsonObjectImpl(final Map<JsonString, JsonData> elements) {
    this.elements = elements;
  }

  @Override
  public int size() {
    return this.elements.size();
  }

  @Override
  public boolean isEmpty() {
    return this.elements.isEmpty();
  }

  @Override
  public boolean containsKey(final Object key) {
    return this.elements.containsKey(key);
  }

  @Override
  public boolean containsValue(final Object value) {
    return this.elements.containsValue(value);
  }

  @Override
  public JsonData get(final Object key) {
    return this.elements.get(key);
  }

  @Override
  public @NotNull Set<JsonString> keySet() {
    return this.elements.keySet();
  }

  @Override
  public @NotNull Collection<JsonData> values() {
    return this.elements.values();
  }

  @Override
  public @NotNull Set<Entry<JsonString, JsonData>> entrySet() {
    if (null == this.entrySet) {
      synchronized (this) {
        if (null == this.entrySet) {
          this.entrySet = new EntrySet();
        }
      }
    }

    return this.entrySet;
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) { return true; }
    if (!(other instanceof JsonObject)) { return false; }
    return size() == ((JsonObject) other).size() && this.elements.equals(other);
  }

  @Override
  public int hashCode() {
    return this.elements.hashCode();
  }

  @Override
  public String toString() {
    return examine(StringExaminer.simpleEscaping());
  }

  public final class EntrySet extends AbstractSet<Entry<JsonString, JsonData>> {

    public final JsonObject owningMap = JsonObjectImpl.this;
    private final Set<Entry<JsonString, JsonData>> entrySet = JsonObjectImpl.this.elements.entrySet();

    @Override
    public int size() {
      return this.entrySet.size();
    }

    @Override
    public boolean isEmpty() {
      return this.entrySet.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
      return this.entrySet.contains(o);
    }

    @Override
    public @NotNull Iterator<Entry<JsonString, JsonData>> iterator() {
      return this.entrySet.iterator();
    }

    @Override
    public Stream<Entry<JsonString, JsonData>> stream() {
      return this.entrySet.stream();
    }

    @Override
    public boolean containsAll(final @NotNull Collection<?> c) {
      return this.entrySet.containsAll(c);
    }
  }

  private static final class Empty extends JsonObjectImpl {

    private static final long serialVersionUID = 249269091970949547L;

    private static final JsonObject INSTANCE = new Empty();

    private Empty() {
      super(Map.of());
    }

    private Object readResolve() {
      return INSTANCE;
    }
  }
}
