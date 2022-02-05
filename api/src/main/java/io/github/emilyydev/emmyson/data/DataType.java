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

import io.github.emilyydev.emmyson.exception.IncompatibleTypesException;
import io.github.emilyydev.emmyson.util.Try;
import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * A type identifier class for json-representable data. It has a name and a Java class type for instances of said json
 * data.
 *
 * @param <T> the Java type representing the json type
 */
public interface DataType<T extends JsonData> extends Examinable, Serializable {

  /**
   * Data type representing the "null" json literal.
   */
  DataType<JsonNull> NULL = type("null", JsonNull.class);

  /**
   * Data type representing a json boolean ({@code true}/{@code false} literals).
   */
  DataType<JsonBoolean> BOOLEAN = type("boolean", JsonBoolean.class);

  /**
   * Data type representing numeric json values.
   */
  DataType<JsonNumber> NUMBER = type("number", JsonNumber.class);

  /**
   * Data type representing json strings.
   */
  DataType<JsonString> STRING = type("string", JsonString.class);

  /**
   * Data type representing json arrays.
   */
  DataType<JsonArray> ARRAY = type("array", JsonArray.class);

  /**
   * Data type representing json objects.
   */
  DataType<JsonObject> OBJECT = type("object", JsonObject.class);

  /**
   * Creates a new standard data type for json-representable data.
   *
   * @param name the name of the json type
   * @param type the Java class type representing the json type
   * @param <T>  the Java type representing the json type
   * @return a new {@link DataType} object
   */
  static <T extends JsonData> DataType<T> type(final String name, final Class<T> type) {
    return new SimpleDataType<>(requireNonNull(name, "name"), requireNonNull(type, "type"));
  }

  /**
   * Gets the type name.
   *
   * @return the type name
   */
  String name();

  /**
   * Gets the Java class type of this json type.
   *
   * @return the class of this json type
   */
  Class<T> type();

  /**
   * Gets a predicate that tests if a provided {@link JsonData} can safely be used in the function returned by
   * {@link #mapper()}.
   *
   * @return a predicate that asserts whether or not the given {@link JsonData} is mappable to this type
   */
  default Predicate<? super JsonData> predicate() {
    return type()::isInstance;
  }

  /**
   * Returns a {@link Function} that maps the given {@link JsonData} to this type.
   * <p>
   * Custom implementations feel free to introduce mapping to custom types through other methods besides casting, for
   * example, given a custom {@code JsonUuid} type, mapping from a {@link JsonString} could be allowed, by
   * deserializing the string value to the corresponding UUID.
   * </p>
   *
   * @return a {@link Function} that converts the given {@link JsonData} to this type
   */
  default Function<? super JsonData, ? extends T> mapper() {
    return type()::cast;
  }

  /**
   * Attempts to map the given {@link JsonData} to this type. The returned {@link Try} object will either contain the
   * mapped object if it {@link Try#isSuccess()}, or an {@link IncompatibleTypesException} if it
   * {@link Try#isFailure()}.
   *
   * @param jsonData the object to map
   * @return the resulting {@link Try}
   */
  default Try<T> map(final JsonData jsonData) {
    return Try.success(jsonData)
        .filter(predicate())
        .map(mapper())
        .fold(throwable -> Try.failure(new IncompatibleTypesException(
            String.format("JSON data type %s is not convertible to data type %s", jsonData.type().name(), name())
        )), Try::success);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default @NotNull String examinableName() {
    return DataType.class.getSimpleName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(
        ExaminableProperty.of("name", name()),
        ExaminableProperty.of("type", type())
    );
  }
}
