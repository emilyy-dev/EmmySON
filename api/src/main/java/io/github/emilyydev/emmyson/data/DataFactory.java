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
import io.github.emilyydev.emmyson.util.Throwing;
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
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * The core of this API. Source of creation of various json-representable objects. Provides methods to read and write
 * from/to various sources/sinks of data.
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
   * Shortcut method for the ServiceLoader API, lazily finds DataFactory providers for the class loader that loaded this
   * class.
   *
   * @return a stream of providers
   */
  static Stream<ServiceLoader.Provider<DataFactory>> streamFactories() {
    return ServiceLoader.load(DataFactory.class, DataFactory.class.getClassLoader()).stream();
  }

  /**
   * Attempts to read serialized json from the given string as the provided data type.
   *
   * @param in   the json string to attempt to read
   * @param type the expected data type the serialized data will be attempted to be read as
   * @param <T>  the expected java class representing the deserialized data type
   * @return a {@link Try} object. If the data was parsed and read successfully, it will be a {@code Success} and will
   * contain a value of type {@code T} according to the provided {@code type}. Else it will be a {@code Failure},
   * storing the exception at the moment of failure
   * @see Try#isSuccess()
   * @see Try#isFailure()
   * @see Try#fold(Function, Throwing.Function)
   */
  <T extends JsonData> Try<T> read(String in, DataType<T> type);

  /**
   * Attempts to read json from the given file as the provided data type.
   *
   * @param in   the json string to attempt to read
   * @param type the expected data type the serialized data will be attempted to be read as
   * @param <T>  the expected java class representing the deserialized data type
   * @return a {@link Try} object. If the data was parsed and read successfully, it will be a {@code Success} and will
   * contain a value of type {@code T} according to the provided {@code type}. Else it will be a {@code Failure},
   * storing the exception at the moment of failure
   * @see Try#isSuccess()
   * @see Try#isFailure()
   * @see Try#fold(Function, Throwing.Function)
   */
  <T extends JsonData> Try<T> read(File in, DataType<T> type);

  /**
   * Attempts to read json from the file pointed by the given path as the provided data type.
   *
   * @param in   the json string to attempt to read
   * @param type the expected data type the serialized data will be attempted to be read as
   * @param <T>  the expected java class representing the deserialized data type
   * @return a {@link Try} object. If the data was parsed and read successfully, it will be a {@code Success} and will
   * contain a value of type {@code T} according to the provided {@code type}. Else it will be a {@code Failure},
   * storing the exception at the moment of failure
   * @see Try#isSuccess()
   * @see Try#isFailure()
   * @see Try#fold(Function, Throwing.Function)
   */
  <T extends JsonData> Try<T> read(Path in, DataType<T> type);

  /**
   * Attempts to read json from the given input stream as the provided data type.
   *
   * @param in   the json string to attempt to read
   * @param type the expected data type the serialized data will be attempted to be read as
   * @param <T>  the expected java class representing the deserialized data type
   * @return a {@link Try} object. If the data was parsed and read successfully, it will be a {@code Success} and will
   * contain a value of type {@code T} according to the provided {@code type}. Else it will be a {@code Failure},
   * storing the exception at the moment of failure
   * @see Try#isSuccess()
   * @see Try#isFailure()
   * @see Try#fold(Function, Throwing.Function)
   */
  <T extends JsonData> Try<T> read(InputStream in, DataType<T> type);

  /**
   * Attempts to read json from the given readable source as the provided data type.
   *
   * @param in   the json string to attempt to read
   * @param type the expected data type the serialized data will be attempted to be read as
   * @param <T>  the expected java class representing the deserialized data type
   * @return a {@link Try} object. If the data was parsed and read successfully, it will be a {@code Success} and will
   * contain a value of type {@code T} according to the provided {@code type}. Else it will be a {@code Failure},
   * storing the exception at the moment of failure
   * @see Try#isSuccess()
   * @see Try#isFailure()
   * @see Try#fold(Function, Throwing.Function)
   */
  <T extends JsonData> Try<T> read(Readable in, DataType<T> type);

  /**
   * Creates a new json reader to parse the serialized json in the given string.
   *
   * @param in the serialized json data to parse
   * @return a new reader to parse the provided string
   * @throws IOException if any kind of IO error occurs
   */
  JsonReader createReader(String in) throws IOException;

  /**
   * Creates a new json reader to parse the json in the given file.
   *
   * @param in the file containing the json to parse
   * @return a new reader to parse the contents of the given file
   * @throws IOException if any kind of IO error occurs
   */
  JsonReader createReader(File in) throws IOException;

  /**
   * Creates a new json reader to parse the json in the file pointed by the given path.
   *
   * @param in the file path containing the json to parse
   * @return a new reader to parse the contents of the given file path
   * @throws IOException if any kind of IO error occurs
   */
  JsonReader createReader(Path in) throws IOException;

  /**
   * Creates a new json reader to parse the json provided by the given input stream.
   *
   * @param in the input stream providing the json data to parse
   * @return a new reader to parse the data provided by the input stream
   * @throws IOException if any kind of IO error occurs
   */
  JsonReader createReader(InputStream in) throws IOException;

  /**
   * Creates a new json reader to parse the json from the given readable source.
   *
   * @param in the source to read the json to parse
   * @return a new reader to parse the contents of the given readable source
   * @throws IOException if any kind of IO error occurs
   */
  JsonReader createReader(Readable in) throws IOException;

  /**
   * @param out
   * @param data
   * @return
   */
  Optional<IOException> write(File out, JsonData data);

  /**
   * @param out
   * @param data
   * @return
   */
  Optional<IOException> write(Path out, JsonData data);

  /**
   * @param out
   * @param data
   * @return
   */
  Optional<IOException> write(OutputStream out, JsonData data);

  /**
   * @param out
   * @param data
   * @return
   */
  Optional<IOException> write(Appendable out, JsonData data);

  /**
   * @param out
   * @return
   * @throws IOException
   */
  JsonWriter createWriter(File out) throws IOException;

  /**
   * @param out
   * @return
   * @throws IOException
   */
  JsonWriter createWriter(Path out) throws IOException;

  /**
   * @param out
   * @return
   * @throws IOException
   */
  JsonWriter createWriter(OutputStream out) throws IOException;

  /**
   * @param out
   * @return
   * @throws IOException
   */
  JsonWriter createWriter(Appendable out) throws IOException;

  /**
   * @return
   */
  JsonNull nil();

  /**
   * @param b
   * @return
   */
  JsonBoolean bool(boolean b);

  /**
   * @param b
   * @return
   */
  JsonNumber number(byte b);

  /**
   * @param s
   * @return
   */
  JsonNumber number(short s);

  /**
   * @param i
   * @return
   */
  JsonNumber number(int i);

  /**
   * @param l
   * @return
   */
  JsonNumber number(long l);

  /**
   * @param f
   * @return
   */
  JsonNumber number(float f);

  /**
   * @param d
   * @return
   */
  JsonNumber number(double d);

  /**
   * @param number
   * @return
   */
  JsonNumber number(Number number);

  /**
   * @param str
   * @return
   */
  JsonString string(CharSequence str);

  /**
   * @return an empty {@link JsonArray}
   */
  JsonArray arrayOf();

  /**
   * @param element
   * @return
   */
  JsonArray arrayOf(@Nullable JsonData element);

  /**
   * @param elements
   * @return
   */
  JsonArray arrayOf(@Nullable JsonData @NotNull ... elements);

  /**
   * @param elements
   * @return
   */
  JsonArray arrayOf(Iterable<? extends @Nullable JsonData> elements);

  /**
   * @return an empty {@link JsonObject}
   */
  JsonObject objectOf();

  /**
   * @param name
   * @param value
   * @return
   */
  JsonObject objectOf(CharSequence name, @Nullable JsonData value);

  /**
   * @param entry
   * @return
   */
  JsonObject objectOf(Map.Entry<? extends CharSequence, ? extends @Nullable JsonData> entry);

  /**
   * @param entries
   * @return
   */
  JsonObject objectOf(Map.Entry<? extends CharSequence, ? extends @Nullable JsonData> @NotNull ... entries);

  /**
   * @param entries
   * @return
   */
  JsonObject objectOf(Iterable<? extends Map.Entry<? extends CharSequence, ? extends @Nullable JsonData>> entries);

  /**
   * @param map
   * @return
   */
  JsonObject objectOf(Map<? extends CharSequence, ? extends @Nullable JsonData> map);
}
