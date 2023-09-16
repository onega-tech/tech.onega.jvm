package tech.onega.jvm.std.struct.cache;

import tech.onega.jvm.std.annotation.Lazy;
import tech.onega.jvm.std.annotation.NotThreadSafe;
import tech.onega.jvm.std.lang.Lambda;
import tech.onega.jvm.std.struct.map.MMap;

@NotThreadSafe
final public class CacheBasic<K, V, E extends Throwable> implements Cache<K, V, E> {

  private final MMap<K, V> map;

  private final int capacity;

  private final Lambda.Function<K, V, E> factory;

  public CacheBasic(int initialSize, int capacity, Lambda.Function<K, V, E> factory) {
    this.map = MMap.create(initialSize, capacity);
    this.capacity = capacity;
    this.factory = factory;
  }

  @Override
  public int capacity() {
    return capacity;
  }

  @Override
  public void clear() {
    map.clear();
  }

  @Override
  public boolean contains(K key) {
    return map.containsKey(key);
  }

  @Lazy
  @Override
  public V get(K key) throws E {
    V value = map.get(key);
    if (value == null) {
      value = factory.invoke(key);
      if (map.size() == capacity) {
        map.removeKey(map.keys().first());
      }
      map.add(key, value);
    }
    return value;
  }

  @Override
  public int size() {
    return map.size();
  }

}
