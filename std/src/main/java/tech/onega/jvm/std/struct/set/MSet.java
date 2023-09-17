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
    this.hashCodeReseted = true;
    this.hashCode = 0;
    this.maxCapacity = maxCapacity;
    this.capacity = initialCapacity;
    this.data = HashTable.lifo(initialCapacity, maxCapacity);
  }

  @Self
  @Override
  public MSet<V> add(final V value) {
    this.checkCanAdd(1);
    this.data.replace(KV.of(value, null));
    this.hashCodeReseted = true;
    return this;
  }

  @Override
  public MSet<V> addAll(final Iterable<? extends V> iterable) {
    return this.addIterator(iterable.iterator());
  }

  @Override
  public MSet<V> addAll(final Iterable<? extends V> iterable, final int limit) {
    return this.addIterator(IteratorUtils.limit(iterable.iterator(), limit));
  }

  @Override
  public MSet<V> addAll(final Iterable<? extends V> iterable, final int limit, final int offset) {
    return this.addIterator(IteratorUtils.limitOffset(iterable.iterator(), limit, offset));
  }

  @Override
  public MSet<V> addAll(final Stream<? extends V> stream) {
    return this.addIterator(stream.iterator());
  }

  @SuppressWarnings("unchecked")
  @Self
  @Override
  public MSet<V> addAll(final V... values) {
    return this.addAll(values, values.length, 0);
  }

  @Self
  @Override
  public MSet<V> addAll(final V[] values, final int limit) {
    return this.addAll(values, limit, 0);
  }

  @Override
  public MSet<V> addAll(final V[] values, final int limit, final int offset) {
    if (values != null && values.length > 0) {
      this.checkCanAdd(limit);
      for (int i = offset; i < offset + limit; i++) {
        this.data.replace(KV.of(values[i], null));
      }
      this.hashCodeReseted = true;
    }
    return this;
  }

  @Self
  public MSet<V> addIterator(final Iterator<? extends V> iterator) {
    while (iterator.hasNext()) {
      this.checkCanAdd(1);
      this.data.replace(KV.of(iterator.next(), null));
    }
    this.hashCodeReseted = true;
    return this;
  }

  @Copy
  @Override
  public Collection<V> asCollection() {
    return VectorUtils.wrapToCollection(this);
  }

  @Override
  public int capacity() {
    return Math.max(this.capacity, this.data.size());
  }

  @Self
  @Override
  public MSet<V> capacity(final int newCapacity) {
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

  @Self
  @Override
  public MSet<V> clear() {
    this.data.clear();
    this.hashCodeReseted = true;
    return this;
  }

  @Override
  public boolean contains(final V value) {
    return this.data.contains(value);
  }

  public ISet<V> destroy() {
    final ISet<V> result = ISet.wrap(this.data.destroy());
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

  @Mutable
  @Self
  public <E extends Throwable> MSet<V> filter(final Lambda.Function<V, Boolean, E> filter) throws E {
    return this.filter(filter, this.size(), 0);
  }

  @Mutable
  @Self
  public <E extends Throwable> MSet<V> filter(final Lambda.Function<V, Boolean, E> filter, final int limit) throws E {
    return this.filter(filter, limit, 0);
  }

  @Mutable
  @Self
  public <E extends Throwable> MSet<V> filter(final Lambda.Function<V, Boolean, E> filter, final int limit, final int offset)
    throws E {
    if (this.isEmpty()) {
      return this;
    }
    this.data.walk(ctx -> {
      if (!filter.invoke(ctx.kv().key())) {
        ctx.remove();
      }
    }, limit, offset);
    this.hashCodeReseted = true;
    return this;
  }

  @Override
  public V first() {
    final KV<V, Object> first = this.data.first();
    return first == null ? null : first.key();
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
  public Iterator<V> iterator() {
    return new IteratorMap<>(this.data.iterator(), KV::key);
  }

  @Override
  public int maxCapacity() {
    return this.maxCapacity;
  }

  @Self
  @Override
  public MSet<V> remove(final V value) {
    this.data.remove(value);
    this.hashCodeReseted = true;
    return this;
  }

  @Override
  public MSet<V> removeAll(final Iterable<? extends V> values) {
    for (final V value : values) {
      this.remove(value);
    }
    return this;
  }

  @Override
  public int size() {
    return this.data.size();
  }

  @Self
  @Override
  public MSet<V> size(final int newSize) {
    this.data.size(newSize);
    this.hashCodeReseted = true;
    return this;
  }

  @Self
  @Override
  public MSet<V> sort(final Comparator<V> comparator) {
    this.data = this.data.stream()
      .sorted((kv1, kv2) -> comparator.compare(kv1.key(), kv2.key()))
      .collect(HashTable.collector(HashTable.lifo(this.size(), this.maxCapacity)));
    return this;
  }

  @Override
  public Spliterator<V> spliterator() {
    return Spliterators.spliterator(this.iterator(), this.size(), Spliterator.ORDERED | Spliterator.SIZED | Spliterator.DISTINCT);
  }

  @Override
  public Stream<V> stream() {
    return StreamSupport.stream(this.spliterator(), false);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Object[] toArray() {
    return IterableUtils.toArray((Iterable<Object>) this, this.size(), 0, Object.class);
  }

  @Override
  public V[] toArray(final Function<Integer, V[]> arrayFactory) {
    final var result = arrayFactory.apply(this.size());
    var i = 0;
    for (final var v : this) {
      result[i++] = v;
    }
    return result;
  }

  @Override
  public IList<V> toIList() {
    return this.isEmpty() ? IList.empty() : this.stream().collect(IList.collector(this.size()));
  }

  public ISet<V> toISet() {
    return ISet.wrap(this.data.toImmutable());
  }

  @Override
  public String toString() {
    return IterableUtils.toString(this);
  }

  @Self
  @Override
  public MSet<V> trim() {
    this.capacity = this.data.size();
    this.data.trim();
    return this;
  }

}
