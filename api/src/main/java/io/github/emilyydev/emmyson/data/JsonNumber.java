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

import net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * Represents a JSON number.
 */
public interface JsonNumber extends JsonData, Comparable<JsonNumber> {

  @Override
  default DataType<JsonNumber> type() {
    return DataType.NUMBER;
  }

  /**
   * Returns the value of this number as a {@code long}.
   *
   * @return the value of this number as a {@code long}
   * @apiNote This method may be lossy, prefer {@link #asNumber()}.
   * @see #asNumber()
   */
  long longValue();

  /**
   * Returns the value of this number as a {@code double}.
   *
   * @return the value of this number as a {@code double}
   * @apiNote This method may be lossy, prefer {@link #asNumber()}.
   * @see #asNumber()
   */
  double doubleValue();

  /**
   * Returns the value of this number as a {@code Number}.
   * This method should be preferred and the result handled appropriately if the value is known to not fit in a
   * {@code long} or a {@code double} (e.g. checking if it's an instanceof {@code BigInteger} or {@code BigDecimal}).
   *
   * @return the value of this number as a {@code Number}
   */
  Number asNumber();

  // implementers must override this method as j.l.Number does not implement Comparable<Number>
  @Override int compareTo(JsonNumber that);

  @Override
  default @NotNull String examinableName() {
    return JsonNumber.class.getSimpleName();
  }

  @Override
  default @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.concat(
        JsonData.super.examinableProperties(),
        Stream.of(ExaminableProperty.of("value", asNumber()))
    );
  }
}
