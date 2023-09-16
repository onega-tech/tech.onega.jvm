package tech.onega.jvm.std.struct.cache;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import tech.onega.jvm.std.annotation.Lazy;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.lang.Lambda;
import tech.onega.jvm.std.struct.map.MMap;

@ThreadSafe
final public class CacheConcurrent<K, V, E extends Throwable> implements Cache<K, V, E> {

  private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

  private final MMap<K, V> map;

  private final int capacity;

  private final Lambda.Function<K, V, E> factory;

  public CacheConcurrent(int initialSize, int capacity, Lambda.Function<K, V, E> factory) {
    this.map = MMap.create(initialSize);
    this.capacity = capacity;
    this.factory = factory;
  }

  @Override
  public int capacity() {
    return capacity;
  }

  @Override
  public void clear() {
    try {
      rwLock.writeLock().lock();
      map.clear();
    }
    finally {
      rwLock.writeLock().unlock();
    }
  }

  @Override
  public boolean contains(final K key) {
    try {
      rwLock.readLock().lock();
      return map.containsKey(key);
    }
    finally {
      rwLock.readLock().unlock();
    }
  }

  @Lazy
  @Override
  public V get(final K key) throws E {
    try {
      rwLock.readLock().lock();
      final V value = map.get(key);
      if (value != null) {
        return value;
      }
    }
    finally {
      rwLock.readLock().unlock();
    }
    try {
      rwLock.writeLock().lock();
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
    finally {
      rwLock.writeLock().unlock();
    }
  }

  @Override
  public int size() {
    try {
      rwLock.readLock().lock();
      return map.size();
    }
    finally {
      rwLock.readLock().unlock();
    }
  }

}
