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

import io.github.emilyydev.emmyson.data.DataFactory;
import io.github.emilyydev.emmyson.data.DataType;
import io.github.emilyydev.emmyson.data.JsonArray;
import io.github.emilyydev.emmyson.data.JsonBoolean;
import io.github.emilyydev.emmyson.data.JsonData;
import io.github.emilyydev.emmyson.data.JsonNull;
import io.github.emilyydev.emmyson.data.JsonNumber;
import io.github.emilyydev.emmyson.data.JsonObject;
import io.github.emilyydev.emmyson.data.JsonString;
import io.github.emilyydev.emmyson.io.JsonReader;
import io.github.emilyydev.emmyson.io.JsonWriter;
import io.github.emilyydev.emmyson.simple.io.StandardJsonReader;
import io.github.emilyydev.emmyson.simple.io.StandardJsonWriter;
import io.github.emilyydev.emmyson.util.Try;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;

public final class StandardDataFactory implements DataFactory {

  private static final DataFactory INSTANCE = new StandardDataFactory();

  public static DataFactory provider() {
    return INSTANCE;
  }

  @Contract("_ -> !null")
  private static JsonData nullSafe(final @Nullable JsonData e) {
    return null == e ? JsonNullImpl.INSTANCE : e;
  }

  @Override
  public <T extends JsonData> Try<T> read(final String in, final DataType<T> type) {
    return read(new StringReader(in), type);
  }

  @Override
  public <T extends JsonData> Try<T> read(final File in, final DataType<T> type) {
    try {
      return read(new InputStreamReader(new FileInputStream(in), StandardCharsets.UTF_8), type);
    } catch (final FileNotFoundException exception) {
      return Try.failure(exception);
    }
  }

  @Override
  public <T extends JsonData> Try<T> read(final Path in, final DataType<T> type) {
    try {
      return read(Files.newBufferedReader(in, StandardCharsets.UTF_8), type);
    } catch (final IOException exception) {
      return Try.failure(exception);
    }
  }

  @Override
  public <T extends JsonData> Try<T> read(final InputStream in, final DataType<T> type) {
    return read(new InputStreamReader(in, StandardCharsets.UTF_8), type);
  }

  @Override
  public <T extends JsonData> Try<T> read(final Readable in, final DataType<T> type) {
    try (final JsonReader reader = createReader(in)) {
      return reader.read().as(type);
    } catch (final IOException exception) {
      return Try.failure(exception);
    }
  }

  @Override
  public JsonReader createReader(final String in) {
    return createReader(new StringReader(in));
  }

  @Override
  public JsonReader createReader(final File in) throws IOException {
    return createReader(new InputStreamReader(new FileInputStream(in), StandardCharsets.UTF_8));
  }

  @Override
  public JsonReader createReader(final Path in) throws IOException {
    return createReader(Files.newBufferedReader(in, StandardCharsets.UTF_8));
  }

  @Override
  public JsonReader createReader(final InputStream in) {
    return createReader(new InputStreamReader(in, StandardCharsets.UTF_8));
  }

  @Override
  public JsonReader createReader(final Readable in) {
    return new StandardJsonReader(in, this);
  }

  @Override
  public Optional<IOException> write(final File out, final JsonData data) {
    try {
      return write(new OutputStreamWriter(new FileOutputStream(out), StandardCharsets.UTF_8), data);
    } catch (final FileNotFoundException exception) {
      return Optional.of(exception);
    }
  }

  @Override
  public Optional<IOException> write(final Path out, final JsonData data) {
    try {
      return write(Files.newBufferedWriter(out, StandardCharsets.UTF_8), data);
    } catch (final IOException exception) {
      return Optional.of(exception);
    }
  }

  @Override
  public Optional<IOException> write(final OutputStream out, final JsonData data) {
    return write(new OutputStreamWriter(out, StandardCharsets.UTF_8), data);
  }

  @Override
  public Optional<IOException> write(final Appendable out, final JsonData data) {
    try (final JsonWriter writer = createWriter(out)) {
      writer.write(data);
      return Optional.empty();
    } catch (final IOException exception) {
      return Optional.of(exception);
    }
  }

  @Override
  public JsonWriter createWriter(final File out) throws IOException {
    return createWriter(new OutputStreamWriter(new FileOutputStream(out), StandardCharsets.UTF_8));
  }

  @Override
  public JsonWriter createWriter(final Path out) throws IOException {
    return createWriter(Files.newBufferedWriter(out, StandardCharsets.UTF_8));
  }

  @Override
  public JsonWriter createWriter(final OutputStream out) {
    return createWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
  }

  @Override
  public JsonWriter createWriter(final Appendable out) {
    return new StandardJsonWriter(out);
  }

  @Override
  public JsonNull nil() {
    return JsonNullImpl.INSTANCE;
  }

  @Override
  public JsonBoolean bool(final boolean b) {
    return b ? JsonBooleanImpl.TRUE : JsonBooleanImpl.FALSE;
  }

  @Override
  public JsonNumber number(final byte b) {
    return new JsonNumberImpl(b);
  }

  @Override
  public JsonNumber number(final short s) {
    return new JsonNumberImpl(s);
  }

  @Override
  public JsonNumber number(final int i) {
    return new JsonNumberImpl(i);
  }

  @Override
  public JsonNumber number(final long l) {
    return new JsonNumberImpl(l);
  }

  @Override
  public JsonNumber number(final float f) {
    return new JsonNumberImpl(f);
  }

  @Override
  public JsonNumber number(final double d) {
    return new JsonNumberImpl(d);
  }

  @Override
  public JsonNumber number(final Number number) {
    return new JsonNumberImpl(requireNonNull(number, "number"));
  }

  @Override
  public JsonString string(final CharSequence str) {
    if (str instanceof JsonString) {
      return (JsonString) str;
    }

    return 0 == str.length() ? JsonStringImpl.EMPTY : new JsonStringImpl(str.toString());
  }

  @Override
  public JsonArray arrayOf() {
    return JsonArrayImpl.EMPTY;
  }

  @Override
  public JsonArray arrayOf(final @Nullable JsonData element) {
    return new JsonArrayImpl(List.of(nullSafe(element)));
  }

  @Override
  public JsonArray arrayOf(final @Nullable JsonData @NotNull ... elements) {
    if (0 == elements.length) {
      return JsonArrayImpl.EMPTY;
    } else if (1 == elements.length) {
      return new JsonArrayImpl(List.of(nullSafe(elements[0])));
    }

    final ArrayList<JsonData> mutable = new ArrayList<>(elements.length);
    for (final JsonData element : elements) {
      mutable.add(nullSafe(element));
    }
    return new JsonArrayImpl(unmodifiableList(mutable));
  }

  @Override
  @SuppressWarnings("unchecked")
  public JsonArray arrayOf(final Iterable<? extends @Nullable JsonData> elements) {
    if (elements instanceof JsonArray) {
      return (JsonArray) elements;
    } else if (elements instanceof Collection) {
      final Collection<? extends JsonData> c = (Collection<? extends JsonData>) elements;
      if (0 == c.size()) {
        return JsonArrayImpl.EMPTY;
      } else if (1 == c.size()) {
        return new JsonArrayImpl(List.of(nullSafe(elements.iterator().next())));
      }

      final ArrayList<JsonData> mutable = new ArrayList<>(c);
      mutable.replaceAll(StandardDataFactory::nullSafe);
      return new JsonArrayImpl(unmodifiableList(mutable));
    }

    final ArrayList<JsonData> mutable = new ArrayList<>();
    elements.forEach(e -> mutable.add(nullSafe(e)));
    if (0 == mutable.size()) {
      return JsonArrayImpl.EMPTY;
    } else if (1 == mutable.size()) {
      return new JsonArrayImpl(List.of(mutable.get(0)));
    }

    return new JsonArrayImpl(unmodifiableList(mutable));
  }

  @Override
  public JsonObject objectOf() {
    return JsonObjectImpl.EMPTY;
  }

  @Override
  public JsonObject objectOf(final CharSequence key, final @Nullable JsonData value) {
    return new JsonObjectImpl(Map.of(string(key), nullSafe(value)));
  }

  @Override
  public JsonObject objectOf(final Map.Entry<? extends CharSequence, ? extends @Nullable JsonData> entry) {
    return new JsonObjectImpl(Map.of(string(entry.getKey()), nullSafe(entry.getValue())));
  }

  @Override
  @SafeVarargs
  public final JsonObject objectOf(final Map.Entry<? extends CharSequence, ? extends @Nullable JsonData> @NotNull ... entries) {
    if (0 == entries.length) {
      return JsonObjectImpl.EMPTY;
    } else if (1 == entries.length) {
      final Map.Entry<? extends CharSequence, ? extends JsonData> entry = entries[0];
      return new JsonObjectImpl(Map.of(string(entry.getKey()), nullSafe(entry.getValue())));
    }

    final LinkedHashMap<JsonString, JsonData> mutable = new LinkedHashMap<>(entries.length);
    for (final Map.Entry<? extends CharSequence, ? extends JsonData> element : entries) {
      mutable.put(string(element.getKey()), nullSafe(element.getValue()));
    }
    return new JsonObjectImpl(unmodifiableMap(mutable));
  }

  @Override
  public JsonObject objectOf(final Iterable<? extends Map.Entry<? extends CharSequence, ? extends @Nullable JsonData>> entries) {
    if (entries instanceof JsonObjectImpl.EntrySet) {
      return ((JsonObjectImpl.EntrySet) entries).owningMap;
    }

    final LinkedHashMap<JsonString, JsonData> mutable = entries instanceof Collection
        ? new LinkedHashMap<>(((Collection<?>) entries).size())
        : new LinkedHashMap<>();
    for (final Map.Entry<? extends CharSequence, ? extends JsonData> entry : entries) {
      mutable.put(string(entry.getKey()), nullSafe(entry.getValue()));
    }

    return mutable.isEmpty() ? JsonObjectImpl.EMPTY : new JsonObjectImpl(unmodifiableMap(mutable));
  }

  @Override
  public JsonObject objectOf(final Map<? extends CharSequence, ? extends @Nullable JsonData> map) {
    if (map instanceof JsonObject) {
      return (JsonObject) map;
    } else if (map.isEmpty()) {
      return JsonObjectImpl.EMPTY;
    }

    final LinkedHashMap<JsonString, JsonData> mutable = new LinkedHashMap<>(map.size());
    map.forEach((name, value) -> mutable.put(string(name), nullSafe(value)));
    return new JsonObjectImpl(unmodifiableMap(mutable));
  }
}
