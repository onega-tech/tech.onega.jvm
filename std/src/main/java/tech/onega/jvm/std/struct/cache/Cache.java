package tech.onega.jvm.std.struct.cache;

import javax.validation.constraints.NotNull;
import tech.onega.jvm.std.annotation.Lazy;
import tech.onega.jvm.std.lang.Lambda;

public interface Cache<K, V, E extends Throwable> extends AutoCloseable {

  static <K, V, E extends Throwable> Cache<K, V, E> basic(final int initialSize, final int capacity,
    final Lambda.Function<K, V, E> factory) {
    return new CacheBasic<>(initialSize, capacity, factory);
  }

  static <K, V, E extends Throwable> Cache<K, V, E> concurrent(final int initialSize, final int capacity,
    final Lambda.Function<K, V, E> factory) {
    return new CacheConcurrent<>(initialSize, capacity, factory);
  }

  static <K, V, E extends Throwable> Cache<K, V, E> copyOnWrite(final int initialSize, final int capacity,
    final Lambda.Function<K, V, E> factory) {
    return new CacheCopyOnWrite<>(initialSize, capacity, factory);
  }

  int capacity();

  void clear();

  @Override
  default void close() {
    clear();
  }

  boolean contains(K key);

  @Lazy
  @NotNull
  V get(final K key) throws E;

  default boolean isEmpty() {
    return size() == 0;
  }

  int size();

}
