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
 * A {@link JsonString} is a {@link JsonData} that represents a string.
 */
public interface JsonString extends JsonData, Comparable<JsonString> {

  @Override
  default DataType<JsonString> type() {
    return DataType.STRING;
  }

  /**
   * Returns the length of this JSON string.
   *
   * @return the length of this string
   */
  int length();

  /**
   * Returns the character at the specified index.
   *
   * @param index the index of the character
   * @return the character at the specified index
   * @throws IndexOutOfBoundsException if the index is out of range (index &lt; 0 || index >= length())
   */
  char charAt(int index);

  /**
   * Returns a substring of this JSON string.
   *
   * @param start the beginning index, inclusive
   * @param end   the ending index, exclusive
   * @return a substring of this JSON string
   * @throws IndexOutOfBoundsException if the start or end index is out of range
   *                                   (start &lt; 0 || start >= length() || end &lt; 0 || end > length())
   */
  JsonString substring(int start, int end);

  /**
   * Returns the string value of this JSON string.
   *
   * @return the string value of this JSON string
   */
  String asString();

  @Override
  default int compareTo(final JsonString that) {
    return asString().compareTo(that.asString());
  }

  @Override
  default @NotNull String examinableName() {
    return JsonString.class.getSimpleName();
  }

  @Override
  default @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.concat(
        JsonData.super.examinableProperties(),
        Stream.of(ExaminableProperty.of("value", asString()))
    );
  }
}
