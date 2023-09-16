package tech.onega.jvm.std.concurrent;

import java.util.concurrent.CompletableFuture;
import tech.onega.jvm.std.lang.Lambda;

final public class FutureUtils {

  public static <R> boolean exec(final CompletableFuture<R> future, final Lambda.Supplier<R, Throwable> func) {
    try {
      final R result = func.invoke();
      return future.complete(result);
    }
    catch (final Throwable e) {
      return future.completeExceptionally(e);
    }
  }

  public static boolean fail(final CompletableFuture<?> future, final String message, final Object... args) {
    final var errorMessage = args.length == 0 ? message : String.format(message, args);
    final var error = new IllegalArgumentException(errorMessage);
    return future.completeExceptionally(error);
  }

  public static boolean fail(final CompletableFuture<?> future, final Throwable error) {
    return future.completeExceptionally(error);
  }

  public static <H, R> CompletableFuture<R> handleAndConsume(
    final CompletableFuture<H> handleFuture,
    final CompletableFuture<R> resultFuture,
    final Lambda.Consumer<H, Throwable> func) {
    handleFuture.handle((hResult, hError) -> {
      if (hError != null) {
        return resultFuture.completeExceptionally(hError);
      }
      else {
        try {
          func.invoke(hResult);
          return true;
        }
        catch (final Throwable e) {
          return resultFuture.completeExceptionally(e);
        }
      }
    });
    return resultFuture;
  }

  public static <H, R> CompletableFuture<R> handleAndExec(
    final CompletableFuture<H> handleFuture,
    final CompletableFuture<R> resultFuture,
    final Lambda.Function<H, R, Throwable> func) {
    handleFuture.handle((hResult, hError) -> {
      if (hError != null) {
        return resultFuture.completeExceptionally(hError);
      }
      else {
        try {
          final R result = func.invoke(hResult);
          return resultFuture.complete(result);
        }
        catch (final Throwable e) {
          return resultFuture.completeExceptionally(e);
        }
      }
    });
    return resultFuture;
  }

}
