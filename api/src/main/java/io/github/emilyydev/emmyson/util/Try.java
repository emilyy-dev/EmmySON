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

import net.kyori.examination.Examinable;
import net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;

/**
 * A {@code Try} monad type to facilitate error handling and data processing with failure potential.
 * <p>
 * Lets one write code that may fail without having to focus on try-catch blocks in a fluent way.
 * </p>
 * <p>
 * A {@code Try} object can be either a {@link Success} which contains a result value of type {@code T}, or a
 * {@link Failure} which contains a {@link Throwable}.
 * </p>
 * <p>
 * A {@code Try} object is {@link Serializable} if the contained value is serializable. That is:
 * </p>
 * <ul>
 *   <li>
 *     In case of a {@link Success}, the object is serializable if the contained result value of type {@code T}
 *     is serializable.
 *   </li>
 *   <li>
 *     In case of a {@link Failure}, the object is serializable if the contained exception is serializable.
 *   </li>
 * </ul>
 * <p>
 * While a {@code Try} object is immutable by design, no guarantees are made about the mutability of the stored value.
 * </p>
 * <p>
 * <strong>Note:</strong> a small set of {@link Error}s are deemed non-recoverable state when creating a {@link Failure}
 * and will be rethrown instead (subclasses are included):
 * </p>
 * <ul>
 *   <li>{@link LinkageError}</li>
 *   <li>{@link ThreadDeath}</li>
 *   <li>{@link VirtualMachineError}</li>
 * </ul>
 *
 * @param <T> the contained result type in case of success
 */
public interface Try<T> extends Examinable, Serializable {

  /**
   * Static factory method for a successful {@code Try} object from a {@code T} value.
   *
   * @param value the result value to create the successful {@code Try} with, must not be {@code null}
   * @param <T>   the result type
   * @return a new {@link Success} object
   */
  @Contract("_ -> new")
  static <T> Try<T> success(final T value) {
    return new Success<>(requireNonNull(value, "value"));
  }

  /**
   * Static factory method for a failed {@code Try} object from a {@link Throwable}.
   *
   * @param throwable the underlying exception to create the failed {@code Try} with, must not be {@code null}
   * @param <T>       the (inferred) result type
   * @return a new {@link Failure} object
   */
  @Contract("_ -> new")
  static <T> Try<T> failure(final Throwable throwable) {
    return new Failure<>(requireNonNull(throwable, "throwable"));
  }

  /**
   * Create a {@code Try} based on a {@link Throwing.Runnable} action.
   *
   * @param action the action to run which may fail
   * @return the resulting {@code Try} object, a {@link Failure} if the action threw an exception or a (null-valued)
   * {@link Success} if it ran normally
   */
  static Try<Void> of(final Throwing.Runnable action) {
    requireNonNull(action, "action");
    try {
      action.tryRun();
      return Success.VOID_SUCCESS;
    } catch (final Throwable throwable) {
      return failure(throwable);
    }
  }

  /**
   * Same as {@link #of(Throwing.Runnable) of(action::run)}.
   *
   * @param action the action to run which may fail
   * @return the resulting {@code Try} object, a {@link Failure} if the action threw an exception or a (null-valued)
   * {@link Success} if it ran normally
   * @see #of(Throwing.Runnable)
   */
  static Try<Void> ofRunnable(final Runnable action) {
    requireNonNull(action, "action");
    return of(action::run);
  }

  /**
   * Create a {@code Try} object with from a {@link Throwing.Supplier} to provide a value {@code T} in case of
   * {@link Success}. The supplier may fail, returning a {@link Failure} instead.
   *
   * @param supplier the supplying action to run which may fail
   * @param <T>      the result type
   * @return the resulting {@code Try} object, a {@link Failure} if the action threw an exception or a {@link Success}
   * containing the supplied value if it ran normally
   */
  static <T> Try<T> of(final Throwing.Supplier<? extends T> supplier) {
    requireNonNull(supplier, "supplier");
    try {
      return success(supplier.tryGet());
    } catch (final Throwable throwable) {
      return failure(throwable);
    }
  }

  /**
   * Same as {@link #of(Throwing.Supplier) of(supplier::get)}.
   *
   * @param supplier the supplying action to run which may fail
   * @param <T>      the result type
   * @return the resulting {@code Try} object, a {@link Failure} if the action threw an exception or a {@link Success}
   * containing the supplied value if it ran normally
   * @see #of(Throwing.Supplier)
   */
  static <T> Try<T> ofSupplier(final Supplier<? extends T> supplier) {
    requireNonNull(supplier, "supplier");
    return of(supplier::get);
  }

  /**
   * Same as {@link #of(Throwing.Supplier) of(callable::call)}.
   *
   * @param callable the callable action to run which may fail
   * @param <T>      the result type
   * @return the resulting {@code Try} object, a {@link Failure} if the action threw an exception or a {@link Success}
   * containing the supplied value if it ran normally
   * @see #of(Throwing.Supplier)
   */
  static <T> Try<T> ofCallable(final Callable<? extends T> callable) {
    requireNonNull(callable, "callable");
    return of(callable::call);
  }

  /**
   * Checks if this {@code Try} object is a {@link Success}.
   *
   * @return {@code true} if this {@code Try} is a {@link Success} and contains a result value (may be {@code null}).
   * {@code false} otherwise.
   */
  boolean isSuccess();

  /**
   * Checks if this {@code Try} object is a {@link Failure}.
   *
   * @return {@code true} if this {@code Try} is a {@link Failure}. {@code false} otherwise.
   */
  boolean isFailure();

  /**
   * Same as {@link #tryConsume(Throwing.Consumer) tryConsume(consumer::accept)}.
   *
   * @param consumer the consumer that will take the contained value {@code T} if {@link #isSuccess()}
   * @return this {@code Try} object, or if the consumer action is ran and throws an exception, a new {@link Failure}
   * with the exception thrown
   * @see #tryConsume(Throwing.Consumer)
   */
  default Try<T> consume(final Consumer<? super T> consumer) {
    return tryConsume(consumer::accept);
  }

  /**
   * Same as {@link #tryMap(Throwing.Function) tryMap(function::apply)}.
   *
   * @param function the function to apply to the contained value {@code T} if {@link #isSuccess()}
   * @param <R>      the new result type
   * @return this object if {@link #isFailure()}, a new {@link Success} after running the contained value through
   * the given function if {@link #isSuccess()} or a new {@link Failure} if the function throws an exception
   * @see #tryMap(Throwing.Function)
   */
  default <R> Try<R> map(final Function<? super T, ? extends R> function) {
    return tryMap(function::apply);
  }

  /**
   * Same as {@link #tryFilter(Throwing.Predicate) tryFilter(predicate::test)}.
   *
   * @param predicate the predicate to test the contained value with if {@link #isSuccess()}
   * @return this object if {@link #isFailure()} or if {@link #isSuccess()} and the predicate test passes, or a new
   * {@link Failure} if it doesn't or if it fails
   * @see #tryFilter(Throwing.Predicate)
   */
  default Try<T> filter(final Predicate<? super T> predicate) {
    return tryFilter(predicate::test);
  }

  /**
   * Returns the given function applied to the value from this {@link Success} or returns this if this is a
   * {@link Failure}.
   *
   * @param function the function to apply to the contained value if {@link #isSuccess()}
   * @param <R>      the new result type
   * @return {@code this} if {@link #isFailure()} or the returned {@code Try} object after applying the function to the
   * contained value if {@link #isSuccess()}
   */
  <R> Try<R> flatMap(Function<? super T, ? extends Try<R>> function);

  /**
   * Applies {@code ifFailure} to the stored exception if {@link #isFailure()} or {@code ifSuccess} to the contained
   * value if {@link #isSuccess()}, and returned the returned value.
   * <p>
   * If {@code ifSuccess} is ran and throws an exception, {@code ifFailure} is applied to the thrown exception.
   * </p>
   *
   * @param ifFailure the function to apply if {@link #isFailure()}
   * @param ifSuccess the function to apply if {@link #isSuccess()}
   * @param <R>       the result type
   * @return the result of running either function as applicable
   */
  <R> R fold(Function<? super Throwable, ? extends R> ifFailure, Throwing.Function<? super T, ? extends R> ifSuccess);

  /**
   * If {@link #isSuccess()}, the given consumer will be passed the contained value. If the consumer action fails, a new
   * {@link Failure} will be returned with the thrown exception, else {@code this} is returned. If {@link #isFailure()},
   * {@code this} is returned.
   *
   * @param consumer the consumer that will take the contained value {@code T} if {@link #isSuccess()}
   * @return this {@code Try} object, or if the consumer action is ran and throws an exception, a new {@link Failure}
   * with the exception thrown
   */
  Try<T> tryConsume(Throwing.Consumer<? super T> consumer);

  /**
   * If {@link #isSuccess()}, the given function will be applied to the contained value and the returned value will be
   * put in a new {@link Success}, then returned. If the function fails, a new {@link Failure} will be returned with the
   * thrown exception. If {@link #isFailure()}, {@code this} is returned.
   *
   * @param function the function to apply to the contained value {@code T} if {@link #isSuccess()}
   * @param <R>      the new result type
   * @return this object if {@link #isFailure()}, a new {@link Success} after running the contained value through
   * the given function if {@link #isSuccess()} or a new {@link Failure} if the function throws an exception
   */
  <R> Try<R> tryMap(Throwing.Function<? super T, ? extends R> function);

  /**
   * If {@link #isSuccess()}, the given predicate will test the contained value. If the predicate itself fails, a new
   * {@link Failure} will be returned with the thrown exception, else {@code this} is returned. If the predicate test
   * succeeds, {@code this} is returned, else a new {@link Failure} is returned. If {@link #isFailure()}, {@code this}
   * is returned.
   *
   * @param predicate the predicate to test the contained value with if {@link #isSuccess()}
   * @return this object if {@link #isFailure()} or if {@link #isSuccess()} and the predicate test passes, or a new
   * {@link Failure} if it doesn't or if it fails
   */
  Try<T> tryFilter(Throwing.Predicate<? super T> predicate);

  /**
   * Gets the existing contained value {@code T} or throws the stored exception if this object {@link #isFailure()}.
   * <p>
   * <strong>Note:</strong> if {@link #isFailure()}, the stored exception will be "sneakily" thrown as-is, and
   * <strong>not</strong> wrapped in an unchecked exception.
   * </p>
   *
   * @return the contained value {@code T} if {@link #isSuccess()}
   */
  T getOrThrow();

  /**
   * Gets the existing contained value {@code T} or throws the exception provided by the given supplier if this object
   * {@link #isFailure()}.
   * <p>
   * <strong>Note:</strong> if {@link #isFailure()}, the provided exception will be "sneakily" thrown as-is, and
   * <strong>not</strong> wrapped in an unchecked exception.
   * </p>
   *
   * @param exceptionSupplier the supplier that provides the exception to be thrown if {@link #isFailure()}
   * @return the contained value {@code T} if {@link #isSuccess()}
   */
  T getOrThrow(Supplier<? extends Throwable> exceptionSupplier);

  /**
   * Gets the existing contained value {@code T} or returns the provided value if this object {@link #isFailure()}.
   *
   * @param fallback a fallback value if {@link #isFailure()}
   * @return the contained value {@code T} if {@link #isSuccess()}, or the provided value if {@link #isFailure()}
   */
  @Contract("!null -> !null") T getOrElse(@Nullable T fallback);

  /**
   * Gets the existing contained value {@code T} or runs the given supplier and returns the provided value if this
   * object {@link #isFailure()}.
   *
   * @param fallbackSupplier a supplier to provide a fallback value if {@link #isFailure()}
   * @return the contained value {@code T} if {@link #isSuccess()}, or the value provided by the supplier if
   * {@link #isFailure()}
   */
  T getOrElseGet(Supplier<? extends @Nullable T> fallbackSupplier);

  /**
   * Creates an {@link Optional} from this {@code Try} object.
   *
   * @return an empty optional if {@link #isFailure()}, or an optional containing the value {@code T} if
   * {@link #isSuccess()}
   */
  Optional<T> toOptional();

  /**
   * Creates a {@link Stream} from this {@code Try} object.
   *
   * @return an empty stream if {@link #isFailure()}, or a stream containing the single value {@code T} if
   * {@link #isSuccess()}
   */
  Stream<T> toStream();

  @Override
  default @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(ExaminableProperty.of("value", fold(identity(), identity()::apply)));
  }
}
