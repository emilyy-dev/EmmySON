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
import io.github.emilyydev.emmyson.simple.util.LinkedHashMap;
import net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

public class JsonObjectImpl implements JsonObject {

  private static final long serialVersionUID = 2756192412730463825L;

  public static JsonObject empty() {
    return Empty.INSTANCE;
  }

  public static JsonObject emptyOrCreate(final LinkedHashMap<JsonString, JsonData> elements) {
    return elements.isEmpty() ? Empty.INSTANCE : new JsonObjectImpl(elements);
  }

  private final LinkedHashMap<JsonString, JsonData> elements;
  private transient volatile JavaMap javaMap = null;

  private JsonObjectImpl(final LinkedHashMap<JsonString, JsonData> elements) {
    this.elements = elements;
  }

  @Override
  public final int size() {
    return this.elements.size();
  }

  @Override
  public final boolean isEmpty() {
    return this.elements.isEmpty();
  }

  @Override
  public final Optional<JsonData> get(final JsonString name) {
    return this.elements.get(requireNonNull(name, "name"));
  }

  @Override
  public final JsonObject remove(final JsonString name) {
    return emptyOrCreate(this.elements.remove(name));
  }

  @Override
  public final JsonObject withMapping(final JsonString name, final JsonData data) {
    return emptyOrCreate(this.elements.withMapping(name, data));
  }

  @Override
  public final JsonData getOrJsonNull(final JsonString name) {
    return getOrElse(name, JsonNullImpl.INSTANCE);
  }

  @Override
  public final Map<JsonString, ? extends JsonData> asMap() {
    if (this.javaMap == null) {
      synchronized (this) {
        if (this.javaMap == null) {
          this.javaMap = new JavaMap();
        }
      }
    }

    return this.javaMap;
  }

  @Override
  public final boolean equals(final Object other) {
    if (this == other) { return true; }
    if (!(other instanceof JsonObject)) { return false; }
    final JsonObject that = (JsonObject) other;
    return that instanceof JsonObjectImpl && this.elements.equals(((JsonObjectImpl) that).elements)
           || size() == that.size() && asMap().equals(that.asMap());
  }

  @Override
  public final int hashCode() {
    return this.elements.hashCode();
  }

  @Override
  public final String toString() {
    return examine(StringExaminer.simpleEscaping());
  }

  public final class JavaMap implements Map<JsonString, JsonData> {

    public final JsonObjectImpl owningObject = JsonObjectImpl.this;
    private final Map<JsonString, JsonData> delegate = JsonObjectImpl.this.elements.stream()
        .collect(collectingAndThen(
            toMap(Entry::getKey, Entry::getValue, (lhs, rhs) -> lhs, java.util.LinkedHashMap::new),
            Collections::unmodifiableMap
        ));
    private transient volatile EntrySet entrySet = null;

    @Override public boolean equals(final Object o) { return o == this || o instanceof Map && this.delegate.equals(o); }
    @Override public int hashCode() { return this.delegate.hashCode(); }
    @Override public String toString() { return "JavaMap(" + JsonObjectImpl.this + ')'; }

    @Override
    public @NotNull Set<Entry<JsonString, JsonData>> entrySet() {
      if (this.entrySet == null) {
        synchronized (this) {
          if (this.entrySet == null) {
            this.entrySet = new EntrySet();
          }
        }
      }

      return this.entrySet;
    }

    @Override public int size() { return this.delegate.size(); }
    @Override public boolean isEmpty() { return this.delegate.isEmpty(); }
    @Override public boolean containsKey(final Object key) { return this.delegate.containsKey(key); }
    @Override public boolean containsValue(final Object value) { return this.delegate.containsValue(value); }
    @Override public JsonData get(final Object key) { return this.delegate.get(key); }
    @Override public @Nullable JsonData put(final JsonString key, final JsonData value) { return this.delegate.put(key, value); }
    @Override public JsonData remove(final Object key) { return this.delegate.remove(key); }
    @Override public void putAll(final @NotNull Map<? extends JsonString, ? extends JsonData> m) { this.delegate.putAll(m); }
    @Override public void clear() { this.delegate.clear(); }
    @Override public @NotNull Set<JsonString> keySet() { return this.delegate.keySet(); }
    @Override public @NotNull Collection<JsonData> values() { return this.delegate.values(); }
    @Override @SuppressWarnings("SuspiciousMethodCalls") public JsonData getOrDefault(final Object key, final JsonData defaultValue) { return this.delegate.getOrDefault(key, defaultValue); }
    @Override public void forEach(final BiConsumer<? super JsonString, ? super JsonData> action) { this.delegate.forEach(action); }
    @Override public void replaceAll(final BiFunction<? super JsonString, ? super JsonData, ? extends JsonData> function) { this.delegate.replaceAll(function); }
    @Override public @Nullable JsonData putIfAbsent(final JsonString key, final JsonData value) { return this.delegate.putIfAbsent(key, value); }
    @Override public boolean remove(final Object key, final Object value) { return this.delegate.remove(key, value); }
    @Override public boolean replace(final JsonString key, final JsonData oldValue, final JsonData newValue) { return this.delegate.replace(key, oldValue, newValue); }
    @Override public @Nullable JsonData replace(final JsonString key, final JsonData value) { return this.delegate.replace(key, value); }
    @Override public JsonData computeIfAbsent(final JsonString key, final @NotNull Function<? super JsonString, ? extends JsonData> mappingFunction) { return this.delegate.computeIfAbsent(key, mappingFunction); }
    @Override public JsonData computeIfPresent(final JsonString key, final @NotNull BiFunction<? super JsonString, ? super JsonData, ? extends JsonData> remappingFunction) { return this.delegate.computeIfPresent(key, remappingFunction); }
    @Override public JsonData compute(final JsonString key, final @NotNull BiFunction<? super JsonString, ? super JsonData, ? extends JsonData> remappingFunction) { return this.delegate.compute(key, remappingFunction); }
    @Override public JsonData merge(final JsonString key, final @NotNull JsonData value, final @NotNull BiFunction<? super JsonData, ? super JsonData, ? extends JsonData> remappingFunction) { return this.delegate.merge(key, value, remappingFunction); }

    public final class EntrySet implements Set<Map.Entry<JsonString, JsonData>> {

      public final JavaMap owningMap = JavaMap.this;
      private final Set<Map.Entry<JsonString, JsonData>> delegate = JavaMap.this.delegate.entrySet();

      @Override public boolean equals(final Object o) { return o == this || o instanceof Set && this.delegate.equals(o); }
      @Override public int hashCode() { return this.delegate.hashCode(); }
      @Override public String toString() { return "EntrySet(" + JavaMap.this + ')'; }

      @Override public int size() { return this.delegate.size(); }
      @Override public boolean isEmpty() { return this.delegate.isEmpty(); }
      @Override public boolean contains(final Object o) { return this.delegate.contains(o); }
      @Override public @NotNull Iterator<Map.Entry<JsonString, JsonData>> iterator() { return this.delegate.iterator(); }
      @Override public void forEach(final Consumer<? super Map.Entry<JsonString, JsonData>> action) { this.delegate.forEach(action); }
      @Override public Object @NotNull [] toArray() { return this.delegate.toArray(); }
      @Override @SuppressWarnings("SuspiciousToArrayCall") public <T> T @NotNull [] toArray(final T @NotNull [] a) { return this.delegate.toArray(a); }
      @Override public boolean add(final Map.Entry<JsonString, JsonData> e) { return this.delegate.add(e); }
      @Override public boolean remove(final Object o) { return this.delegate.remove(o); }
      @Override public boolean containsAll(final @NotNull Collection<?> c) { return this.delegate.containsAll(c); }
      @Override public boolean addAll(final @NotNull Collection<? extends Map.Entry<JsonString, JsonData>> c) { return this.delegate.addAll(c); }
      @Override public boolean retainAll(final @NotNull Collection<?> c) { return this.delegate.retainAll(c); }
      @Override public boolean removeAll(final @NotNull Collection<?> c) { return this.delegate.removeAll(c); }
      @Override public boolean removeIf(final Predicate<? super Map.Entry<JsonString, JsonData>> filter) { return this.delegate.removeIf(filter); }
      @Override public void clear() { this.delegate.clear(); }
      @Override public Spliterator<Map.Entry<JsonString, JsonData>> spliterator() { return this.delegate.spliterator(); }
      @Override public Stream<Map.Entry<JsonString, JsonData>> stream() { return this.delegate.stream(); }
      @Override public Stream<Map.Entry<JsonString, JsonData>> parallelStream() { return this.delegate.parallelStream(); }
    }
  }

  private static final class Empty extends JsonObjectImpl {

    private static final long serialVersionUID = 249269091970949547L;

    private static final JsonObject INSTANCE = new Empty();

    private Empty() {
      super(LinkedHashMap.empty());
    }

    private Object readResolve() {
      return INSTANCE;
    }
  }
}
