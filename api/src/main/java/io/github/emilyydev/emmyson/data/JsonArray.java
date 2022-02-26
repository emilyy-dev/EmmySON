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

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface JsonArray extends JsonData {

  @Override
  default DataType<JsonArray> type() {
    return DataType.ARRAY;
  }

  int size();
  JsonData get(int index);
  JsonArray append(final JsonData jsonData);
  JsonArray appendAll(final Iterable<? extends JsonData> elements);
  JsonArray prepend(final JsonData jsonData);
  JsonArray prependAll(final Iterable<? extends JsonData> elements);

  Stream<JsonData> stream();

  default boolean isEmpty() {
    return size() == 0;
  }

  default void forEach(final Consumer<? super JsonData> action) {
    for (int i = 0; i < size(); ++i) {
      action.accept(get(i));
    }
  }

  List<? extends JsonData> asJavaList();

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
