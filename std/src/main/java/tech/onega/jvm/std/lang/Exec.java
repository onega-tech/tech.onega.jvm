package tech.onega.jvm.std.lang;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;

final public class Exec {

  public static <V> void future(final CompletableFuture<V> future, final Consumer<Throwable> errorHandler, final Consumer<V> valueHandler) {
    future.handle((value, error) -> {
      if (error != null) {
        errorHandler.accept(error);
      }
      else {
        try {
          valueHandler.accept(value);
        }
        catch (final Exception e) {
          errorHandler.accept(e);
        }
      }
      return null;
    });
  }

  public static <R> CompletableFuture<R> future(final Lambda.Consumer<CompletableFuture<R>, Throwable> valueHandler) {
    final var future = new CompletableFuture<R>();
    try {
      valueHandler.invoke(future);
    }
    catch (final Throwable e) {
      future.completeExceptionally(e);
    }
    return future;
  }

  public static <V, E extends Throwable> V lambda(final Lambda.Supplier<V, E> func) throws E {
    return func.invoke();
  }

  public static <E extends Throwable> void locking(final Lock lock, final Lambda.Void<E> function) throws E {
    lock.lock();
    try {
      function.invoke();
    }
    finally {
      lock.unlock();
    }
  }

  public static void quietly(final Lambda.Void<Throwable> lambda) {
    if (lambda != null) {
      try {
        lambda.invoke();
      }
      catch (final Throwable e) {
        //ignore
      }
    }
  }

  public static <R> R quietlyR(final Lambda.Supplier<R, Throwable> lambda) {
    if (lambda != null) {
      try {
        return lambda.invoke();
      }
      catch (final Throwable e) {
        //ignore
      }
    }
    return null;
  }

  public static void sleep(final Duration duration) throws InterruptedException {
    TimeUnit.MILLISECONDS.sleep(duration.toMillis());
  }

}
