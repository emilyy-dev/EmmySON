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
import io.github.emilyydev.emmyson.simple.util.LinkedHashMap;
import io.github.emilyydev.emmyson.simple.util.LinkedList;
import io.github.emilyydev.emmyson.util.Try;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

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
  public <T extends JsonData> Try<T> read(final String json, final DataType<T> type) {
    return read(new StringReader(json), type);
  }

  @Override
  public <T extends JsonData> Try<T> read(final File file, final DataType<T> type) {
    try (final var reader = new FileReader(file, StandardCharsets.UTF_8)) {
      return read(reader, type);
    } catch (final IOException exception) {
      return Try.failure(exception);
    }
  }

  @Override
  public <T extends JsonData> Try<T> read(final Path in, final DataType<T> type) {
    try (final var reader = Files.newBufferedReader(in)) {
      return read(reader, type);
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
    try (final var reader = createReader(in)) {
      return reader.read().as(type);
    } catch (final IOException exception) {
      return Try.failure(exception);
    }
  }

  @Override
  public JsonReader createReader(final String json) {
    return createReader(new StringReader(json));
  }

  @Override
  public JsonReader createReader(final File file) throws IOException {
    return createReader(new FileReader(file, StandardCharsets.UTF_8));
  }

  @Override
  public JsonReader createReader(final Path in) throws IOException {
    return createReader(Files.newBufferedReader(in));
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
  public Optional<IOException> write(final File file, final JsonData data) {
    try (final var writer = new FileWriter(file, StandardCharsets.UTF_8)) {
      return write(writer, data);
    } catch (final IOException exception) {
      return Optional.of(exception);
    }
  }

  @Override
  public Optional<IOException> write(final Path path, final JsonData data) {
    try (final var writer = Files.newBufferedWriter(path)) {
      return write(writer, data);
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
    try (final var writer = createWriter(out)) {
      writer.write(data);
      return Optional.empty();
    } catch (final IOException exception) {
      return Optional.of(exception);
    }
  }

  @Override
  public JsonWriter createWriter(final File file) throws IOException {
    return createWriter(new FileWriter(file, StandardCharsets.UTF_8));
  }

  @Override
  public JsonWriter createWriter(final Path path) throws IOException {
    return createWriter(Files.newBufferedWriter(path));
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
    return JsonBooleanImpl.get(b);
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
    return requireNonNull(str, "str") instanceof JsonString ? (JsonString) str : JsonStringImpl.emptyOrCreate(str);
  }

  @Override
  public JsonArray arrayOf() {
    return JsonArrayImpl.empty();
  }

  @Override
  public JsonArray arrayOf(final @Nullable JsonData element) {
    return JsonArrayImpl.emptyOrCreate(LinkedList.single(nullSafe(element)));
  }

  @Override
  public JsonArray arrayOf(final @Nullable JsonData @NotNull ... elements) {
    if (0 == elements.length) {
      return arrayOf();
    } else if (1 == elements.length) {
      return arrayOf(elements[0]);
    } else {
      return arrayOf(Arrays.stream(elements));
    }
  }

  @Override
  public JsonArray arrayOf(final Collection<? extends @Nullable JsonData> elements) {
    if (elements instanceof JsonArrayImpl.JavaList) {
      return ((JsonArrayImpl.JavaList) elements).owningArray;
    } else if (elements.isEmpty()) {
      return arrayOf();
    } else if (1 == elements.size()) {
      return arrayOf(elements.iterator().next());
    } else {
      return arrayOf(elements.stream());
    }
  }

  @Override
  public JsonArray arrayOf(final Stream<? extends @Nullable JsonData> elements) {
    return elements.map(StandardDataFactory::nullSafe)
        .collect(collectingAndThen(
            collectingAndThen(
                toList(), LinkedList::ofAll
            ), JsonArrayImpl::emptyOrCreate
        ));
  }

  @Override
  public JsonObject objectOf() {
    return JsonObjectImpl.empty();
  }

  @Override
  public JsonObject objectOf(final CharSequence key, final @Nullable JsonData value) {
    return JsonObjectImpl.emptyOrCreate(LinkedHashMap.single(string(key), nullSafe(value)));
  }

  @Override
  public JsonObject objectOf(final Map.Entry<? extends CharSequence, ? extends @Nullable JsonData> entry) {
    return objectOf(entry.getKey(), entry.getValue());
  }

  @Override
  @SafeVarargs
  public final JsonObject objectOf(
      final Map.Entry<? extends CharSequence, ? extends @Nullable JsonData> @NotNull ... entries
  ) {
    if (0 == entries.length) {
      return objectOf();
    } else if (1 == entries.length) {
      return objectOf(entries[0]);
    } else {
      return objectOf(Arrays.stream(entries));
    }
  }

  @Override
  public JsonObject objectOf(
      final Collection<? extends Map.Entry<? extends CharSequence, ? extends @Nullable JsonData>> entries
  ) {
    if (entries.isEmpty()) {
      return objectOf();
    } else if (1 == entries.size()) {
      return objectOf(entries.iterator().next());
    } else {
      return objectOf(entries.stream());
    }
  }

  @Override
  public JsonObject objectOf(
      final Stream<? extends Map.Entry<? extends CharSequence, ? extends @Nullable JsonData>> entries
  ) {
    return entries.map(entry -> Map.entry(string(entry.getKey()), nullSafe(entry.getValue())))
        .collect(collectingAndThen(
            collectingAndThen(
                toList(),
                LinkedHashMap::ofAll
            ), JsonObjectImpl::emptyOrCreate
        ));
  }

  @Override
  public JsonObject objectOf(final Map<? extends CharSequence, ? extends @Nullable JsonData> map) {
    if (map.isEmpty()) {
      return objectOf();
    } else if (1 == map.size()) {
      return objectOf(map.entrySet().iterator().next());
    } else {
      return objectOf(map.entrySet().stream());
    }
  }
}
