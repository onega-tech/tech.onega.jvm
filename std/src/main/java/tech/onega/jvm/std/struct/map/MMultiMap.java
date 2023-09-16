package tech.onega.jvm.std.struct.map;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import tech.onega.jvm.std.annotation.Copy;
import tech.onega.jvm.std.annotation.Immutable;
import tech.onega.jvm.std.annotation.Mutable;
import tech.onega.jvm.std.struct.hash.KV;
import tech.onega.jvm.std.struct.hash.table.HashTable;
import tech.onega.jvm.std.struct.iterable.IterableUtils;
import tech.onega.jvm.std.struct.iterator.IteratorUtils;
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.struct.list.MList;
import tech.onega.jvm.std.struct.stream.StreamUtils;
import tech.onega.jvm.std.struct.vector.MVector;
import tech.onega.jvm.std.struct.vector.VectorUtils;

@Mutable
final public class MMultiMap<K, V> implements MVector<KV<K, V>> {

  private static final long serialVersionUID = 1L;

  public static <K, V> Collector<KV<K, V>, ?, MMultiMap<K, V>> collector() {
    return collector(VectorUtils.DEFAULT_INITIAL_CAPACITY);
  }

  public static <K, V> Collector<KV<K, V>, ?, MMultiMap<K, V>> collector(final int initialSize) {
    return StreamUtils.selfCollector(() -> MMultiMap.<K, V>create(initialSize, VectorUtils.DEFAULT_MAX_CAPACITY),
      MMultiMap::add);
  }

  public static <K, V> MMultiMap<K, V> create() {
    return new MMultiMap<>(VectorUtils.DEFAULT_INITIAL_CAPACITY, VectorUtils.DEFAULT_MAX_CAPACITY);
  }

  public static <K, V> MMultiMap<K, V> create(final int initialCapacity) {
    return new MMultiMap<>(initialCapacity, VectorUtils.DEFAULT_MAX_CAPACITY);
  }

  public static <K, V> MMultiMap<K, V> create(final int initialCapacity, final int maxCapacity) {
    return new MMultiMap<>(initialCapacity, maxCapacity);
  }

  public static <K, V> MMultiMap<K, V> of(@Immutable @Copy final Iterable<? extends KV<K, V>> iterable) {
    final int size = IterableUtils.trySizeOf(iterable);
    return new MMultiMap<K, V>(Math.max(size, VectorUtils.DEFAULT_INITIAL_CAPACITY), VectorUtils.DEFAULT_MAX_CAPACITY)
      .addAll(iterable);
  }

  public static <K, V> MMultiMap<K, V> of(final java.util.Map<K, V> map) {
    return new MMultiMap<K, V>(map.size(), VectorUtils.DEFAULT_MAX_CAPACITY)
      .addStreamEntry(map.entrySet().stream());
  }

  @SafeVarargs
  public static <K, V> MMultiMap<K, V> of(final KV<K, V>... keyValues) {
    return of(keyValues, keyValues.length, 0);
  }

  public static <K, V> MMultiMap<K, V> of(@Immutable @Copy final KV<K, V>[] keyValues, final int limit,
    final int offset) {
    return new MMultiMap<K, V>(limit, VectorUtils.DEFAULT_MAX_CAPACITY).addAll(keyValues, limit, offset);
  }

  public static <K, V> MMultiMap<K, V> of(@Immutable @Copy final Stream<? extends KV<K, V>> stream) {
    return new MMultiMap<K, V>(VectorUtils.DEFAULT_INITIAL_CAPACITY, VectorUtils.DEFAULT_MAX_CAPACITY).addAll(stream);
  }

  private HashTable<K, MList<V>> data;

  private int capacity;

  private int hashCode = 0;

  private boolean hashCodeReseted = true;

  private final int maxCapacity;

  private int size = 0;

  private MMultiMap(final int initialCapacity, final int maxCapacity) {
    this.maxCapacity = maxCapacity;
    this.capacity = initialCapacity;
    data = HashTable.lifo(initialCapacity, maxCapacity);
  }

  public MMultiMap<K, V> add(final K key, final V value) {
    checkCanAdd(1);
    addUnsafe(key, value);
    hashCodeReseted = true;
    return this;
  }

  @Override
  public MMultiMap<K, V> add(final KV<K, V> keyValue) {
    return add(keyValue.key(), keyValue.value());
  }

  @Override
  public MMultiMap<K, V> addAll(final Iterable<? extends KV<K, V>> iterable) {
    return addIterator(iterable.iterator());
  }

  @Override
  public MMultiMap<K, V> addAll(final Iterable<? extends KV<K, V>> iterable, final int limit) {
    return addIterator(IteratorUtils.limit(iterable.iterator(), limit));
  }

  @Override
  public MMultiMap<K, V> addAll(final Iterable<? extends KV<K, V>> iterable, final int limit, final int offset) {
    return addIterator(IteratorUtils.limitOffset(iterable.iterator(), limit, offset));
  }

  @SuppressWarnings("unchecked")
  @Override
  public MMultiMap<K, V> addAll(final KV<K, V>... keyValues) {
    return addAll(keyValues, keyValues.length, 0);
  }

  @Override
  public MMultiMap<K, V> addAll(final KV<K, V>[] keyValues, final int limit) {
    return addAll(keyValues, limit, 0);
  }

  @Override
  public MMultiMap<K, V> addAll(final KV<K, V>[] keyValues, final int limit, final int offset) {
    if (keyValues != null && keyValues.length > 0) {
      checkCanAdd(limit);
      for (int i = offset; i < limit + offset; i++) {
        addUnsafe(keyValues[i].key(), keyValues[i].value());
      }
      hashCodeReseted = true;
    }
    return this;
  }

  @Override
  public MMultiMap<K, V> addAll(final Stream<? extends KV<K, V>> stream) {
    return addIterator(stream.iterator());
  }

  public MMultiMap<K, V> addIterator(final Iterator<? extends KV<K, V>> iterator) {
    while (iterator.hasNext()) {
      checkCanAdd(1);
      final var kv = iterator.next();
      addUnsafe(kv.key(), kv.value());
    }
    hashCodeReseted = true;
    return this;
  }

  public MMultiMap<K, V> addStreamEntry(final Stream<? extends Entry<K, V>> stream) {
    final Iterator<? extends Map.Entry<K, V>> iterator = stream.iterator();
    while (iterator.hasNext()) {
      checkCanAdd(1);
      final Map.Entry<K, V> kv = iterator.next();
      addUnsafe(kv.getKey(), kv.getValue());
    }
    hashCodeReseted = true;
    return this;
  }

  private void addUnsafe(final K key, final V value) {
    final var kv = data.get(key);
    if (kv == null) {
      data.replace(KV.of(key, MList.of(value)));
    }
    else {
      kv.value.add(value);
    }
    size++;
  }

  @Override
  public Collection<KV<K, V>> asCollection() {
    return VectorUtils.wrapToCollection(this);
  }

  @Override
  public int capacity() {
    return Math.max(capacity, data.size());
  }

  @Override
  public MMultiMap<K, V> capacity(final int newCapacity) {
    if (capacity() != newCapacity) {
      if (newCapacity < 0 || newCapacity > maxCapacity()) {
        throw new OutOfMemoryError(String.format("Can't set capacity %s. newCapacity:%s > maxCapacity:%s",
          this.getClass(), newCapacity, maxCapacity()));
      }
      if (newCapacity < data.size()) {
        final MMultiMap<K, V> resized = stream().limit(newCapacity).collect(collector(keysSize()));
        this.data = resized.data;
        this.size = resized.size();
        this.hashCode = resized.hashCode();
        hashCodeReseted = false;
      }
      capacity = newCapacity;
    }
    return this;
  }

  private void checkCanAdd(final int count) {
    if (size() + count > maxCapacity) {
      throw new IllegalStateException(
        String.format("Can't add %s. Cause size:%s, maxCapacity:%s", count, size(), maxCapacity));
    }
  }

  @Override
  public MMultiMap<K, V> clear() {
    data.clear();
    size = 0;
    hashCodeReseted = true;
    return this;
  }

  @Override
  public boolean contains(final KV<K, V> kv) {
    final KV<K, MList<V>> e = data.get(kv.key());
    if (e == null) {
      return false;
    }
    return e.value.contains(kv.value);
  }

  public boolean containsKey(final K key) {
    return data.contains(key);
  }

  public boolean containsValue(final V value) {
    return values().contains(value);
  }

  public IMultiMap<K, V> destroy() {
    final IMultiMap<K, V> result = IMultiMap.of(size, data.toImmutable(MList::destroy));
    this.data = null;
    this.size = 0;
    this.hashCodeReseted = false;
    this.hashCode = 0;
    this.capacity = 0;
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    return VectorUtils.equals(this, obj);
  }

  @Override
  public KV<K, V> first() {
    if (data.isEmpty()) {
      return null;
    }
    final KV<K, MList<V>> kv = data.iterator().next();
    return kv.value.isEmpty() ? null : KV.of(kv.key, kv.value.first());
  }

  public V first(final K key) {
    final KV<K, MList<V>> kv = data.get(key);
    return kv == null ? null : kv.value.first();
  }

  public MList<V> get(final K key) {
    return get(key, null);
  }

  public MList<V> get(final K key, final MList<V> defaultValue) {
    final KV<K, MList<V>> kv = data.get(key);
    return kv == null ? defaultValue : kv.value;
  }

  @Override
  public int hashCode() {
    if (isEmpty()) {
      return 0;
    }
    else if (hashCodeReseted) {
      hashCode = data.hashCode();
      hashCodeReseted = false;
    }
    return hashCode;
  }

  @Override
  public boolean isEmpty() {
    return data.isEmpty();
  }

  @Override
  public boolean isFull() {
    return size() == maxCapacity();
  }

  @Override
  public Iterator<KV<K, V>> iterator() {
    final Iterator<KV<K, MList<V>>> nodeIterator = data.iterator();
    return new Iterator<>() {

      private Iterator<V> valueIterator = null;

      private K key;

      @Override
      public boolean hasNext() {
        return nodeIterator.hasNext() || (valueIterator != null && valueIterator.hasNext());
      }

      @Override
      public KV<K, V> next() {
        if (valueIterator == null || !valueIterator.hasNext()) {
          final var kv = nodeIterator.next();
          key = kv.key();
          valueIterator = kv.value().iterator();
        }
        return KV.of(key, valueIterator.next());
      }

    };
  }

  @Copy
  public IList<KV<K, IList<V>>> keyMultiValues() {
    final MList<KV<K, IList<V>>> mList = MList.create(keysSize());
    for (final KV<K, MList<V>> kv : data) {
      mList.add(KV.of(kv.key, kv.value.toIList()));
    }
    return mList.toIList();
  }

  public IList<K> keys() {
    return data.stream().map(KV::key).collect(IList.collector(keysSize()));
  }

  public int keysSize() {
    return data.size();
  }

  public IList<KV<K, V>> keyValues() {
    return stream().collect(IList.collector(size()));
  }

  @Override
  public int maxCapacity() {
    return maxCapacity;
  }

  public MMultiMap<K, V> remove(final K key, final V value) {
    return remove(KV.of(key, value));
  }

  @Override
  public MMultiMap<K, V> remove(final KV<K, V> keyValue) {
    final KV<K, MList<V>> kv = data.get(keyValue.key());
    if (kv != null) {
      final MList<V> items = kv.value;
      if (items != null) {
        final int sizeBefore = items.size();
        items.remove(keyValue.value());
        if (sizeBefore > items.size()) {
          size--;
          hashCodeReseted = true;
        }
        if (items.isEmpty()) {
          data.remove(keyValue.key());
        }
      }
    }
    return this;
  }

  @Override
  public MMultiMap<K, V> removeAll(final Iterable<? extends KV<K, V>> values) {
    for (final KV<K, V> value : values) {
      remove(value);
    }
    return this;
  }

  public MMultiMap<K, V> removeKey(final K key) {
    final KV<K, MList<V>> kv = data.get(key);
    if (kv != null) {
      final MList<V> items = kv.value();
      if (items != null) {
        data.remove(key);
        size = size - items.size();
        hashCodeReseted = true;
      }
    }
    return this;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public MMultiMap<K, V> size(final int newSize) {
    if (newSize < data.size()) {
      final MMultiMap<K, V> resized = stream().limit(newSize).collect(collector(keysSize()));
      this.data = resized.data;
      this.size = resized.size();
      this.hashCode = resized.hashCode();
      hashCodeReseted = false;
    }
    return this;
  }

  @Override
  public MMultiMap<K, V> sort(final Comparator<KV<K, V>> comparator) {
    final MMultiMap<K, V> sorted = stream().sorted(comparator).collect(collector(keysSize()));
    this.data = sorted.data;
    this.hashCode = sorted.hashCode();
    hashCodeReseted = false;
    return this;
  }

  @Override
  public Spliterator<KV<K, V>> spliterator() {
    return Spliterators.spliterator(iterator(), size(), Spliterator.ORDERED | Spliterator.SIZED);
  }

  public Stream<KV<K, MList<V>>> steamKeyMultiValues() {
    return data.stream();
  }

  @Override
  public Stream<KV<K, V>> stream() {
    return StreamSupport.stream(spliterator(), false);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Object[] toArray() {
    return IterableUtils.toArray((Iterable<Object>) (Iterable<?>) this, size(), 0, Object.class);
  }

  @Override
  public KV<K, V>[] toArray(Function<Integer, KV<K, V>[]> arrayFactory) {
    var result = arrayFactory.apply(this.size());
    var i = 0;
    for (var v : this) {
      result[i++] = v;
    }
    return result;
  }

  @Override
  public IList<KV<K, V>> toIList() {
    return stream().collect(IList.collector(size()));
  }

  public IMultiMap<K, V> toIMultiMap() {
    return IMultiMap.of(size, data.toImmutable(MList::toIList));
  }

  @Override
  public String toString() {
    return data.toString();
  }

  @Override
  public MMultiMap<K, V> trim() {
    this.capacity = data.size();
    data.trim();
    return this;
  }

  public IList<V> values() {
    return stream().map(KV::value).collect(IList.collector(size()));
  }

}
