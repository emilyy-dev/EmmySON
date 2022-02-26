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

import io.github.emilyydev.emmyson.util.Try;
import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.stream.Stream;

/**
 * This is the base of every data type. It represents any kind of json-representable element or data.
 */
public interface JsonData extends Examinable, Serializable {

  /**
   * Gets this object's type.
   *
   * @return this object's json type
   * @see DataType
   */
  DataType<? extends JsonData> type();

  /**
   * Attempts to map this object to the provided type.
   * <p>
   * Same as {@link DataType#map(JsonData) type.map(jsonData)}.
   * </p>
   *
   * @param type the data type to map this object to
   * @param <T>  the data type to map this object to
   * @return the resulting {@link Try}. See {@link DataType#map(JsonData)}
   * @see DataType#map(JsonData)
   */
  default <T extends JsonData> Try<T> as(final DataType<T> type) {
    return type.map(this);
  }

  @Override
  default @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(ExaminableProperty.of("type", type()));
  }
}
