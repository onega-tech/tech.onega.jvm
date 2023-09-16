package tech.onega.jvm.std.struct.list;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import tech.onega.jvm.std.annotation.Copy;
import tech.onega.jvm.std.annotation.Immutable;
import tech.onega.jvm.std.annotation.Unsafe;
import tech.onega.jvm.std.lang.Lambda;
import tech.onega.jvm.std.struct.array.ArrayUtils;
import tech.onega.jvm.std.struct.hash.Hash;
import tech.onega.jvm.std.struct.iterable.IterableUtils;
import tech.onega.jvm.std.struct.set.ISet;
import tech.onega.jvm.std.struct.stream.StreamUtils;
import tech.onega.jvm.std.struct.vector.IVector;
import tech.onega.jvm.std.struct.vector.Vector;
import tech.onega.jvm.std.struct.vector.VectorUtils;

@Immutable
final public class IList<V> implements IVector<V> {

  private static final long serialVersionUID = 1L;

  private static final IList<Object> EMPTY_REF = new IList<>(new Object[0]);

  public static <V> Collector<V, ?, IList<V>> collector() {
    return collector(VectorUtils.DEFAULT_INITIAL_CAPACITY);
  }

  public static <V> Collector<V, ?, IList<V>> collector(final int initialSize) {
    return StreamUtils.simpleCollector(() -> MList.<V>create(initialSize, VectorUtils.DEFAULT_MAX_CAPACITY), MList::add,
      MList::destroy);
  }

  @Immutable
  @Copy
  public static <V> IList<V> copy(final Iterable<? extends V> iterable) {
    return MList.<V>copy(iterable).destroy();
  }

  @Immutable
  @Copy
  public static <V> IList<V> copy(final Stream<? extends V> stream) {
    return MList.<V>copy(stream).destroy();
  }

  @Immutable
  @Copy
  public static <V> IList<V> copy(final V[] vals, final int limit) {
    return copy(vals, limit);
  }

  @Immutable
  @Copy
  public static <V> IList<V> copy(final V[] vals, final int limit, final int offset) {
    if (vals == null || vals.length == 0 || limit <= 0 || vals.length <= offset) {
      return empty();
    }
    return wrap(ArrayUtils.copy(Object.class, vals, limit, offset));
  }

  @Immutable
  @Copy
  public static <V> IList<? extends V> copy(final Vector<? extends V> vector) {
    return vector.toIList();
  }

  @SafeVarargs
  public static <V> IList<V> copy(final Vector<V>... vectors) {
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
    final var result = MList.<V>create(totalSize);
    for (final Vector<V> vector : vectors) {
      result.addAll(vector);
    }
    return result.destroy();
  }

  @SuppressWarnings("unchecked")
  public static <V> IList<V> empty() {
    return (IList<V>) EMPTY_REF;
  }

  @SafeVarargs
  public static <V> IList<V> of(final V... values) {
    if (values == null) {
      return IList.empty();
    }
    return wrap(values);
  }

  @Unsafe
  public static <V> IList<V> wrap(final Object[] data) {
    return data == null || data.length == 0 ? empty() : new IList<>(data);
  }

  private final int hashCode;

  protected final Object[] data;

  @Unsafe
  protected IList(final Object[] data) {
    this.data = data;
    this.hashCode = Hash.array(this.data, this.data.length, 0);
  }

  @Override
  public Collection<V> asCollection() {
    return VectorUtils.wrapToCollection(this);
  }

  @Override
  public boolean contains(final V value) {
    return ArrayUtils.contains(this.data, value, this.data.length, 0);
  }

  @Override
  public boolean equals(final Object obj) {
    return VectorUtils.equals(this, obj);
  }

  @Override
  @SuppressWarnings("unchecked")
  public V first() {
    return this.data.length == 0 ? null : (V) this.data[0];
  }

  @SuppressWarnings("unchecked")
  public V get(final int pos) {
    return (V) this.data[pos];
  }

  @Override
  public int hashCode() {
    return this.hashCode;
  }

  public int indexOf(final V value) {
    return IterableUtils.indexOf(this, value);
  }

  @Override
  public boolean isEmpty() {
    return this.data.length == 0;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Iterator<V> iterator() {
    return ArrayUtils.iterator((V[]) this.data, this.data.length, 0);
  }

  @SuppressWarnings("unchecked")
  public V last() {
    return this.data.length == 0 ? null : (V) this.data[this.data.length - 1];
  }

  @Copy
  @SuppressWarnings("unchecked")
  public <R, E extends Throwable> IList<R> map(final Lambda.Function<V, R, E> mapper) throws E {
    if (this.isEmpty()) {
      return IList.<R>empty();
    }
    final var result = new Object[this.data.length];
    for (int i = 0; i < this.data.length; i++) {
      result[i] = mapper.invoke((V) this.data[i]);
    }
    return new IList<>(result);
  }

  @Copy
  public IList<V> reverse() {
    if (this.isEmpty()) {
      return this;
    }
    final var result = new Object[this.data.length];
    ArrayUtils.reverse(this.data, result, this.data.length);
    return wrap(result);
  }

  @Override
  public int size() {
    return this.data.length;
  }

  @Copy
  @SuppressWarnings("unchecked")
  public IList<V> sort(final Comparator<V> comparator) {
    if (this.isEmpty()) {
      return this;
    }
    final Object[] result = new Object[this.data.length];
    System.arraycopy(this.data, 0, result, 0, this.data.length);
    ArrayUtils.sort(result, result.length, 0, (Comparator<Object>) comparator);
    return wrap(result);
  }

  @Override
  public Spliterator<V> spliterator() {
    return Spliterators.spliterator(this.iterator(), this.size(),
      Spliterator.ORDERED | Spliterator.SIZED | Spliterator.IMMUTABLE);
  }

  @Override
  public Stream<V> stream() {
    return StreamSupport.stream(this.spliterator(), false);
  }

  @Copy
  @Override
  public Object[] toArray() {
    final Object[] result = new Object[this.data.length];
    System.arraycopy(this.data, 0, result, 0, this.data.length);
    return result;
  }

  public <T> T[] toArray(final Function<Integer, T[]> arrayFactory) {
    final T[] result = arrayFactory.apply(this.data.length);
    System.arraycopy(this.data, 0, result, 0, this.data.length);
    return result;
  }

  @Override
  public IList<V> toIList() {
    return this;
  }

  public ISet<V> toISet() {
    return ISet.copy(this);
  }

  @SuppressWarnings("unchecked")
  public List<V> toList() {
    return (List<V>) Arrays.asList(this.toArray());
  }

  public LinkedHashSet<V> toSet() {
    final LinkedHashSet<V> result = new LinkedHashSet<>(this.size());
    result.addAll(this.asCollection());
    return result;
  }

  @Override
  public String toString() {
    return IterableUtils.toString(this);
  }

}
