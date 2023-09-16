package tech.onega.jvm.std.struct.cache;

import java.util.concurrent.atomic.AtomicReference;
import tech.onega.jvm.std.annotation.Lazy;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.lang.Lambda;
import tech.onega.jvm.std.struct.map.MMap;

@ThreadSafe
final public class CacheCopyOnWrite<K, V, E extends Throwable> implements Cache<K, V, E> {

  private final AtomicReference<MMap<K, V>> mapRef;

  private final int capacity;

  private final Lambda.Function<K, V, E> factory;

  private final int initialCapacity;

  public CacheCopyOnWrite(int initialCapacity, int capacity, Lambda.Function<K, V, E> factory) {
    this.mapRef = new AtomicReference<>(MMap.create(initialCapacity));
    this.capacity = capacity;
    this.factory = factory;
    this.initialCapacity = initialCapacity;
  }

  @Override
  public int capacity() {
    return capacity;
  }

  @Override
  public void clear() {
    mapRef.set(MMap.create(initialCapacity));
  }

  @Override
  public boolean contains(K key) {
    return mapRef.get().containsKey(key);
  }

  @Lazy
  @Override
  public V get(K key) throws E {
    var map = mapRef.get();
    V value = map.get(key);
    if (value != null) {
      return value;
    }
    value = factory.invoke(key);
    map = map.cloneMap();
    if (map.size() == capacity) {
      map.removeKey(map.keys().first());
    }
    map.add(key, value);
    mapRef.set(map);
    return value;
  }

  @Override
  public int size() {
    return mapRef.get().size();
  }

}
