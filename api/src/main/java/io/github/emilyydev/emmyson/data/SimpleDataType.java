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

import net.kyori.examination.string.StringExaminer;

import java.util.Objects;

final class SimpleDataType<T extends JsonData> implements DataType<T> {

  private static final long serialVersionUID = -6040953449716625026L;

  private final String name;
  private final Class<T> type;

  SimpleDataType(final String name, final Class<T> type) {
    this.name = name;
    this.type = type;
  }

  @Override
  public String name() {
    return this.name;
  }

  @Override
  public Class<T> type() {
    return this.type;
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) { return true; }
    if (!(other instanceof DataType)) { return false; }
    final DataType<?> that = (DataType<?>) other;
    return this.name.equals(that.name()) && this.type.equals(that.type());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.name, this.type);
  }

  @Override
  public String toString() {
    return examine(StringExaminer.simpleEscaping());
  }
}
