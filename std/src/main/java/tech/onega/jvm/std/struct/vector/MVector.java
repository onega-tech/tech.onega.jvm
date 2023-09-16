package tech.onega.jvm.std.struct.vector;

import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Stream;
import tech.onega.jvm.std.annotation.Mutable;
import tech.onega.jvm.std.annotation.Self;

@Mutable
public interface MVector<V> extends Vector<V> {

  @Mutable
  @Self
  MVector<V> add(V value);

  @Mutable
  @Self
  MVector<V> addAll(Iterable<? extends V> iterable);

  @Mutable
  @Self
  MVector<V> addAll(Iterable<? extends V> iterable, int limit);

  @Mutable
  @Self
  MVector<V> addAll(Iterable<? extends V> iterable, int limit, int offset);

  @Mutable
  @Self
  MVector<V> addAll(Stream<? extends V> stream);

  @SuppressWarnings("unchecked")
  @Mutable
  @Self
  MVector<V> addAll(V... values);

  @Mutable
  @Self
  MVector<V> addAll(V[] values, final int limit);

  @Mutable
  @Self
  MVector<V> addAll(V[] values, int limit, int offset);

  int capacity();

  @Mutable
  @Self
  MVector<V> capacity(int newCapacity);

  @Mutable
  @Self
  MVector<V> clear();

  boolean isFull();

  int maxCapacity();

  @Mutable
  @Self
  MVector<V> remove(V value);

  @Mutable
  @Self
  MVector<V> removeAll(Iterable<? extends V> values);

  @Mutable
  @Self
  MVector<V> size(int newSize);

  @Mutable
  @Self
  MVector<V> sort(Comparator<V> comparator);

  V[] toArray(Function<Integer, V[]> arrayFactory);

  @Mutable
  @Self
  MVector<V> trim();

}
