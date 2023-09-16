package tech.onega.jvm.std.struct.array;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import tech.onega.jvm.std.annotation.Immutable;
import tech.onega.jvm.std.annotation.Mutable;
import tech.onega.jvm.std.lang.Equals;
import tech.onega.jvm.std.lang.Lambda;
import tech.onega.jvm.std.struct.stream.StreamUtils;
import tech.onega.jvm.std.struct.vector.Vector;

final public class ArrayUtils {

  public static void appendArray(final StringBuilder builder, final Object[] vals, final int limit, final int offset) {
    if (vals == null) {
      builder.append("null");
    }
    else {
      builder.append('[');
      final int lastPos = offset + limit - 1;
      for (int i = offset; i < offset + limit; i++) {
        if (vals[i] instanceof Object[]) {
          final Object[] tmp = (Object[]) vals[i];
          appendArray(builder, tmp, tmp.length, 0);
        }
        else {
          builder.append(vals[i]);
        }
        if (i < lastPos) {
          builder.append(", ");
        }
      }
      builder.append(']');
    }
  }

  public static <V, E extends Throwable> void apply(final @Mutable V[] vals, final int limit, final int offset,
    final Lambda.Function<V, V, E> mapper) throws E {
    for (int i = offset; i < limit + offset; i++) {
      vals[i] = mapper.invoke(vals[i]);
    }
  }

  @SafeVarargs
  public static <T> List<T> asList(final T... a) {
    return java.util.Arrays.asList(a);
  }

  public static <V> Collector<V, ?, ? extends Object[]> collector(final int size) {
    final Object[] result = new Object[size];
    return collector(result);
  }

  public static <V, Z> Collector<V, ?, ? extends Z[]> collector(final Z[] data) {
    return new Collector<V, Z[], Z[]>() {

      int pos = 0;

      @SuppressWarnings("unchecked")
      @Override
      public BiConsumer<Z[], V> accumulator() {
        return (tmp, v) -> tmp[pos++] = (Z) v;
      }

      @Override
      public java.util.Set<Characteristics> characteristics() {
        return StreamUtils.EMPTY_COLLECTOR_CHARACTERISTICS;
      }

      @Override
      public BinaryOperator<Z[]> combiner() {
        return null;
      }

      @Override
      public Function<Z[], Z[]> finisher() {
        return tmp -> tmp;
      }

      @Override
      public Supplier<Z[]> supplier() {
        return () -> data;
      }

    };
  }

  public static <V> boolean contains(final V[] vals, final V value, final int limit, final int offset) {
    for (int i = offset; i < offset + limit; i++) {
      if (Equals.yes(vals[i], value)) {
        return true;
      }
    }
    return false;
  }

  public static <V> V[] copy(final Class<V> componentType, final V[] vals, final int limit, final int offset) {
    final V[] result = create(componentType, limit);
    System.arraycopy(vals, offset, result, 0, limit);
    return result;
  }

  public static void copyTo(final Object[] vals, final int limit, final int offset, final Object[] dest,
    final int destOffset) {
    System.arraycopy(vals, offset, dest, destOffset, limit);
  }

  public static <V> void copyTo(final V[] vals, final int limit, final int offset, final Collection<V> collection) {
    for (int i = offset; i < offset + limit; i++) {
      collection.add(vals[i]);
    }
  }

  @SuppressWarnings("unchecked")
  public static <V> V[] create(final Class<V> componentType, final int size) {
    final Object result = componentType == Object.class ? new Object[size] : Array.newInstance(componentType, size);
    return (V[]) result;
  }

  public static <T> T[] expand(final T[] src, final Class<T> componentType, final int newSize) {
    return expand(src, componentType, newSize, newSize);
  }

  public static <T> T[] expand(final T[] src, final Class<T> componentType, final int newSize, final int maxSize) {
    final int size = Math.min(newSize, maxSize);
    final T[] dest = ArrayUtils.create(componentType, size);
    System.arraycopy(src, 0, dest, 0, src.length);
    return dest;
  }

  public static <V, E extends Throwable> V[] filter(
    final @Immutable V[] vals,
    final int limit,
    final int offset,
    final Lambda.Function<V, Boolean, E> filter) throws E {
    if (vals == null) {
      return null;
    }
    @SuppressWarnings("unchecked")
    final Class<V> componentType = (Class<V>) vals.getClass().getComponentType();
    final V[] tmp = create(componentType, vals.length);
    int resultSize = 0;
    for (int i = offset; i < offset + limit; i++) {
      final V v = vals[i];
      if (filter.invoke(v)) {
        tmp[resultSize++] = v;
      }
    }
    final V[] result = create(componentType, resultSize);
    System.arraycopy(tmp, 0, result, 0, resultSize);
    return result;
  }

  public static <V, E extends Throwable> int filter(
    final @Immutable V[] vals,
    final int limit,
    final int offset,
    final @Mutable V[] dest,
    final int destOffset,
    final Lambda.Function<V, Boolean, E> filter) throws E {
    if (vals == null || dest == null) {
      return 0;
    }
    int resultSize = 0;
    for (int i = 0; i < limit; i++) {
      final V v = vals[offset + i];
      if (filter.invoke(v)) {
        dest[resultSize + destOffset] = v;
        resultSize++;
      }
    }
    return resultSize;
  }

  public static <T, E extends Throwable> void foreach(final T[] array, final Lambda.Consumer<T, E> consumer) throws E {
    for (final T v : array) {
      consumer.invoke(v);
    }
  }

  public static <T> T[] grow(final T[] src, final Class<T> componentType, final int needSize, final int maxSize) {
    if (src.length > needSize) {
      return src;
    }
    final int newSize = nextCapacity(src.length, needSize, maxSize);
    return expand(src, componentType, newSize, maxSize);
  }

  public static byte[] growBytes(final byte[] src, final int needSize, final int maxSize) {
    if (src.length > needSize) {
      return src;
    }
    final int newSize = nextCapacity(src.length, needSize, maxSize);
    final byte[] result = new byte[newSize];
    System.arraycopy(src, 0, result, 0, Math.min(src.length, newSize));
    return result;
  }

  public static long[] growLong(final long[] src, final int needSize, final int maxSize) {
    if (src.length > needSize) {
      return src;
    }
    final int newSize = nextCapacity(src.length, needSize, maxSize);
    final long[] result = new long[newSize];
    System.arraycopy(src, 0, result, 0, Math.min(src.length, newSize));
    return result;
  }

  @SafeVarargs
  public static <V> Iterator<V> iterator(final V... data) {
    return iterator(data, data.length, 0);
  }

  public static <V> Iterator<V> iterator(final V[] data, final int limit, final int offset) {
    return new Iterator<>() {

      int end = offset + limit;

      int index = offset;

      @Override
      public boolean hasNext() {
        return index < end;
      }

      @Override
      public V next() {
        return data[index++];
      }

    };
  }

  public static <V, R, E extends Throwable> void map(
    final @Immutable V[] vals,
    final int limit,
    final int offset,
    final @Mutable R[] dest,
    final int destOffset,
    final Lambda.Function<V, R, E> mapper) throws E {
    for (int i = 0; i < limit; i++) {
      dest[i + destOffset] = mapper.invoke(vals[i + offset]);
    }
  }

  public static Object[] merge(final Object... params) {
    int size = 0;
    for (final Object param : params) {
      if (param instanceof Object[]) {
        size += ((Object[]) param).length;
      }
      else if (param instanceof Collection) {
        size += ((Collection<?>) param).size();
      }
      else if (param instanceof Vector) {
        size += ((Vector<?>) param).size();
      }
      else {
        size++;
      }
    }
    final Object[] result = new Object[size];
    int index = 0;
    for (final Object param : params) {
      if (param instanceof Object[]) {
        final Object[] tmp = (Object[]) param;
        System.arraycopy(tmp, 0, result, index, tmp.length);
        index += tmp.length;
      }
      else if (param instanceof Collection) {
        final Collection<?> tmp = (Collection<?>) param;
        for (final Object v : tmp) {
          result[index++] = v;
        }
      }
      else if (param instanceof Vector) {
        final Vector<?> tmp = (Vector<?>) param;
        for (final Object v : tmp) {
          result[index++] = v;
        }
      }
      else {
        result[index++] = param;
      }
    }
    return result;
  }

  private static int nextCapacity(final int current, final int need, final int max) {
    int result = current;
    while (result < need) {
      result = Math.min(result * 2, max);
      result = result - result % 16 + 16;
    }
    return result;
  }

  public static void reverse(final Object[] data, final int limit) {
    int left = 0;
    int right = limit - 1;
    Object temp = null;
    while (left < right) {
      temp = data[left];
      data[left] = data[right];
      data[right] = temp;
      left++;
      right--;
    }
  }

  public static void reverse(final Object[] data, final Object[] result, final int limit) {
    for (int i = 0; i < limit; i++) {
      result[limit - 1 - i] = data[i];
    }
  }

  public static <V> void sort(
    final @Mutable V[] vals,
    final int limit,
    final int offset,
    final Comparator<V> comparator) {
    java.util.Arrays.sort(vals, offset, offset + limit, comparator);
  }

  public static <T> Spliterator<T> spliterator(final T[] data) {
    return spliterator(data, data.length, 0);
  }

  public static <T> Spliterator<T> spliterator(final T[] data, final int limit, final int offset) {
    return Spliterators.spliterator(data, offset, offset + limit, Spliterator.ORDERED | Spliterator.IMMUTABLE);
  }

  public static String toString(@Immutable final Object[] vals, final int limit, final int offset) {
    if (vals == null) {
      return "null";
    }
    else if (vals.length == 0) {
      return "[]";
    }
    final StringBuilder builder = new StringBuilder(128);
    appendArray(builder, vals, limit, offset);
    return builder.toString();
  }

}
