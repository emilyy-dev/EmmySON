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

package io.github.emilyydev.emmyson.util;

import net.kyori.examination.string.StringExaminer;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

final class Success<T> implements Try<T> {

  static final Success<Void> VOID_SUCCESS = new Success<>(null);

  private static final long serialVersionUID = -877155561245586683L;

  private final T value;

  Success(final T value) {
    this.value = value;
  }

  @Override
  public boolean isSuccess() {
    return true;
  }

  @Override
  public boolean isFailure() {
    return false;
  }

  @Override
  public <R> Try<R> flatMap(final Function<? super T, ? extends Try<R>> function) {
    requireNonNull(function, "function");
    return function.apply(this.value);
  }

  @Override
  public <R> R fold(
      final Function<? super Throwable, ? extends R> ifFailure,
      final Throwing.Function<? super T, ? extends R> ifSuccess
  ) {
    requireNonNull(ifFailure, "ifFailure");
    requireNonNull(ifSuccess, "ifSuccess");
    try {
      return ifSuccess.tryApply(this.value);
    } catch (final Throwable throwable) {
      return ifFailure.apply(throwable);
    }
  }

  @Override
  public Try<T> tryConsume(final Throwing.Consumer<? super T> consumer) {
    requireNonNull(consumer, "consumer");
    try {
      consumer.tryAccept(this.value);
      return this;
    } catch (final Throwable throwable) {
      return Try.failure(throwable);
    }
  }

  @Override
  public <R> Try<R> tryMap(final Throwing.Function<? super T, ? extends R> function) {
    requireNonNull(function, "function");
    try {
      return Try.success(function.tryApply(this.value));
    } catch (final Throwable throwable) {
      return Try.failure(throwable);
    }
  }

  @Override
  public Try<T> tryFilter(final Throwing.Predicate<? super T> predicate) {
    requireNonNull(predicate, "predicate");
    try {
      return predicate.tryTest(this.value) ? this : Try.failure(new AssertionError());
    } catch (final Throwable throwable) {
      return Try.failure(throwable);
    }
  }

  @Override
  public T getOrThrow() {
    return this.value;
  }

  @Override
  public T getOrThrow(final Supplier<? extends Throwable> exceptionSupplier) {
    requireNonNull(exceptionSupplier, "exceptionSupplier");
    return this.value;
  }

  @Override
  public T getOrElse(final @Nullable T fallback) {
    return this.value;
  }

  @Override
  public T getOrElseGet(final Supplier<? extends @Nullable T> fallbackSupplier) {
    requireNonNull(fallbackSupplier, "fallbackSupplier");
    return this.value;
  }

  @Override
  public Optional<T> toOptional() {
    return Optional.of(this.value);
  }

  @Override
  public Stream<T> toStream() {
    return Stream.of(this.value);
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) { return true; }
    if (null == other || getClass() != other.getClass()) { return false; }
    return this.value.equals(((Success<?>) other).value);
  }

  @Override
  public int hashCode() {
    return this.value.hashCode();
  }

  @Override
  public String toString() {
    return examine(StringExaminer.simpleEscaping());
  }
}
