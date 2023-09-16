package tech.onega.jvm.std.struct.cache;

import org.testng.annotations.Test;
import tech.onega.jvm.std.validate.Check;

public class CacheCopyOnWriteTest {

  private static <K, V, E extends Throwable> Cache<Integer, String, RuntimeException> createCache(
    final int initialSize,
    final int capacity) {
    return new CacheCopyOnWrite<>(initialSize, capacity, String::valueOf);
  }

  @Test
  public void testClear() {
    try (final var cache = createCache(32, 32)) {
      Check.isTrue(cache.isEmpty());
      Check.equals(cache.get(1), "1");
      Check.isTrue(cache.contains(1));
      Check.isFalse(cache.isEmpty());
      cache.clear();
      Check.isTrue(cache.isEmpty());
      Check.isFalse(cache.contains(1));
    }
  }

  @Test
  public void testGet() {
    try (final var cache = createCache(32, 32)) {
      Check.equals(cache.capacity(), 32);
      Check.equals(cache.size(), 0);
      Check.isTrue(cache.isEmpty());
      Check.isFalse(cache.contains(1));
      Check.equals(cache.get(1), "1");
      Check.isTrue(cache.contains(1));
      Check.equals(cache.capacity(), 32);
      Check.equals(cache.size(), 1);
      Check.isFalse(cache.isEmpty());
    }
  }

  @Test
  public void testMaxCapacity() {
    try (final var cache = createCache(2, 2)) {
      Check.equals(cache.size(), 0);
      Check.equals(cache.get(1), "1");
      Check.isTrue(cache.contains(1));
      Check.equals(cache.size(), 1);
      Check.equals(cache.get(2), "2");
      Check.isTrue(cache.contains(1));
      Check.isTrue(cache.contains(2));
      Check.equals(cache.size(), 2);
      Check.equals(cache.get(2), "2");
      Check.isTrue(cache.contains(1));
      Check.isTrue(cache.contains(2));
      Check.equals(cache.size(), 2);
      Check.equals(cache.get(3), "3");
      Check.isTrue(cache.contains(3));
      Check.isFalse(cache.contains(1));
      Check.isTrue(cache.contains(2));
      Check.isTrue(cache.contains(3));
      Check.equals(cache.size(), 2);
    }
  }

}
