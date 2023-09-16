package tech.onega.jvm.std.struct.iterator;

import java.util.Iterator;
import tech.onega.jvm.std.annotation.Mutable;
import tech.onega.jvm.std.annotation.Self;
import tech.onega.jvm.std.lang.Equals;
import tech.onega.jvm.std.struct.array.ArrayUtils;

final public class IteratorUtils {

  @SafeVarargs
  public static <V> Iterator<V> concat(final Iterator<V>... iterators) {
    return new IteratorConcatA<>(iterators);
  }

  public static boolean equals(final Iterator<?> iteratorA, final Iterator<?> iteratorB) {
    if (iteratorA == iteratorB) {
      return true;
    }
    else if (iteratorA == null || iteratorB == null) {
      return false;
    }
    while (iteratorA.hasNext()) {
      if (!iteratorB.hasNext()) {
        return false;
      }
      if (Equals.no(iteratorA.next(), iteratorB.next())) {
        return false;
      }
    }
    return !iteratorA.hasNext() && !iteratorB.hasNext();
  }

  public static <V> IteratorLimit<V> limit(final Iterator<V> iterator, final int limit) {
    return new IteratorLimit<>(iterator, limit);
  }

  public static <V> IteratorLimitOffset<V> limitOffset(final Iterator<V> iterator, final int limit, final int offset) {
    return new IteratorLimitOffset<>(iterator, limit, offset);
  }

  public static <V> IteratorOffset<V> offset(final Iterator<V> iterator, final int offset) {
    return new IteratorOffset<>(iterator, offset);
  }

  public static <V> Iterator<V> readOnly(final Iterator<V> original) {
    return new Iterator<>() {

      @Override
      public boolean hasNext() {
        return original.hasNext();
      }

      @Override
      public V next() {
        return original.next();
      }

    };
  }

  @Self
  public static <V> Iterator<V> skip(@Mutable final Iterator<V> iterator, final int offset) {
    int pos = 0;
    while (iterator.hasNext() && pos++ < offset) {
      iterator.next();
    }
    return iterator;
  }

  public static Object[] toArray(final Iterator<?> iterator) {
    Object[] result = new Object[16];
    int pos = 0;
    while (iterator.hasNext()) {
      if (pos == result.length) {
        result = ArrayUtils.grow(result, Object.class, pos + 1, Integer.MAX_VALUE - 8);
      }
      result[pos++] = iterator.next();
    }
    return pos == result.length ? result : ArrayUtils.copy(Object.class, result, pos, 0);
  }

}
