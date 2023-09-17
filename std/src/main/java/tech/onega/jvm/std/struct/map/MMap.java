package tech.onega.jvm.std.struct.map;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import tech.onega.jvm.std.annotation.Copy;
import tech.onega.jvm.std.annotation.Immutable;
import tech.onega.jvm.std.annotation.Mutable;
import tech.onega.jvm.std.annotation.Self;
import tech.onega.jvm.std.lang.Equals;
import tech.onega.jvm.std.lang.Lambda;
import tech.onega.jvm.std.struct.hash.KV;
import tech.onega.jvm.std.struct.hash.table.HashTable;
import tech.onega.jvm.std.struct.iterable.IterableUtils;
import tech.onega.jvm.std.struct.iterator.IteratorUtils;
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.struct.list.MList;
import tech.onega.jvm.std.struct.set.ISet;
import tech.onega.jvm.std.struct.stream.StreamUtils;
import tech.onega.jvm.std.struct.vector.MVector;
import tech.onega.jvm.std.struct.vector.VectorUtils;

@Mutable
final public class MMap<K, V> implements MVector<KV<K, V>>, Cloneable {

  private static final long serialVersionUID = 1L;

  public static <K, V> Collector<KV<K, V>, ?, MMap<K, V>> collector() {
    return collector(VectorUtils.DEFAULT_INITIAL_CAPACITY);
  }

  public static <K, V> Collector<KV<K, V>, ?, MMap<K, V>> collector(final int initialSize) {
    return StreamUtils.selfCollector(() -> MMap.<K, V>create(initialSize, VectorUtils.DEFAULT_MAX_CAPACITY), MMap::add);
  }

  public static <K, V> MMap<K, V> copy(final Iterable<? extends KV<K, V>> iterable) {
    return new MMap<K, V>(VectorUtils.DEFAULT_INITIAL_CAPACITY, VectorUtils.DEFAULT_MAX_CAPACITY).addAll(iterable);
  }

  public static <K, V> MMap<K, V> copy(final java.util.Map<K, V> map) {
    return new MMap<K, V>(map.size(), VectorUtils.DEFAULT_MAX_CAPACITY)
      .addStreamEntry(map.entrySet().stream());
  }

  public static <K, V> MMap<K, V> create() {
    return new MMap<>(VectorUtils.DEFAULT_INITIAL_CAPACITY, VectorUtils.DEFAULT_MAX_CAPACITY);
  }

  public static <K, V> MMap<K, V> create(final int initialCapacity) {
    return new MMap<>(initialCapacity, VectorUtils.DEFAULT_MAX_CAPACITY);
  }

  public static <K, V> MMap<K, V> create(final int initialCapacity, final int maxCapacity) {
    return new MMap<>(initialCapacity, maxCapacity);
  }

  public static <K, V> MMap<K, V> empty() {
    return new MMap<>(0, VectorUtils.DEFAULT_MAX_CAPACITY);
  }

  @SafeVarargs
  public static <K, V> MMap<K, V> of(final KV<K, V>... keyValues) {
    return new MMap<K, V>(keyValues.length, VectorUtils.DEFAULT_MAX_CAPACITY).addAll(keyValues);
  }

  public static <K, V> MMap<K, V> of(@Immutable @Copy final KV<K, V>[] keyValues, final int limit, final int offset) {
    return new MMap<K, V>(limit, VectorUtils.DEFAULT_MAX_CAPACITY).addAll(keyValues, limit, offset);
  }

  public static <K, V> MMap<K, V> of(final Stream<? extends KV<K, V>> stream) {
    return new MMap<K, V>(VectorUtils.DEFAULT_INITIAL_CAPACITY, VectorUtils.DEFAULT_MAX_CAPACITY).addAll(stream);
  }

  private HashTable<K, V> data;

  private int capacity;

  private int hashCode = 0;

  private boolean hashCodeReseted = true;

  private final int maxCapacity;

  private MMap(final int initialCapacity, final int maxCapacity) {
    this.maxCapacity = maxCapacity;
    this.capacity = initialCapacity;
    this.data = HashTable.lifo(initialCapacity, maxCapacity);
  }

  @Self
  public MMap<K, V> add(final K key, final V value) {
    return this.add(KV.of(key, value));
  }

  @Override
  @Self
  public MMap<K, V> add(final KV<K, V> keyValue) {
    if (keyValue != null) {
      this.checkCanAdd(1);
      this.data.replace(keyValue);
      this.hashCodeReseted = true;
    }
    return this;
  }

  @Override
  public MMap<K, V> addAll(final Iterable<? extends KV<K, V>> iterable) {
    return this.addIterator(iterable.iterator());
  }

  @Override
  public MMap<K, V> addAll(final Iterable<? extends KV<K, V>> iterable, final int limit) {
    return this.addIterator(IteratorUtils.limit(iterable.iterator(), limit));
  }

  @Override
  public MMap<K, V> addAll(final Iterable<? extends KV<K, V>> iterable, final int limit, final int offset) {
    return this.addIterator(IteratorUtils.limitOffset(iterable.iterator(), limit, offset));
  }

  @SuppressWarnings("unchecked")
  @Override
  @Self
  public MMap<K, V> addAll(final KV<K, V>... keyValues) {
    return this.addAll(keyValues, keyValues.length, 0);
  }

  @Override
  @Self
  public MMap<K, V> addAll(final KV<K, V>[] keyValues, final int limit) {
    return this.addAll(keyValues, limit, 0);
  }

  @Override
  @Self
  public MMap<K, V> addAll(final KV<K, V>[] keyValues, final int limit, final int offset) {
    if (keyValues != null && keyValues.length > 0) {
      this.checkCanAdd(limit);
      for (int i = offset; i < limit + offset; i++) {
        this.data.replace(keyValues[i]);
      }
      this.hashCodeReseted = true;
    }
    return this;
  }

  @Override
  public MMap<K, V> addAll(final Stream<? extends KV<K, V>> stream) {
    return this.addIterator(stream.iterator());
  }

  @Self
  public MMap<K, V> addIterator(final Iterator<? extends KV<K, V>> iterator) {
    while (iterator.hasNext()) {
      this.checkCanAdd(1);
      this.data.replace(iterator.next());
    }
    this.hashCodeReseted = true;
    return this;
  }

  @Self
  public MMap<K, V> addStreamEntry(final Stream<? extends Map.Entry<K, V>> stream) {
    final Iterator<? extends Map.Entry<K, V>> iterator = stream.iterator();
    while (iterator.hasNext()) {
      this.checkCanAdd(1);
      this.data.replace(KV.of(iterator.next()));
    }
    this.hashCodeReseted = true;
    return this;
  }

  @Override
  @Copy
  public Collection<KV<K, V>> asCollection() {
    return VectorUtils.wrapToCollection(this);
  }

  @Override
  public int capacity() {
    return Math.max(this.capacity, this.data.size());
  }

  @Override
  @Self
  public MMap<K, V> capacity(final int newCapacity) {
    if (this.capacity() != newCapacity) {
      if (newCapacity < 0 || newCapacity > this.maxCapacity()) {
        throw new OutOfMemoryError(String.format("Can't set capacity %s. newCapacity:%s > maxCapacity:%s",
          this.getClass(), newCapacity, this.maxCapacity()));
      }
      if (newCapacity < this.data.size()) {
        this.data.size(newCapacity);
        this.hashCodeReseted = true;
      }
      this.capacity = newCapacity;
    }
    return this;
  }

  private void checkCanAdd(final int count) {
    if (this.size() + count > this.maxCapacity) {
      throw new IllegalStateException(
        String.format("Can't add %s. Cause size:%s, maxCapacity:%s", count, this.size(), this.maxCapacity));
    }
  }

  @Override
  @Self
  public MMap<K, V> clear() {
    this.data.clear();
    this.hashCodeReseted = true;
    return this;
  }

  @Override
  public Object clone() {
    return this.cloneMap();
  }

  public MMap<K, V> cloneMap() {
    return new MMap<K, V>(this.size(), this.maxCapacity).addAll(this);
  }

  @Override
  public boolean contains(final KV<K, V> kv) {
    final KV<K, V> e = this.data.get(kv.key());
    return e == null ? false : Equals.yes(e.value(), kv.value());
  }

  public boolean containsKey(final K key) {
    return this.data.contains(key);
  }

  public boolean containsValue(final V value) {
    return this.data.stream().filter(n -> Equals.yes(n.value(), value)).findAny().isPresent();
  }

  public IMap<K, V> destroy() {
    final IMap<K, V> result = IMap.wrap(this.data.destroy());
    this.data = null;
    this.hashCode = 0;
    this.hashCodeReseted = false;
    this.capacity = 0;
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    return VectorUtils.equals(this, obj);
  }

  @Override
  public KV<K, V> first() {
    return this.data.first();
  }

  public V get(final K key) {
    return this.get(key, null);
  }

  public V get(final K key, final V defaultValue) {
    final KV<K, V> kv = this.data.get(key);
    return kv == null ? defaultValue : kv.value();
  }

  public <E extends Throwable> V getOrSet(final K key, final Lambda.Supplier<V, E> valueFactory) throws E {
    final KV<K, V> kv = this.data.get(key);
    if (kv == null) {
      final V val = valueFactory.invoke();
      this.add(key, val);
      return val;
    }
    return kv.value();
  }

  @Override
  public int hashCode() {
    if (this.isEmpty()) {
      return 0;
    }
    else if (this.hashCodeReseted) {
      this.hashCode = this.data.hashCode();
      this.hashCodeReseted = false;
    }
    return this.hashCode;
  }

  @Override
  public boolean isEmpty() {
    return this.data.isEmpty();
  }

  @Override
  public boolean isFull() {
    return this.size() == this.maxCapacity();
  }

  @Override
  public Iterator<KV<K, V>> iterator() {
    return this.data.iterator();
  }

  @Copy
  public IList<K> keys() {
    return this.data
      .stream()
      .map(KV::key)
      .collect(IList.collector(this.size()));
  }

  @Copy
  public ISet<K> keySet() {
    return this.data
      .stream()
      .map(KV::key)
      .collect(ISet.collector(this.size()));
  }

  @Copy
  public IList<KV<K, V>> keyValues() {
    return this.data
      .stream()
      .collect(IList.collector(this.size()));
  }

  @Override
  public int maxCapacity() {
    return this.maxCapacity;
  }

  @Override
  @Self
  public MMap<K, V> remove(final KV<K, V> keyValue) {
    return this.removeKey(keyValue.key());
  }

  @Self
  public <E extends Throwable> MMap<K, V> remove(final Lambda.Function<KV<K, V>, Boolean, E> filter) throws E {
    final MList<K> forRemove = MList.create();
    for (final KV<K, V> kv : this) {
      if (!filter.invoke(kv)) {
        forRemove.add(kv.key());
      }
    }
    for (final K k : forRemove) {
      this.data.remove(k);
    }
    return this;
  }

  @Override
  public MMap<K, V> removeAll(final Iterable<? extends KV<K, V>> kvs) {
    for (final KV<K, V> kv : kvs) {
      this.removeKey(kv.key());
    }
    return this;
  }

  @Self
  public MMap<K, V> removeKey(final K key) {
    this.data.remove(key);
    this.hashCodeReseted = true;
    return this;
  }

  @Override
  public int size() {
    return this.data.size();
  }

  @Override
  @Self
  public MMap<K, V> size(final int newSize) {
    this.data.size(newSize);
    this.hashCodeReseted = true;
    return this;
  }

  @Override
  @Self
  public MMap<K, V> sort(final Comparator<KV<K, V>> comparator) {
    this.data = this.data
      .stream()
      .sorted(comparator)
      .collect(HashTable.collector(HashTable.lifo(this.size(), this.maxCapacity)));
    return this;
  }

  @Override
  public Spliterator<KV<K, V>> spliterator() {
    return Spliterators.spliterator(this.iterator(), this.size(), Spliterator.ORDERED | Spliterator.SIZED);
  }

  @Override
  @Copy
  public Stream<KV<K, V>> stream() {
    return StreamSupport.stream(this.spliterator(), false);
  }

  @Override
  @SuppressWarnings("unchecked")
  @Copy
  public Object[] toArray() {
    return IterableUtils.toArray((Iterable<Object>) (Iterable<?>) this, this.size(), 0, Object.class);
  }

  @Override
  public KV<K, V>[] toArray(final Function<Integer, KV<K, V>[]> arrayFactory) {
    final var result = arrayFactory.apply(this.size());
    var i = 0;
    for (final var v : this) {
      result[i++] = v;
    }
    return result;
  }

  @Override
  @Copy
  public IList<KV<K, V>> toIList() {
    return this.isEmpty() ? IList.empty() : this.stream().collect(IList.collector(this.size()));
  }

  @Copy
  public IMap<K, V> toIMap() {
    return IMap.wrap(this.data.toImmutable());
  }

  @Copy
  public Map<K, V> toMap() {
    return this.data.stream()
      .collect(StreamUtils.selfCollector(
        () -> new LinkedHashMap<>(this.size()),
        (a, v) -> a.put(v.key(), v.value())));
  }

  @Override
  public String toString() {
    return IterableUtils.toString(this);
  }

  @Override
  @Self
  public MMap<K, V> trim() {
    this.capacity = this.data.size();
    this.data.trim();
    return this;
  }

  @Copy
  public IList<V> values() {
    return this.isEmpty() ? IList.empty()
      : this.stream()
        .map(KV::value)
        .collect(IList.collector(this.size()));
  }

}
