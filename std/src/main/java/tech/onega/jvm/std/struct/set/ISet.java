package tech.onega.jvm.std.struct.set;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import tech.onega.jvm.std.annotation.Copy;
import tech.onega.jvm.std.annotation.Immutable;
import tech.onega.jvm.std.annotation.Self;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.struct.hash.KV;
import tech.onega.jvm.std.struct.hash.table.HashTableImmutable;
import tech.onega.jvm.std.struct.iterable.IterableUtils;
import tech.onega.jvm.std.struct.iterator.IteratorMap;
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.struct.stream.StreamUtils;
import tech.onega.jvm.std.struct.vector.IVector;
import tech.onega.jvm.std.struct.vector.Vector;
import tech.onega.jvm.std.struct.vector.VectorUtils;

@Immutable
@ThreadSafe
final public class ISet<V> implements IVector<V> {

  private static final long serialVersionUID = 1L;

  private static final ISet<Object> EMPTY_REF = new ISet<>(HashTableImmutable.empty());

  public static <V> Collector<V, ?, ISet<V>> collector() {
    return collector(VectorUtils.DEFAULT_INITIAL_CAPACITY);
  }

  public static <V> Collector<V, ?, ISet<V>> collector(final int initialSize) {
    return StreamUtils.simpleCollector(() -> MSet.<V>create(initialSize, VectorUtils.DEFAULT_MAX_CAPACITY), MSet::add,
      MSet::destroy);
  }

  @Immutable
  public static <V> ISet<V> copy(final Iterable<? extends V> iterable) {
    return MSet.<V>copy(iterable).destroy();
  }

  @Immutable
  public static <V> ISet<V> copy(final Stream<? extends V> stream) {
    return MSet.<V>copy(stream).destroy();
  }

  @Immutable
  @Copy
  public static <V> ISet<V> copy(final V[] vals, final int limit, final int offset) {
    return MSet.<V>copy(vals, limit, offset).destroy();
  }

  @SafeVarargs
  public static <V> ISet<V> copy(final Vector<V>... vectors) {
    if (vectors == null || vectors.length == 0) {
      return empty();
    }
    int totalSize = 0;
    for (final Vector<V> vector : vectors) {
      totalSize += vector.size();
    }
    if (totalSize == 0) {
      return empty();
    }
    final var result = MSet.<V>create(totalSize);
    for (final Vector<V> vector : vectors) {
      result.addAll(vector);
    }
    return result.destroy();
  }

  @SuppressWarnings("unchecked")
  public static <V> ISet<V> empty() {
    return (ISet<V>) EMPTY_REF;
  }

  @SafeVarargs
  public static final <V> ISet<V> of(final V... values) {
    return MSet.<V>of(values).destroy();
  }

  @Immutable
  public static <V> ISet<V> wrap(final HashTableImmutable<V, Object> data) {
    return (data == null || data.isEmpty()) ? empty() : new ISet<>(data);
  }

  private final HashTableImmutable<V, Object> data;

  private final int hashCode;

  private ISet(final HashTableImmutable<V, Object> data) {
    this.hashCode = data.hashCode();
    this.data = data;
  }

  @Override
  @Self
  public Collection<V> asCollection() {
    return VectorUtils.wrapToCollection(this);
  }

  @Override
  public boolean contains(final V value) {
    return data.contains(value);
  }

  @Override
  public boolean equals(final Object obj) {
    return VectorUtils.equals(this, obj);
  }

  @Override
  public V first() {
    final KV<V, Object> first = data.first();
    return first == null ? null : first.key;
  }

  @Override
  public int hashCode() {
    return hashCode;
  }

  @Override
  public boolean isEmpty() {
    return data.isEmpty();
  }

  @Override
  public Iterator<V> iterator() {
    return new IteratorMap<>(data.iterator(), KV::key);
  }

  @Copy
  public ISet<V> remove(final Iterable<? extends V> vals) {
    final MSet<V> data = this.toMSet();
    data.removeAll(vals);
    return data.destroy();
  }

  @Override
  public int size() {
    return data.size();
  }

  @Copy
  public ISet<V> sort(final Comparator<V> comparator) {
    return stream()
      .sorted(comparator)
      .collect(collector(size()));
  }

  @Override
  public Spliterator<V> spliterator() {
    return Spliterators.spliterator(iterator(), size(),
      Spliterator.ORDERED | Spliterator.SIZED | Spliterator.DISTINCT | Spliterator.IMMUTABLE);
  }

  @Override
  public Stream<V> stream() {
    return StreamSupport.stream(spliterator(), false);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Object[] toArray() {
    return IterableUtils.toArray((Iterable<Object>) this, data.size(), 0, Object.class);
  }

  @Override
  public IList<V> toIList() {
    return stream().collect(IList.collector(size()));
  }

  public List<V> toList() {
    return stream().collect(Collectors.toList());
  }

  public MSet<V> toMSet() {
    return MSet.copy(this);
  }

  public Set<V> toSet() {
    return new LinkedHashSet<>(VectorUtils.wrapToCollection(this));
  }

  @Override
  public String toString() {
    return IterableUtils.toString(this);
  }

}
