package tech.onega.jvm.std.struct.map;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import tech.onega.jvm.std.annotation.Copy;
import tech.onega.jvm.std.annotation.Immutable;
import tech.onega.jvm.std.struct.hash.KV;
import tech.onega.jvm.std.struct.hash.table.HashTableImmutable;
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.struct.stream.StreamUtils;
import tech.onega.jvm.std.struct.vector.IVector;
import tech.onega.jvm.std.struct.vector.VectorUtils;

@Immutable
final public class IMultiMap<K, V> implements IVector<KV<K, V>> {

  private static final long serialVersionUID = 1L;

  private static final IMultiMap<Object, Object> EMPTY_REF = new IMultiMap<>(0, HashTableImmutable.empty());

  public static <K, V> Collector<KV<K, V>, ?, IMultiMap<K, V>> collector() {
    return collector(VectorUtils.DEFAULT_INITIAL_CAPACITY);
  }

  public static <K, V> Collector<KV<K, V>, ?, IMultiMap<K, V>> collector(final int initialSize) {
    return StreamUtils.simpleCollector(() -> MMultiMap.<K, V>create(initialSize, VectorUtils.DEFAULT_MAX_CAPACITY),
      MMultiMap::add, MMultiMap::destroy);
  }

  @SuppressWarnings("unchecked")
  public static <K, V> IMultiMap<K, V> empty() {
    return (IMultiMap<K, V>) EMPTY_REF;
  }

  public static <K, V> IMultiMap<K, V> of(final int size, final HashTableImmutable<K, IList<V>> data) {
    if (data == null || data.isEmpty() || size == 0) {
      return IMultiMap.empty();
    }
    return new IMultiMap<>(size, data);
  }

  @Immutable
  @Copy
  public static <K, V> IMultiMap<K, V> of(final Iterable<? extends KV<K, V>> iterable) {
    return MMultiMap.of(iterable).destroy();
  }

  @Immutable
  @Copy
  public static <K, V> IMultiMap<K, V> of(final java.util.Map<K, V> map) {
    if (map.isEmpty()) {
      return empty();
    }
    final MMultiMap<K, V> mMap = MMultiMap.create(map.size(), map.size());
    mMap.addStreamEntry(map.entrySet().stream());
    return mMap.destroy();
  }

  @SafeVarargs
  public static <K, V> IMultiMap<K, V> of(final KV<K, V>... keyValues) {
    return MMultiMap.of(keyValues).destroy();
  }

  @Immutable
  @Copy
  public static <K, V> IMultiMap<K, V> of(final KV<K, V>[] keyValues, final int limit, final int offset) {
    return MMultiMap.of(keyValues, limit, offset).destroy();
  }

  @Immutable
  @Copy
  public static <K, V> IMultiMap<K, V> of(final Stream<? extends KV<K, V>> stream) {
    return MMultiMap.of(stream).destroy();
  }

  private final HashTableImmutable<K, IList<V>> data;

  private final IList<K> keys;

  private final IList<V> values;

  private final IList<KV<K, V>> keyValues;

  private final IList<KV<K, IList<V>>> keyMultiValues;

  private IMultiMap(final int size, final HashTableImmutable<K, IList<V>> data) {
    this.data = data;
    final int keySize = data.size();
    final Object[] tmpKeyMultiValues = new Object[keySize];
    final Object[] tmpKeys = new Object[keySize];
    final Object[] tmpValues = new Object[size];
    final Object[] tmpKeyValues = new Object[size];
    int keyIndex = 0;
    int valueIndex = 0;
    for (final KV<K, IList<V>> kv : data) {
      tmpKeyMultiValues[keyIndex] = kv;
      tmpKeys[keyIndex] = kv.key();
      for (final V v : kv.value()) {
        tmpValues[valueIndex] = v;
        tmpKeyValues[valueIndex] = KV.of(kv.key(), v);
        valueIndex++;
      }
      keyIndex++;
    }
    keyMultiValues = IList.wrap(tmpKeyMultiValues);
    keys = IList.wrap(tmpKeys);
    values = IList.wrap(tmpValues);
    keyValues = IList.wrap(tmpKeyValues);
  }

  @Override
  public Collection<KV<K, V>> asCollection() {
    return VectorUtils.wrapToCollection(this);
  }

  @Override
  public boolean contains(final KV<K, V> kv) {
    final KV<K, IList<V>> e = data.get(kv.key());
    if (e == null) {
      return false;
    }
    return e.value.contains(kv.value);
  }

  public boolean containsKey(final K key) {
    return data.contains(key);
  }

  public boolean containsValue(final V value) {
    return values.contains(value);
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
    final KV<K, IList<V>> kv = data.first();
    return kv == null ? null : kv.value.isEmpty() ? null : KV.of(kv.key, kv.value.first());
  }

  public V first(final K key) {
    return first(key, null);
  }

  public V first(final K key, final V defaultValue) {
    final KV<K, IList<V>> kv = data.get(key);
    return kv == null ? defaultValue : kv.value.first();
  }

  public IList<V> get(final K key) {
    return get(key, null);
  }

  public IList<V> get(final K key, final IList<V> defaultValue) {
    final KV<K, IList<V>> node = data.get(key);
    return node == null ? defaultValue : node.value;
  }

  @Override
  public int hashCode() {
    return keyValues.hashCode();
  }

  @Override
  public boolean isEmpty() {
    return keyValues.isEmpty();
  }

  @Override
  public Iterator<KV<K, V>> iterator() {
    return keyValues.iterator();
  }

  public IList<KV<K, IList<V>>> keyMultiValues() {
    return keyMultiValues;
  }

  public IList<K> keys() {
    return keys;
  }

  public int keysSize() {
    return keys.size();
  }

  public IList<KV<K, V>> keyValues() {
    return keyValues;
  }

  @Override
  public int size() {
    return keyValues.size();
  }

  @Override
  public Spliterator<KV<K, V>> spliterator() {
    return Spliterators.spliterator(iterator(), size(),
      Spliterator.ORDERED | Spliterator.SIZED | Spliterator.IMMUTABLE);
  }

  @Override
  public Stream<KV<K, V>> stream() {
    return StreamSupport.stream(spliterator(), false);
  }

  @Override
  public Object[] toArray() {
    return keyValues.toArray();
  }

  @Override
  public IList<KV<K, V>> toIList() {
    return keyValues;
  }

  public IMultiMap<K, V> toIMultiMap() {
    return this;
  }

  @Override
  public String toString() {
    return keyMultiValues().toString();
  }

  public IList<V> values() {
    return values;
  }

}
