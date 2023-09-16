package tech.onega.jvm.std.struct.list;

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
import tech.onega.jvm.std.annotation.Unsafe;
import tech.onega.jvm.std.lang.Lambda;
import tech.onega.jvm.std.struct.array.ArrayUtils;
import tech.onega.jvm.std.struct.hash.Hash;
import tech.onega.jvm.std.struct.iterable.IterableUtils;
import tech.onega.jvm.std.struct.iterator.IteratorUtils;
import tech.onega.jvm.std.struct.stream.StreamUtils;
import tech.onega.jvm.std.struct.vector.MVector;
import tech.onega.jvm.std.struct.vector.Vector;
import tech.onega.jvm.std.struct.vector.VectorUtils;

@Mutable
final public class MList<V> implements MVector<V> {

  private static final long serialVersionUID = 1L;

  public static <V> Collector<V, ?, MList<V>> collector() {
    return collector(VectorUtils.DEFAULT_INITIAL_CAPACITY);
  }

  public static <V> Collector<V, ?, MList<V>> collector(final int initialSize) {
    return StreamUtils.selfCollector(() -> MList.<V>create(initialSize, VectorUtils.DEFAULT_MAX_CAPACITY), MList::add);
  }

  @Copy
  public static <V> MList<V> copy(final Iterable<? extends V> iterable) {
    final int size = IterableUtils.trySizeOf(iterable);
    return new MList<V>(Math.max(size, VectorUtils.DEFAULT_INITIAL_CAPACITY), VectorUtils.DEFAULT_MAX_CAPACITY)
      .addAll(iterable);
  }

  @Copy
  public static <V> MList<V> copy(final Stream<? extends V> stream) {
    return new MList<V>(VectorUtils.DEFAULT_INITIAL_CAPACITY, VectorUtils.DEFAULT_MAX_CAPACITY).addAll(stream);
  }

  @Immutable
  @Copy
  public static <V> MList<V> copy(final V[] vals, final int limit, final int offset) {
    return new MList<V>(limit, VectorUtils.DEFAULT_MAX_CAPACITY).addAll(vals, limit, offset);
  }

  public static <V> MList<V> create() {
    return new MList<>(VectorUtils.DEFAULT_INITIAL_CAPACITY, VectorUtils.DEFAULT_MAX_CAPACITY);
  }

  public static <V> MList<V> create(final int initialCapacity) {
    return create(initialCapacity, VectorUtils.DEFAULT_MAX_CAPACITY);
  }

  public static <V> MList<V> create(final int initialCapacity, final int maxCapacity) {
    return new MList<>(initialCapacity, maxCapacity);
  }

  public static <V> MList<V> empty() {
    return of();
  }

  @SafeVarargs
  public static <V> MList<V> of(final V... values) {
    return copy(values, values.length, 0);
  }

  private Object[] data;

  private int size = 0;

  private int hashCode = 0;

  private boolean hashCodeReseted = true;

  private int maxCapacity;

  @Unsafe
  protected MList(final int initialCapacity, final int maxCapacity) {
    this.maxCapacity = maxCapacity;
    this.data = new Object[initialCapacity];
  }

  @Override
  public MList<V> add(final V value) {
    return set(size, value);
  }

  @Override
  public MList<V> addAll(final Iterable<? extends V> iterable) {
    return setAll(size, iterable);
  }

  @Override
  public MList<V> addAll(final Iterable<? extends V> iterable, final int limit) {
    return setAll(size, iterable, limit);
  }

  @Override
  public MList<V> addAll(final Iterable<? extends V> iterable, final int limit, final int offset) {
    return setAll(size, iterable, limit, offset);
  }

  @Override
  public MList<V> addAll(final Stream<? extends V> stream) {
    return setAll(size, stream);
  }

  @SuppressWarnings("unchecked")
  @Override
  public MList<V> addAll(final V... values) {
    return setAll(size, values, values.length, 0);
  }

  @Override
  public MList<V> addAll(final V[] values, final int limit) {
    return setAll(size, values, limit);
  }

  @Override
  public MList<V> addAll(final V[] values, final int limit, final int offset) {
    return setAll(size, values, limit, offset);
  }

  @Override
  public Collection<V> asCollection() {
    return VectorUtils.wrapToCollection(this);
  }

  private void assertIndexInBoundsRange(final int limit, final int offset) {
    if (limit + offset > size) {
      throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public int capacity() {
    return data.length;
  }

  @Override
  public MList<V> capacity(final int newCapacity) {
    final int curCapacity = capacity();
    if (curCapacity != newCapacity) {
      if (newCapacity < 0 || newCapacity > maxCapacity()) {
        throw new OutOfMemoryError(String.format("Can't set capacity %s. newCapacity:%s > maxCapacity:%s",
          this.getClass(), newCapacity, maxCapacity()));
      }
      final Object[] tmp = new Object[newCapacity];
      final int newSize = Math.min(size, newCapacity);
      System.arraycopy(this.data, 0, tmp, 0, newSize);
      this.data = tmp;
      this.size = newSize;
      hashCodeReseted = true;
    }
    return this;
  }

  @Override
  public MList<V> clear() {
    this.size = 0;
    hashCodeReseted = true;
    return this;
  }

  @Override
  public boolean contains(final V value) {
    return ArrayUtils.contains(data, value, size, 0);
  }

  public IList<V> destroy() {
    final Object[] oldData = this.data;
    final int oldSize = size;
    this.data = null;
    this.size = 0;
    this.maxCapacity = 0;
    if (oldData.length == 0) {
      return IList.empty();
    }
    else {
      final Object[] result = (oldSize == oldData.length) ? oldData
        : ArrayUtils.copy(Object.class, oldData, oldSize, 0);
      return IList.wrap(result);
    }
  }

  @Override
  public boolean equals(final Object obj) {
    return VectorUtils.equals(this, obj);
  }

  private void expand(final int needSize) {
    this.data = ArrayUtils.grow(this.data, Object.class, needSize, maxCapacity);
  }

  public MList<V> fill(final V v) {
    return fill(v, size, 0);
  }

  public MList<V> fill(final V v, final int limit) {
    return fill(v, limit, 0);
  }

  public MList<V> fill(final V v, final int limit, final int offset) {
    assertIndexInBoundsRange(limit, offset);
    for (int i = offset; i < offset + limit; i++) {
      data[i] = v;
    }
    hashCodeReseted = true;
    return this;
  }

  public <E extends Throwable> MList<V> filter(final Lambda.Function<V, Boolean, E> filter) throws E {
    return filter(filter, size, 0);
  }

  public <E extends Throwable> MList<V> filter(final Lambda.Function<V, Boolean, E> filter, final int limit) throws E {
    return filter(filter, limit, 0);
  }

  @SuppressWarnings("unchecked")
  public <E extends Throwable> MList<V> filter(final Lambda.Function<V, Boolean, E> filter, final int limit, final int offset)
    throws E {
    assertIndexInBoundsRange(limit, offset);
    final Object[] tmp = new Object[limit];
    final int filtered = ArrayUtils.filter(data, limit, offset, tmp, 0, (Lambda.Function<Object, Boolean, E>) filter);
    this.data = tmp;
    this.size = filtered;
    hashCodeReseted = true;
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public V first() {
    return size == 0 ? null : (V) data[0];
  }

  @SuppressWarnings("unchecked")
  public V get(final int pos) {
    if ((pos >= size)) {
      throw new ArrayIndexOutOfBoundsException();
    }
    return (V) data[pos];
  }

  @Override
  public int hashCode() {
    if (isEmpty()) {
      return 0;
    }
    else if (hashCodeReseted) {
      hashCode = Hash.iterable(this);
      hashCodeReseted = false;
    }
    return hashCode;
  }

  public int indexOf(final V value) {
    return IterableUtils.indexOf(this, value);
  }

  @Override
  public boolean isEmpty() {
    return size == 0;
  }

  @Override
  public boolean isFull() {
    return size() == maxCapacity();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Iterator<V> iterator() {
    return ArrayUtils.iterator((V[]) data, size, 0);
  }

  @SuppressWarnings("unchecked")
  public V last() {
    return size == 0 ? null : (V) data[size - 1];
  }

  public <E extends Throwable> MList<V> map(final Lambda.Function<V, V, E> mapper) throws E {
    return map(mapper, size, 0);
  }

  public <E extends Throwable> MList<V> map(final Lambda.Function<V, V, E> mapper, final int limit) throws E {
    return map(mapper, limit, 0);
  }

  @SuppressWarnings("unchecked")
  public <E extends Throwable> MList<V> map(final Lambda.Function<V, V, E> mapper, final int limit, final int offset)
    throws E {
    assertIndexInBoundsRange(limit, offset);
    for (int i = offset; i < offset + limit; i++) {
      data[i] = mapper.invoke((V) data[i]);
    }
    hashCodeReseted = true;
    return this;
  }

  @Override
  public int maxCapacity() {
    return maxCapacity;
  }

  @Override
  public MList<V> remove(final V value) {
    final int index = indexOf(value);
    return removePos(index);
  }

  @Override
  public MList<V> removeAll(final Iterable<? extends V> values) {
    for (final V value : values) {
      remove(value);
    }
    return this;
  }

  public MList<V> removeLast() {
    if (size > 0) {
      data[size - 1] = null;
      size--;
    }
    return this;
  }

  public MList<V> removePos(final int index) {
    if (index == size - 1) {
      return removeLast();
    }
    if (index >= 0) {
      System.arraycopy(data, index + 1, data, index, size - index - 1);
      size--;
      hashCodeReseted = true;
    }
    return this;
  }

  public MList<V> reverse() {
    ArrayUtils.reverse(data, size);
    return this;
  }

  public MList<V> set(final int index, final V value) {
    expand(index + 1);
    data[index] = value;
    size = Math.max(size, index + 1);
    hashCodeReseted = true;
    return this;
  }

  @SuppressWarnings("unchecked")
  public MList<V> setAll(final int pos, final Iterable<? extends V> iterable) {
    if (iterable instanceof IList) {
      final IList<? extends V> list = (IList<? extends V>) iterable;
      return setAll(pos, (V[]) list.data, list.data.length);
    }
    else if (iterable instanceof MList) {
      final MList<? extends V> list = (MList<? extends V>) iterable;
      return setAll(pos, (V[]) list.data, list.data.length);
    }
    else if (iterable instanceof Vector) {
      return setAll(pos, iterable, ((Vector<V>) iterable).size(), 0);
    }
    else if (iterable instanceof Collection) {
      return setAll(pos, iterable, ((Collection<V>) iterable).size(), 0);
    }
    else {
      return setIterator(pos, iterable.iterator());
    }
  }

  public MList<V> setAll(final int pos, final Iterable<? extends V> iterable, final int limit) {
    expand(pos + limit);
    return setIterator(pos, IteratorUtils.limit(iterable.iterator(), limit));
  }

  public MList<V> setAll(final int pos, final Iterable<? extends V> iterable, final int limit, final int offset) {
    expand(pos + limit);
    return setIterator(pos, IteratorUtils.limitOffset(iterable.iterator(), limit, offset));
  }

  public MList<V> setAll(final int pos, final Stream<? extends V> stream) {
    return setAll(pos, IterableUtils.ofStream(stream));
  }

  @SuppressWarnings("unchecked")
  public MList<V> setAll(final int pos, final V... values) {
    return setAll(pos, values, values.length, 0);
  }

  public MList<V> setAll(final int pos, final V[] values, final int limit) {
    return setAll(pos, values, limit, 0);
  }

  public MList<V> setAll(final int pos, final V[] values, final int limit, final int offset) {
    if ((((values != null) && (values.length != 0)) && (limit > 0))) {
      expand(pos + limit);
      System.arraycopy(values, offset, data, pos, limit);
      size = Math.max(size, pos + limit);
      hashCodeReseted = true;
    }
    return this;
  }

  private MList<V> setIterator(final int pos, final Iterator<? extends V> iterator) {
    int index = pos;
    while (iterator.hasNext()) {
      if (index == data.length) {
        expand(index + 1);
      }
      data[index++] = iterator.next();
    }
    size = Math.max(size, index);
    hashCodeReseted = true;
    return this;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public MList<V> size(final int newSize) {
    if (newSize < 0 || newSize > maxCapacity()) {
      throw new OutOfMemoryError(
        String.format("Can't set new size %s. newSize:%s > maxCapacity:%s", this.getClass(), newSize, maxCapacity()));
    }
    if (capacity() < newSize) {
      capacity(newSize);
    }
    this.size = newSize;
    hashCodeReseted = true;
    return this;
  }

  @Override
  public MList<V> sort(final Comparator<V> comparator) {
    return sort(comparator, size, 0);
  }

  public MList<V> sort(final Comparator<V> comparator, final int limit) {
    return sort(comparator, limit, 0);
  }

  @SuppressWarnings("unchecked")
  public MList<V> sort(final Comparator<V> comparator, final int limit, final int offset) {
    assertIndexInBoundsRange(limit, offset);
    java.util.Arrays.sort(data, offset, offset + limit, (Comparator<Object>) comparator);
    hashCodeReseted = true;
    return this;
  }

  @Override
  public Spliterator<V> spliterator() {
    return Spliterators.spliterator(iterator(), size(), Spliterator.ORDERED | Spliterator.SIZED);
  }

  @Override
  public Stream<V> stream() {
    return StreamSupport.stream(spliterator(), false);
  }

  @Copy
  @Override
  public Object[] toArray() {
    final Object[] result = new Object[size];
    System.arraycopy(data, 0, result, 0, size);
    return result;
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

  @Copy
  @Override
  public IList<V> toIList() {
    if (isEmpty()) {
      return IList.empty();
    }
    return IList.wrap(ArrayUtils.copy(Object.class, data, size, 0));
  }

  @Override
  public String toString() {
    return IterableUtils.toString(this);
  }

  @Override
  public MList<V> trim() {
    if (data.length > size) {
      final Object[] tmp = new Object[size];
      System.arraycopy(data, 0, tmp, 0, size);
      this.data = tmp;
      this.size = tmp.length;
    }
    return this;
  }

}
