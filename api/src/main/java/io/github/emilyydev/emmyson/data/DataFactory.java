//
// MIT License
//
// Copyright (c) 2022 emilyy-dev
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package io.github.emilyydev.emmyson.data;

import io.github.emilyydev.emmyson.io.JsonReader;
import io.github.emilyydev.emmyson.io.JsonWriter;
import io.github.emilyydev.emmyson.util.Try;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * The core of this API.
 */
public interface DataFactory {

  /**
   * Shortcut method for the ServiceLoader API, attempts to find a DataFactory provider for the class loader that
   * loaded this class.
   *
   * @return a data factory... maybe
   */
  static Optional<DataFactory> findDataFactory() {
    return ServiceLoader.load(DataFactory.class, DataFactory.class.getClassLoader()).findFirst();
  }

  /**
   *
   *
   * @param in   the json string to attempt to read
   * @param type the expected data type the serialized data will be attempted to be read as
   * @param <T>  the expected java class representing the deserialized data type
   * @return
   */
  <T extends JsonData> Try<T> read(String in, DataType<T> type);

  /**
   *
   * @param in
   * @param type
   * @param <T>
   * @return
   */
  <T extends JsonData> Try<T> read(File in, DataType<T> type);

  /**
   *
   * @param in
   * @param type
   * @param <T>
   * @return
   */
  <T extends JsonData> Try<T> read(Path in, DataType<T> type);

  /**
   *
   * @param in
   * @param type
   * @param <T>
   * @return
   */
  <T extends JsonData> Try<T> read(InputStream in, DataType<T> type);

  /**
   *
   * @param in
   * @param type
   * @param <T>
   * @return
   */
  <T extends JsonData> Try<T> read(Readable in, DataType<T> type);

  /**
   *
   * @param in
   * @return
   * @throws IOException
   */
  JsonReader createReader(String in) throws IOException;

  /**
   *
   * @param in
   * @return
   * @throws IOException
   */
  JsonReader createReader(File in) throws IOException;

  /**
   *
   * @param in
   * @return
   * @throws IOException
   */
  JsonReader createReader(Path in) throws IOException;

  /**
   *
   * @param in
   * @return
   * @throws IOException
   */
  JsonReader createReader(InputStream in) throws IOException;

  /**
   *
   * @param in
   * @return
   * @throws IOException
   */
  JsonReader createReader(Readable in) throws IOException;

  /**
   *
   * @param out
   * @param data
   * @return
   */
  Optional<IOException> write(File out, JsonData data);

  /**
   *
   * @param out
   * @param data
   * @return
   */
  Optional<IOException> write(Path out, JsonData data);

  /**
   *
   * @param out
   * @param data
   * @return
   */
  Optional<IOException> write(OutputStream out, JsonData data);

  /**
   *
   * @param out
   * @param data
   * @return
   */
  Optional<IOException> write(Appendable out, JsonData data);

  /**
   *
   * @param out
   * @return
   * @throws IOException
   */
  JsonWriter createWriter(File out) throws IOException;

  /**
   *
   * @param out
   * @return
   * @throws IOException
   */
  JsonWriter createWriter(Path out) throws IOException;

  /**
   *
   * @param out
   * @return
   * @throws IOException
   */
  JsonWriter createWriter(OutputStream out) throws IOException;

  /**
   *
   * @param out
   * @return
   * @throws IOException
   */
  JsonWriter createWriter(Appendable out) throws IOException;

  /**
   *
   * @return
   */
  JsonNull nil();

  /**
   *
   * @param b
   * @return
   */
  JsonBoolean bool(boolean b);

  /**
   *
   * @param b
   * @return
   */
  JsonNumber number(byte b);

  /**
   *
   * @param s
   * @return
   */
  JsonNumber number(short s);

  /**
   *
   * @param i
   * @return
   */
  JsonNumber number(int i);

  /**
   *
   * @param l
   * @return
   */
  JsonNumber number(long l);

  /**
   *
   * @param f
   * @return
   */
  JsonNumber number(float f);

  /**
   *
   * @param d
   * @return
   */
  JsonNumber number(double d);

  /**
   *
   * @param number
   * @return
   */
  JsonNumber number(Number number);

  /**
   *
   * @param str
   * @return
   */
  JsonString string(CharSequence str);

  /**
   *
   * @return
   */
  JsonArray arrayOf();

  /**
   *
   * @param element
   * @return
   */
  JsonArray arrayOf(@Nullable JsonData element);

  /**
   *
   * @param elements
   * @return
   */
  JsonArray arrayOf(@Nullable JsonData @NotNull ... elements);

  /**
   *
   * @param elements
   * @return
   */
  JsonArray arrayOf(Iterable<? extends @Nullable JsonData> elements);

  /**
   *
   * @return
   */
  JsonObject objectOf();

  /**
   *
   * @param name
   * @param value
   * @return
   */
  JsonObject objectOf(CharSequence name, @Nullable JsonData value);

  /**
   *
   * @param entry
   * @return
   */
  JsonObject objectOf(Map.Entry<? extends CharSequence, ? extends @Nullable JsonData> entry);

  /**
   *
   * @param entries
   * @return
   */
  JsonObject objectOf(Map.Entry<? extends CharSequence, ? extends @Nullable JsonData> @NotNull ... entries);

  /**
   *
   * @param entries
   * @return
   */
  JsonObject objectOf(Iterable<? extends Map.Entry<? extends CharSequence, ? extends @Nullable JsonData>> entries);

  /**
   *
   * @param map
   * @return
   */
  JsonObject objectOf(Map<? extends CharSequence, ? extends @Nullable JsonData> map);
}
