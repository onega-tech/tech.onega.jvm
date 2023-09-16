package tech.onega.jvm.std.struct.iterable;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;
import tech.onega.jvm.std.lang.Equals;
import tech.onega.jvm.std.struct.array.ArrayUtils;
import tech.onega.jvm.std.struct.iterator.IteratorUtils;
import tech.onega.jvm.std.struct.vector.Vector;

final public class IterableUtils {

  @SafeVarargs
  public static <V> Iterable<V> concat(final Iterable<V>... iterables) {
    return () -> new Iterator<>() {

      private int index = 0;

      private Iterator<V> iterator;

      @Override
      public boolean hasNext() {
        if (iterables.length == 0) {
          return false;
        }
        while (index < iterables.length) {
          if (iterator == null) {
            iterator = iterables[index].iterator();
          }
          if (iterator.hasNext()) {
            return true;
          }
          index++;
          iterator = null;
        }
        return false;
      }

      @Override
      public V next() {
        return iterator.next();
      }

    };
  }

  public static <V> boolean contains(final Iterable<V> iterable, final V value) {
    for (final V v : iterable) {
      if (Equals.yes(v, value)) {
        return true;
      }
    }
    return false;
  }

  public static boolean equals(final Iterable<?> iterableA, final Iterable<?> iterableB) {
    if (iterableA == iterableB) {
      return true;
    }
    else if (iterableA == null || iterableB == null) {
      return false;
    }
    else {
      return IteratorUtils.equals(iterableA.iterator(), iterableB.iterator());
    }
  }

  public static <V> int indexOf(final Iterable<V> iterable, final V value) {
    int i = 0;
    for (final V cv : iterable) {
      if (Equals.yes(cv, value)) {
        return i;
      }
      i++;
    }
    return -1;
  }

  public static <V> Iterable<V> limitOffset(final Iterable<V> iterable, final int limit, final int offset) {
    return () -> IteratorUtils.limitOffset(iterable.iterator(), limit, offset);
  }

  public static <V> Iterable<V> ofStream(final Stream<V> stream) {
    return () -> stream.iterator();
  }

  @SuppressWarnings("unchecked")
  public static Object[] toArray(final Iterable<?> iterable) {
    return toArray((Iterable<Object>) iterable, Object.class);
  }

  public static <T> T[] toArray(final Iterable<T> iterable, final Class<T> componentType) {
    final int size = trySizeOf(iterable);
    if (size >= 0) {
      return toArray(iterable, size, 0, componentType);
    }
    else {
      T[] array = ArrayUtils.create(componentType, 16);
      int length = 0;
      for (final T v : iterable) {
        if (length == array.length) {
          array = ArrayUtils.grow(array, componentType, length + 1, Integer.MAX_VALUE - 8);
        }
        array[length++] = v;
      }
      return ArrayUtils.copy(componentType, array, length, 0);
    }
  }

  public static <T> T[] toArray(final Iterable<T> iterable, final int limit, final int offset,
    final Class<T> componentType) {
    final T[] result = ArrayUtils.create(componentType, limit);
    int i = 0;
    for (final T v : iterable) {
      if (i >= offset) {
        result[i] = v;
      }
      i++;
    }
    return result;
  }

  public static String toString(final Iterable<?> iterable) {
    if (iterable == null) {
      return "null";
    }
    final StringBuilder builder = new StringBuilder();
    builder.append('[');
    final Iterator<?> iterator = iterable.iterator();
    while (iterator.hasNext()) {
      builder.append(iterator.next());
      if (iterator.hasNext()) {
        builder.append(",");
      }
    }
    builder.append(']');
    return builder.toString();
  }

  public static int trySizeOf(final Iterable<?> iterable) {
    if (iterable instanceof Collection) {
      return ((Collection<?>) iterable).size();
    }
    else if (iterable instanceof Vector) {
      return ((Vector<?>) iterable).size();
    }
    return -1;
  }

}
