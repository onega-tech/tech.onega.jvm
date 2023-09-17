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
    this.data = HashTable.lifo(initialCapacity, maxCapacity);
  }

  public MMultiMap<K, V> add(final K key, final V value) {
    this.checkCanAdd(1);
    this.addUnsafe(key, value);
    this.hashCodeReseted = true;
    return this;
  }

  @Override
  public MMultiMap<K, V> add(final KV<K, V> keyValue) {
    return this.add(keyValue.key(), keyValue.value());
  }

  @Override
  public MMultiMap<K, V> addAll(final Iterable<? extends KV<K, V>> iterable) {
    return this.addIterator(iterable.iterator());
  }

  @Override
  public MMultiMap<K, V> addAll(final Iterable<? extends KV<K, V>> iterable, final int limit) {
    return this.addIterator(IteratorUtils.limit(iterable.iterator(), limit));
  }

  @Override
  public MMultiMap<K, V> addAll(final Iterable<? extends KV<K, V>> iterable, final int limit, final int offset) {
    return this.addIterator(IteratorUtils.limitOffset(iterable.iterator(), limit, offset));
  }

  @SuppressWarnings("unchecked")
  @Override
  public MMultiMap<K, V> addAll(final KV<K, V>... keyValues) {
    return this.addAll(keyValues, keyValues.length, 0);
  }

  @Override
  public MMultiMap<K, V> addAll(final KV<K, V>[] keyValues, final int limit) {
    return this.addAll(keyValues, limit, 0);
  }

  @Override
  public MMultiMap<K, V> addAll(final KV<K, V>[] keyValues, final int limit, final int offset) {
    if (keyValues != null && keyValues.length > 0) {
      this.checkCanAdd(limit);
      for (int i = offset; i < limit + offset; i++) {
        this.addUnsafe(keyValues[i].key(), keyValues[i].value());
      }
      this.hashCodeReseted = true;
    }
    return this;
  }

  @Override
  public MMultiMap<K, V> addAll(final Stream<? extends KV<K, V>> stream) {
    return this.addIterator(stream.iterator());
  }

  public MMultiMap<K, V> addIterator(final Iterator<? extends KV<K, V>> iterator) {
    while (iterator.hasNext()) {
      this.checkCanAdd(1);
      final var kv = iterator.next();
      this.addUnsafe(kv.key(), kv.value());
    }
    this.hashCodeReseted = true;
    return this;
  }

  public MMultiMap<K, V> addStreamEntry(final Stream<? extends Entry<K, V>> stream) {
    final Iterator<? extends Map.Entry<K, V>> iterator = stream.iterator();
    while (iterator.hasNext()) {
      this.checkCanAdd(1);
      final Map.Entry<K, V> kv = iterator.next();
      this.addUnsafe(kv.getKey(), kv.getValue());
    }
    this.hashCodeReseted = true;
    return this;
  }

  private void addUnsafe(final K key, final V value) {
    final var kv = this.data.get(key);
    if (kv == null) {
      this.data.replace(KV.of(key, MList.of(value)));
    }
    else {
      kv.value().add(value);
    }
    this.size++;
  }

  @Override
  public Collection<KV<K, V>> asCollection() {
    return VectorUtils.wrapToCollection(this);
  }

  @Override
  public int capacity() {
    return Math.max(this.capacity, this.data.size());
  }

  @Override
  public MMultiMap<K, V> capacity(final int newCapacity) {
    if (this.capacity() != newCapacity) {
      if (newCapacity < 0 || newCapacity > this.maxCapacity()) {
        throw new OutOfMemoryError(String.format("Can't set capacity %s. newCapacity:%s > maxCapacity:%s",
          this.getClass(), newCapacity, this.maxCapacity()));
      }
      if (newCapacity < this.data.size()) {
        final MMultiMap<K, V> resized = this.stream().limit(newCapacity).collect(collector(this.keysSize()));
        this.data = resized.data;
        this.size = resized.size();
        this.hashCode = resized.hashCode();
        this.hashCodeReseted = false;
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
  public MMultiMap<K, V> clear() {
    this.data.clear();
    this.size = 0;
    this.hashCodeReseted = true;
    return this;
  }

  @Override
  public boolean contains(final KV<K, V> kv) {
    final KV<K, MList<V>> e = this.data.get(kv.key());
    if (e == null) {
      return false;
    }
    return e.value().contains(kv.value());
  }

  public boolean containsKey(final K key) {
    return this.data.contains(key);
  }

  public boolean containsValue(final V value) {
    return this.values().contains(value);
  }

  public IMultiMap<K, V> destroy() {
    final IMultiMap<K, V> result = IMultiMap.of(this.size, this.data.toImmutable(MList::destroy));
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
    if (this.data.isEmpty()) {
      return null;
    }
    final KV<K, MList<V>> kv = this.data.iterator().next();
    return kv.value().isEmpty() ? null : KV.of(kv.key(), kv.value().first());
  }

  public V first(final K key) {
    final KV<K, MList<V>> kv = this.data.get(key);
    return kv == null ? null : kv.value().first();
  }

  public MList<V> get(final K key) {
    return this.get(key, null);
  }

  public MList<V> get(final K key, final MList<V> defaultValue) {
    final KV<K, MList<V>> kv = this.data.get(key);
    return kv == null ? defaultValue : kv.value();
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
    final Iterator<KV<K, MList<V>>> nodeIterator = this.data.iterator();
    return new Iterator<>() {

      private Iterator<V> valueIterator = null;

      private K key;

      @Override
      public boolean hasNext() {
        return nodeIterator.hasNext() || (this.valueIterator != null && this.valueIterator.hasNext());
      }

      @Override
      public KV<K, V> next() {
        if (this.valueIterator == null || !this.valueIterator.hasNext()) {
          final var kv = nodeIterator.next();
          this.key = kv.key();
          this.valueIterator = kv.value().iterator();
        }
        return KV.of(this.key, this.valueIterator.next());
      }

    };
  }

  @Copy
  public IList<KV<K, IList<V>>> keyMultiValues() {
    final MList<KV<K, IList<V>>> mList = MList.create(this.keysSize());
    for (final KV<K, MList<V>> kv : this.data) {
      mList.add(KV.of(kv.key(), kv.value().toIList()));
    }
    return mList.toIList();
  }

  public IList<K> keys() {
    return this.data.stream().map(KV::key).collect(IList.collector(this.keysSize()));
  }

  public int keysSize() {
    return this.data.size();
  }

  public IList<KV<K, V>> keyValues() {
    return this.stream().collect(IList.collector(this.size()));
  }

  @Override
  public int maxCapacity() {
    return this.maxCapacity;
  }

  public MMultiMap<K, V> remove(final K key, final V value) {
    return this.remove(KV.of(key, value));
  }

  @Override
  public MMultiMap<K, V> remove(final KV<K, V> keyValue) {
    final KV<K, MList<V>> kv = this.data.get(keyValue.key());
    if (kv != null) {
      final MList<V> items = kv.value();
      if (items != null) {
        final int sizeBefore = items.size();
        items.remove(keyValue.value());
        if (sizeBefore > items.size()) {
          this.size--;
          this.hashCodeReseted = true;
        }
        if (items.isEmpty()) {
          this.data.remove(keyValue.key());
        }
      }
    }
    return this;
  }

  @Override
  public MMultiMap<K, V> removeAll(final Iterable<? extends KV<K, V>> values) {
    for (final KV<K, V> value : values) {
      this.remove(value);
    }
    return this;
  }

  public MMultiMap<K, V> removeKey(final K key) {
    final KV<K, MList<V>> kv = this.data.get(key);
    if (kv != null) {
      final MList<V> items = kv.value();
      if (items != null) {
        this.data.remove(key);
        this.size = this.size - items.size();
        this.hashCodeReseted = true;
      }
    }
    return this;
  }

  @Override
  public int size() {
    return this.size;
  }

  @Override
  public MMultiMap<K, V> size(final int newSize) {
    if (newSize < this.data.size()) {
      final MMultiMap<K, V> resized = this.stream().limit(newSize).collect(collector(this.keysSize()));
      this.data = resized.data;
      this.size = resized.size();
      this.hashCode = resized.hashCode();
      this.hashCodeReseted = false;
    }
    return this;
  }

  @Override
  public MMultiMap<K, V> sort(final Comparator<KV<K, V>> comparator) {
    final MMultiMap<K, V> sorted = this.stream().sorted(comparator).collect(collector(this.keysSize()));
    this.data = sorted.data;
    this.hashCode = sorted.hashCode();
    this.hashCodeReseted = false;
    return this;
  }

  @Override
  public Spliterator<KV<K, V>> spliterator() {
    return Spliterators.spliterator(this.iterator(), this.size(), Spliterator.ORDERED | Spliterator.SIZED);
  }

  public Stream<KV<K, MList<V>>> steamKeyMultiValues() {
    return this.data.stream();
  }

  @Override
  public Stream<KV<K, V>> stream() {
    return StreamSupport.stream(this.spliterator(), false);
  }

  @Override
  @SuppressWarnings("unchecked")
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
  public IList<KV<K, V>> toIList() {
    return this.stream().collect(IList.collector(this.size()));
  }

  public IMultiMap<K, V> toIMultiMap() {
    return IMultiMap.of(this.size, this.data.toImmutable(MList::toIList));
  }

  @Override
  public String toString() {
    return this.data.toString();
  }

  @Override
  public MMultiMap<K, V> trim() {
    this.capacity = this.data.size();
    this.data.trim();
    return this;
  }

  public IList<V> values() {
    return this.stream().map(KV::value).collect(IList.collector(this.size()));
  }

}
