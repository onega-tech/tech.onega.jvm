package tech.onega.jvm.std.struct.set;

import java.util.Iterator;
import java.util.WeakHashMap;
import tech.onega.jvm.std.annotation.NotThreadSafe;
import tech.onega.jvm.std.struct.vector.VectorUtils;

@NotThreadSafe
final public class WeakSet<V> implements Iterable<V> {

  private static final Object VALUE = new Object();

  private final WeakHashMap<V, Object> data;

  public WeakSet() {
    this(VectorUtils.DEFAULT_INITIAL_CAPACITY);
  }

  public WeakSet(final int initialCapacity) {
    this.data = new WeakHashMap<>(initialCapacity);
  }

  public void add(final V value) {
    data.put(value, VALUE);
  }

  public void clear() {
    data.clear();
  }

  public boolean contains(final V value) {
    return data.containsKey(value);
  }

  public boolean isEmpty() {
    return data.isEmpty();
  }

  public boolean isNotEmpty() {
    return !data.isEmpty();
  }

  @Override
  public Iterator<V> iterator() {
    return data.keySet().iterator();
  }

  public void remove(final V value) {
    data.remove(value);
  }

  public int size() {
    return data.size();
  }

}
