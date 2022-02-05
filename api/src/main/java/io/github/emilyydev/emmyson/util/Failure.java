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
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

// me
final class Failure<T> implements Try<T> {

  private static final long serialVersionUID = -7454118374695196247L;

  private static final Set<? extends Class<? extends Error>> NON_RECOVERABLE_ERRORS = Set.of(
      LinkageError.class,
      ThreadDeath.class,
      VirtualMachineError.class
  );

  private static void assertValidThrowable(final Throwable throwable) {
    if (NON_RECOVERABLE_ERRORS.stream().anyMatch(clazz -> clazz.isInstance(throwable))) {
      throw (Error) throwable;
    }
  }

  private final Throwable throwable;

  Failure(final Throwable throwable) {
    assertValidThrowable(throwable);
    this.throwable = throwable;
  }

  @Override
  public boolean isSuccess() {
    return false;
  }

  @Override
  public boolean isFailure() {
    return true;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <R> Try<R> flatMap(final Function<? super T, ? extends Try<R>> function) {
    requireNonNull(function, "function");
    return (Try<R>) this;
  }

  @Override
  public <R> R fold(
      final Function<? super Throwable, ? extends R> ifFailure,
      final Throwing.Function<? super T, ? extends R> ifSuccess
  ) {
    requireNonNull(ifFailure, "ifFailure");
    requireNonNull(ifSuccess, "ifSuccess");
    return ifFailure.apply(this.throwable);
  }

  @Override
  public Try<T> tryConsume(final Throwing.Consumer<? super T> consumer) {
    requireNonNull(consumer, "consumer");
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <R> Try<R> tryMap(final Throwing.Function<? super T, ? extends R> function) {
    requireNonNull(function, "function");
    return (Try<R>) this;
  }

  @Override
  public Try<T> tryFilter(final Throwing.Predicate<? super T> predicate) {
    requireNonNull(predicate, "predicate");
    return this;
  }

  @Override
  public T getOrThrow() {
    return Throwing.sneakyThrow(this.throwable);
  }

  @Override
  public T getOrThrow(final Supplier<? extends Throwable> exceptionSupplier) {
    requireNonNull(exceptionSupplier, "exceptionSupplier");
    return Throwing.sneakyThrow(exceptionSupplier.get());
  }

  @Override
  public T getOrElse(final @Nullable T fallback) {
    return fallback;
  }

  @Override
  public T getOrElseGet(final Supplier<? extends @Nullable T> fallbackSupplier) {
    requireNonNull(fallbackSupplier, "fallbackSupplier");
    return fallbackSupplier.get();
  }

  @Override
  public Optional<T> toOptional() {
    return Optional.empty();
  }

  @Override
  public Stream<T> toStream() {
    return Stream.empty();
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) { return true; }
    if (null == other || getClass() != other.getClass()) { return false; }
    return this.throwable.equals(((Failure<?>) other).throwable);
  }

  @Override
  public int hashCode() {
    return this.throwable.hashCode();
  }

  @Override
  public String toString() {
    return examine(StringExaminer.simpleEscaping());
  }
}
