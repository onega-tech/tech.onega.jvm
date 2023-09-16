package tech.onega.jvm.std.struct.set;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
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
import tech.onega.jvm.std.annotation.Unsafe;
import tech.onega.jvm.std.lang.Lambda;
import tech.onega.jvm.std.struct.hash.KV;
import tech.onega.jvm.std.struct.hash.table.HashTable;
import tech.onega.jvm.std.struct.iterable.IterableUtils;
import tech.onega.jvm.std.struct.iterator.IteratorMap;
import tech.onega.jvm.std.struct.iterator.IteratorUtils;
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.struct.stream.StreamUtils;
import tech.onega.jvm.std.struct.vector.MVector;
import tech.onega.jvm.std.struct.vector.VectorUtils;

@Mutable
final public class MSet<V> implements MVector<V> {

  private static final long serialVersionUID = 1L;

  public static <V> Collector<V, ?, MSet<V>> collector() {
    return collector(VectorUtils.DEFAULT_INITIAL_CAPACITY);
  }

  public static <V> Collector<V, ?, MSet<V>> collector(final int initialSize) {
    return StreamUtils.selfCollector(() -> MSet.<V>create(initialSize, VectorUtils.DEFAULT_MAX_CAPACITY), MSet::add);
  }

  @Copy
  public static <V> MSet<V> copy(final Iterable<? extends V> iterable) {
    final int size = IterableUtils.trySizeOf(iterable);
    return new MSet<V>(Math.max(size, VectorUtils.DEFAULT_INITIAL_CAPACITY), VectorUtils.DEFAULT_MAX_CAPACITY)
      .addAll(iterable);
  }

  @Copy
  public static <V> MSet<V> copy(final Stream<? extends V> stream) {
    return new MSet<V>(VectorUtils.DEFAULT_INITIAL_CAPACITY, VectorUtils.DEFAULT_MAX_CAPACITY).addAll(stream);
  }

  @Immutable
  @Copy
  public static <V> MSet<V> copy(final V[] vals, final int limit) {
    return new MSet<V>(limit, VectorUtils.DEFAULT_INITIAL_CAPACITY).addAll(vals, limit);
  }

  @Immutable
  @Copy
  public static <V> MSet<V> copy(final V[] vals, final int limit, final int offset) {
    return new MSet<V>(limit, VectorUtils.DEFAULT_INITIAL_CAPACITY).addAll(vals, limit, offset);
  }

  public static <V> MSet<V> create() {
    return new MSet<>(VectorUtils.DEFAULT_INITIAL_CAPACITY, VectorUtils.DEFAULT_MAX_CAPACITY);
  }

  public static <V> MSet<V> create(final int initialCapacity) {
    return new MSet<>(initialCapacity, VectorUtils.DEFAULT_MAX_CAPACITY);
  }

  public static <V> MSet<V> create(final int initialCapacity, final int maxCapacity) {
    return new MSet<>(initialCapacity, maxCapacity);
  }

  @SafeVarargs
  public static <V> MSet<V> of(final V... values) {
    return new MSet<V>(values.length, VectorUtils.DEFAULT_INITIAL_CAPACITY).addAll(values);
  }

  private HashTable<V, Object> data;

  private int capacity;

  private int hashCode;

  private boolean hashCodeReseted;

  private final int maxCapacity;

  @Unsafe
  protected MSet(final int initialCapacity, final int maxCapacity) {
    hashCodeReseted = true;
    this.hashCode = 0;
    this.maxCapacity = maxCapacity;
    this.capacity = initialCapacity;
    data = HashTable.lifo(initialCapacity, maxCapacity);
  }

  @Self
  @Override
  public MSet<V> add(final V value) {
    checkCanAdd(1);
    data.replace(KV.of(value, null));
    hashCodeReseted = true;
    return this;
  }

  @Override
  public MSet<V> addAll(final Iterable<? extends V> iterable) {
    return addIterator(iterable.iterator());
  }

  @Override
  public MSet<V> addAll(final Iterable<? extends V> iterable, final int limit) {
    return addIterator(IteratorUtils.limit(iterable.iterator(), limit));
  }

  @Override
  public MSet<V> addAll(final Iterable<? extends V> iterable, final int limit, final int offset) {
    return addIterator(IteratorUtils.limitOffset(iterable.iterator(), limit, offset));
  }

  @Override
  public MSet<V> addAll(final Stream<? extends V> stream) {
    return addIterator(stream.iterator());
  }

  @SuppressWarnings("unchecked")
  @Self
  @Override
  public MSet<V> addAll(final V... values) {
    return addAll(values, values.length, 0);
  }

  @Self
  @Override
  public MSet<V> addAll(final V[] values, final int limit) {
    return addAll(values, limit, 0);
  }

  @Override
  public MSet<V> addAll(final V[] values, final int limit, final int offset) {
    if (values != null && values.length > 0) {
      checkCanAdd(limit);
      for (int i = offset; i < offset + limit; i++) {
        data.replace(KV.of(values[i], null));
      }
      hashCodeReseted = true;
    }
    return this;
  }

  @Self
  public MSet<V> addIterator(final Iterator<? extends V> iterator) {
    while (iterator.hasNext()) {
      checkCanAdd(1);
      data.replace(KV.of(iterator.next(), null));
    }
    hashCodeReseted = true;
    return this;
  }

  @Copy
  @Override
  public Collection<V> asCollection() {
    return VectorUtils.wrapToCollection(this);
  }

  @Override
  public int capacity() {
    return Math.max(capacity, data.size());
  }

  @Self
  @Override
  public MSet<V> capacity(final int newCapacity) {
    if (capacity() != newCapacity) {
      if (newCapacity < 0 || newCapacity > maxCapacity()) {
        throw new OutOfMemoryError(String.format("Can't set capacity %s. newCapacity:%s > maxCapacity:%s",
          this.getClass(), newCapacity, maxCapacity()));
      }
      if (newCapacity < data.size()) {
        data.size(newCapacity);
        hashCodeReseted = true;
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

  @Self
  @Override
  public MSet<V> clear() {
    data.clear();
    hashCodeReseted = true;
    return this;
  }

  @Override
  public boolean contains(final V value) {
    return data.contains(value);
  }

  public ISet<V> destroy() {
    final ISet<V> result = ISet.wrap(data.destroy());
    data = null;
    this.hashCode = 0;
    this.hashCodeReseted = false;
    this.capacity = 0;
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    return VectorUtils.equals(this, obj);
  }

  @Mutable
  @Self
  public <E extends Throwable> MSet<V> filter(final Lambda.Function<V, Boolean, E> filter) throws E {
    return filter(filter, size(), 0);
  }

  @Mutable
  @Self
  public <E extends Throwable> MSet<V> filter(final Lambda.Function<V, Boolean, E> filter, final int limit) throws E {
    return filter(filter, limit, 0);
  }

  @Mutable
  @Self
  public <E extends Throwable> MSet<V> filter(final Lambda.Function<V, Boolean, E> filter, final int limit, final int offset)
    throws E {
    if (isEmpty()) {
      return this;
    }
    data.walk(ctx -> {
      if (!filter.invoke(ctx.kv().key)) {
        ctx.remove();
      }
    }, limit, offset);
    this.hashCodeReseted = true;
    return this;
  }

  @Override
  public V first() {
    final KV<V, Object> first = data.first();
    return first == null ? null : first.key;
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
  public Iterator<V> iterator() {
    return new IteratorMap<>(data.iterator(), KV::key);
  }

  @Override
  public int maxCapacity() {
    return maxCapacity;
  }

  @Self
  @Override
  public MSet<V> remove(final V value) {
    data.remove(value);
    hashCodeReseted = true;
    return this;
  }

  @Override
  public MSet<V> removeAll(final Iterable<? extends V> values) {
    for (final V value : values) {
      remove(value);
    }
    return this;
  }

  @Override
  public int size() {
    return data.size();
  }

  @Self
  @Override
  public MSet<V> size(final int newSize) {
    data.size(newSize);
    hashCodeReseted = true;
    return this;
  }

  @Self
  @Override
  public MSet<V> sort(final Comparator<V> comparator) {
    data = data.stream()
      .sorted((kv1, kv2) -> comparator.compare(kv1.key, kv2.key))
      .collect(HashTable.collector(HashTable.lifo(size(), maxCapacity)));
    return this;
  }

  @Override
  public Spliterator<V> spliterator() {
    return Spliterators.spliterator(iterator(), size(), Spliterator.ORDERED | Spliterator.SIZED | Spliterator.DISTINCT);
  }

  @Override
  public Stream<V> stream() {
    return StreamSupport.stream(spliterator(), false);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Object[] toArray() {
    return IterableUtils.toArray((Iterable<Object>) this, size(), 0, Object.class);
  }

  @Override
  public V[] toArray(Function<Integer, V[]> arrayFactory) {
    var result = arrayFactory.apply(this.size());
    var i = 0;
    for (var v : this) {
      result[i++] = v;
    }
    return result;
  }

  @Override
  public IList<V> toIList() {
    return isEmpty() ? IList.empty() : stream().collect(IList.collector(size()));
  }

  public ISet<V> toISet() {
    return ISet.wrap(data.toImmutable());
  }

  @Override
  public String toString() {
    return IterableUtils.toString(this);
  }

  @Self
  @Override
  public MSet<V> trim() {
    this.capacity = data.size();
    data.trim();
    return this;
  }

}
