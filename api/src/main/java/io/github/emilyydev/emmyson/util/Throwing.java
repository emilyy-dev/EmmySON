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

/**
 * Utility class for throwing functional interfaces.
 */
public final class Throwing {

  @SuppressWarnings("unchecked")
  static <X extends Throwable, R> R sneakyThrow(final Throwable throwable) throws X {
    throw (X) throwable;
  }

  private Throwing() { }

  /**
   * Extension of {@link java.lang.Runnable} that allows for throwing exceptions.
   */
  @FunctionalInterface
  public interface Runnable extends java.lang.Runnable {

    void tryRun() throws Throwable;

    @Override
    default void run() {
      try {
        tryRun();
      } catch (final Throwable throwable) {
        sneakyThrow(throwable);
      }
    }
  }

  /**
   * Extension of {@link java.util.function.Function} that allows for throwing exceptions.
   *
   * @param <T> the function input type
   * @param <R> the function output type
   */
  @FunctionalInterface
  public interface Function<T, R> extends java.util.function.Function<T, R> {

    R tryApply(T t) throws Throwable;

    @Override
    default R apply(final T t) {
      try {
        return tryApply(t);
      } catch (final Throwable throwable) {
        return sneakyThrow(throwable);
      }
    }
  }

  /**
   * Extension of {@link java.util.function.Predicate} that allows for throwing exceptions.
   *
   * @param <T> the input type to test
   */
  @FunctionalInterface
  public interface Predicate<T> extends java.util.function.Predicate<T> {

    boolean tryTest(T t) throws Throwable;

    @Override
    default boolean test(final T t) {
      try {
        return tryTest(t);
      } catch (final Throwable throwable) {
        return sneakyThrow(throwable);
      }
    }
  }

  /**
   * Extension of {@link java.util.function.Consumer} that allows for throwing exceptions.
   *
   * @param <T> the input type
   */
  @FunctionalInterface
  public interface Consumer<T> extends java.util.function.Consumer<T> {

    void tryAccept(T t) throws Throwable;

    @Override
    default void accept(final T t) {
      try {
        tryAccept(t);
      } catch (final Throwable throwable) {
        sneakyThrow(throwable);
      }
    }
  }

  /**
   * Extension of {@link java.util.function.Supplier} that allows for throwing exceptions.
   *
   * @param <T> the output type to produce
   */
  @FunctionalInterface
  public interface Supplier<T> extends java.util.function.Supplier<T> {

    T tryGet() throws Throwable;

    @Override
    default T get() {
      try {
        return tryGet();
      } catch (final Throwable throwable) {
        return sneakyThrow(throwable);
      }
    }
  }
}
