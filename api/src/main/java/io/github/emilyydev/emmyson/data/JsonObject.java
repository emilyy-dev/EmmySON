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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

@Unmodifiable
public interface JsonObject extends JsonData, Map<JsonString, JsonData> {

  @Override
  default DataType<JsonObject> type() {
    return DataType.OBJECT;
  }

  @Contract("_, !null -> !null")
  default JsonData getOrElse(final JsonString name, final @Nullable JsonData fallback) {
    final JsonData data = get(requireNonNull(name, "name"));
    return null == data ? fallback : data;
  }

  default JsonData getOrElse(
      final JsonString name,
      final Supplier<? extends @Nullable JsonData> fallbackSupplier
  ) {
    requireNonNull(fallbackSupplier, "fallbackSupplier");
    final JsonData data = get(requireNonNull(name, "name"));
    return null == data ? fallbackSupplier.get() : data;
  }

  @Override
  default @NotNull String examinableName() {
    return JsonObject.class.getSimpleName();
  }

  @Override
  default @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.concat(
        JsonData.super.examinableProperties(),
        Stream.of(
            ExaminableProperty.of("size", size()),
            ExaminableProperty.of("values", entrySet().stream())
        )
    );
  }
}
