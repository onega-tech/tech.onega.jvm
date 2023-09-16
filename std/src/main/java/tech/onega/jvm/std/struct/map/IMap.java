package tech.onega.jvm.std.struct.map;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import tech.onega.jvm.std.annotation.Copy;
import tech.onega.jvm.std.annotation.Immutable;
import tech.onega.jvm.std.lang.Equals;
import tech.onega.jvm.std.struct.hash.KV;
import tech.onega.jvm.std.struct.hash.table.HashTableImmutable;
import tech.onega.jvm.std.struct.iterable.IterableUtils;
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.struct.stream.StreamUtils;
import tech.onega.jvm.std.struct.vector.IVector;
import tech.onega.jvm.std.struct.vector.VectorUtils;

@Immutable
final public class IMap<K, V> implements IVector<KV<K, V>> {

  private static final long serialVersionUID = 1L;

  private static final IMap<Object, Object> EMPTY_REF = new IMap<>(HashTableImmutable.empty());

  public static <K, V> Collector<KV<K, V>, ?, IMap<K, V>> collector() {
    return collector(VectorUtils.DEFAULT_INITIAL_CAPACITY);
  }

  public static <K, V> Collector<KV<K, V>, ?, IMap<K, V>> collector(final int initialSize) {
    return StreamUtils.simpleCollector(() -> MMap.<K, V>create(initialSize, VectorUtils.DEFAULT_MAX_CAPACITY),
      MMap::add, MMap::destroy);
  }

  @SuppressWarnings("unchecked")
  public static <K, V> IMap<K, V> empty() {
    return (IMap<K, V>) EMPTY_REF;
  }

  @Immutable
  @Copy
  public static <K, V> IMap<K, V> of(final Iterable<? extends KV<K, V>> iterable) {
    return MMap.copy(iterable).destroy();
  }

  @Immutable
  @Copy
  public static <K, V> IMap<K, V> of(final java.util.Map<K, V> map) {
    return MMap.copy(map).destroy();
  }

  @SafeVarargs
  public static <K, V> IMap<K, V> of(final KV<K, V>... keyValues) {
    return MMap.of(keyValues).destroy();
  }

  @Immutable
  @Copy
  public static <K, V> IMap<K, V> of(final KV<K, V>[] keyValues, final int limit, final int offset) {
    return MMap.of(keyValues, limit, offset).destroy();
  }

  @Immutable
  @Copy
  public static <K, V> IMap<K, V> of(final Stream<? extends KV<K, V>> stream) {
    return MMap.of(stream).destroy();
  }

  public static <K, V> IMap<K, V> wrap(final HashTableImmutable<K, V> data) {
    return (data == null || data.isEmpty()) ? empty() : new IMap<>(data);
  }

  private final HashTableImmutable<K, V> data;

  private final IList<K> keys;

  private final IList<V> values;

  private final IList<KV<K, V>> keyValues;

  private IMap(final HashTableImmutable<K, V> data) {
    final int size = data.size();
    this.data = data;
    keys = data.stream().map(KV::key).collect(IList.collector(size));
    values = data.stream().map(KV::value).collect(IList.collector(size));
    keyValues = data.stream().collect(IList.collector(size));
  }

  @Override
  public Collection<KV<K, V>> asCollection() {
    return VectorUtils.wrapToCollection(this);
  }

  @Override
  public boolean contains(final KV<K, V> kv) {
    final KV<K, V> e = data.get(kv.key);
    return e == null ? false : Equals.yes(e.value, kv.value);
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
    return keyValues.first();
  }

  public V get(final K key) {
    return get(key, null);
  }

  public V get(final K key, final V defaultValue) {
    final KV<K, V> kv = data.get(key);
    return kv == null ? defaultValue : kv.value;
  }

  @Override
  public int hashCode() {
    return keyValues.hashCode();
  }

  @Override
  public boolean isEmpty() {
    return data.isEmpty();
  }

  @Override
  public Iterator<KV<K, V>> iterator() {
    return keyValues.iterator();
  }

  public IList<K> keys() {
    return keys;
  }

  public IList<KV<K, V>> keyValues() {
    return keyValues;
  }

  @Override
  public int size() {
    return data.size();
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
  @SuppressWarnings("unchecked")
  public Object[] toArray() {
    return IterableUtils.toArray((Iterable<Object>) (Iterable<?>) this, size(), 0, Object.class);
  }

  @Override
  public IList<KV<K, V>> toIList() {
    return keyValues;
  }

  public Map<K, V> toMap() {
    return data.stream()
      .collect(StreamUtils.selfCollector(
        () -> new LinkedHashMap<>(size()),
        (a, v) -> a.put(v.key(), v.value())));
  }

  @Override
  public String toString() {
    return keyValues.toString();
  }

  public IList<V> values() {
    return values;
  }

}
