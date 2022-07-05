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
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.Stream;

/**
 * A {@link JsonArray} is a {@link JsonData} that contains a list of {@link JsonData}s.
 */
public interface JsonArray extends JsonData, IntFunction<JsonData> {

  @Override
  default DataType<JsonArray> type() {
    return DataType.ARRAY;
  }

  /**
   * Returns true if this {@code JsonArray} is empty, false otherwise.
   *
   * @return true if this {@code JsonArray} is empty, false otherwise
   */
  default boolean isEmpty() {
    return size() == 0;
  }

  @Override
  default JsonData apply(final int index) {
    return get(index);
  }

  /**
   * Returns the number of elements in this {@code JsonArray}.
   *
   * @return the number of elements in this {@code JsonArray}
   */
  int size();

  /**
   * Returns the {@code JsonData} at the specified index.
   *
   * @param index the index of the {@code JsonData} to return
   * @return the {@code JsonData} at the specified index
   * @throws IndexOutOfBoundsException if the index is out of range (index &lt; 0 || index >= size())
   */
  JsonData get(int index);

  /**
   * Returns a new {@code JsonArray} removing the element at the specified index.
   *
   * @param index the index of the element to be removed
   * @return a new {@code JsonArray} with the element at the specified index removed
   * @throws IndexOutOfBoundsException if the index is out of range (index &lt; 0 || index >= size())
   */
  JsonArray remove(int index);

  /**
   * Returns a new {@code JsonArray} with the given element inserted at the specified index.
   *
   * @param index the index at which the given element is to be inserted
   * @param element the element to be inserted
   * @return a new {@code JsonArray} with the given element inserted at the specified index
   * @throws IndexOutOfBoundsException if the index is out of range (index &lt; 0 || index > size())
   * @throws NullPointerException if the given element is null
   */
  JsonArray insert(int index, JsonData element);

  /**
   * Returns a new {@code JsonArray} with the given element appended to the end.
   *
   * @param element the element to be appended
   * @return a new {@code JsonArray} with the given element appended to the end
   * @throws NullPointerException if the given element is null
   */
  JsonArray append(JsonData element);

  /**
   * Returns a new {@code JsonArray} with the given elements appended to the end.
   *
   * @param elements the elements to be appended
   * @return a new {@code JsonArray} with the given elements appended to the end
   * @throws NullPointerException if the collection is null
   * @throws NullPointerException if any of the given elements is null
   */
  JsonArray appendAll(Collection<? extends JsonData> elements);

  /**
   * Returns a new {@code JsonArray} with the given {@code JsonArray} appended to the end.
   *
   * @param elements the {@code JsonArray} to be appended
   * @return a new {@code JsonArray} with the given {@code JsonArray} appended to the end
   * @throws NullPointerException if the given {@code JsonArray} is null
   */
  JsonArray appendAll(JsonArray elements);

  /**
   * Returns a new {@code JsonArray} with the given element prepended to the beginning.
   *
   * @param element the element to be prepended
   * @return a new {@code JsonArray} with the given element prepended to the beginning
   * @throws NullPointerException if the given element is null
   */
  JsonArray prepend(JsonData element);

  /**
   * Returns a new {@code JsonArray} with the given elements prepended to the beginning.
   *
   * @param elements the elements to be prepended
   * @return a new {@code JsonArray} with the given elements prepended to the beginning
   * @throws NullPointerException if the collection is null
   * @throws NullPointerException if any of the given elements is null
   */
  JsonArray prependAll(Collection<? extends JsonData> elements);

  /**
   * Returns a new {@code JsonArray} with the given {@code JsonArray} prepended to the beginning.
   *
   * @param elements the {@code JsonArray} to be prepended
   * @return a new {@code JsonArray} with the given {@code JsonArray} prepended to the beginning
   * @throws NullPointerException if the given {@code JsonArray} is null
   */
  JsonArray prependAll(JsonArray elements);

  /**
   * Returns a {@link Stream} of the elements in this {@code JsonArray}.
   *
   * @return a {@link Stream} of the elements in this {@code JsonArray}
   */
  Stream<JsonData> stream();

  /**
   * Runs the given action for each element of this {@code JsonArray}.
   *
   * @param action the side-effecting action to be performed on each element
   * @throws NullPointerException if action is null
   */
  default void forEach(final Consumer<? super JsonData> action) {
    stream().forEach(Objects.requireNonNull(action, "action"));
  }

  /**
   * Returns the elements in this {@code JsonArray} in an immutable {@link List}.
   *
   * @return an immutable {@link List} containing the elements of this {@code JsonArray}
   */
  @Unmodifiable List<? extends JsonData> asList();

  @Override
  default @NotNull String examinableName() {
    return JsonArray.class.getSimpleName();
  }

  @Override
  default @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.concat(
        JsonData.super.examinableProperties(),
        Stream.of(
            ExaminableProperty.of("size", size()),
            ExaminableProperty.of("values", stream())
        )
    );
  }
}
