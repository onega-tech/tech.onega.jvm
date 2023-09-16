package tech.onega.jvm.std.struct.vector;

import java.io.Serializable;
import java.util.Collection;
import java.util.Spliterator;
import java.util.stream.Stream;
import tech.onega.jvm.std.annotation.Copy;
import tech.onega.jvm.std.annotation.Self;
import tech.onega.jvm.std.struct.list.IList;

public interface Vector<V> extends Iterable<V>, Serializable {

  @Self
  Collection<V> asCollection();

  boolean contains(V value);

  V first();

  boolean isEmpty();

  default boolean notContains(final V value) {
    return !contains(value);
  }

  default boolean isNotEmpty() {
    return size() > 0;
  }

  int size();

  @Override
  Spliterator<V> spliterator();

  Stream<V> stream();

  Object[] toArray();

  @Copy
  IList<V> toIList();

}
