package tech.onega.jvm.std.struct.buffer;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Stream;
import tech.onega.jvm.std.annotation.Copy;
import tech.onega.jvm.std.annotation.Self;
import tech.onega.jvm.std.struct.stream.StreamUtils;

final public class RingBuffer<V> implements Iterable<V> {

  private static class RingBufferIterator<V> implements Iterator<V> {

    private final Object[] data;

    private final int size;

    private final int first;

    private int position;

    public RingBufferIterator(final Object[] data, final int first, final int size) {
      this.position = 0;
      this.data = data;
      this.size = size;
      this.first = first;
    }

    @Override
    public boolean hasNext() {
      return position < size;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      return (V) data[(position++ + first) % data.length];
    }

  }

  public static <V> RingBuffer<V> create(final int capacity) {
    return new RingBuffer<>(capacity);
  }

  @SafeVarargs
  public static <V> RingBuffer<V> of(final int capacity, final V... values) {
    return new RingBuffer<V>(capacity).addAll(values);
  }

  private final Object[] data;

  private int first;

  private int last;

  private int size;

  private RingBuffer(final int capacity) {
    this.first = -1;
    this.last = -1;
    data = new Object[capacity];
    size = 0;
  }

  @Self
  public RingBuffer<V> add(final V value) {
    if (data.length == 0) {
      return this;
    }
    else if (size < data.length) {
      size++;
    }
    if (size == 1) {
      first = 0;
      last = 0;
    }
    else {
      last = (last + 1) % data.length;
      if (last == first) {
        first = (first + 1) % data.length;
      }
    }
    data[last] = value;
    return this;
  }

  @SuppressWarnings("unchecked")
  @Self
  public RingBuffer<V> addAll(final V... values) {
    for (final V value : values) {
      add(value);
    }
    return this;
  }

  public RingBuffer<V> addArray(final V[] values) {
    for (final V value : values) {
      add(value);
    }
    return this;
  }

  public int capacity() {
    return data.length;
  }

  public void clear() {
    size = 0;
    first = -1;
    last = -1;
  }

  @SuppressWarnings("unchecked")
  public V first() {
    return (V) data[first];
  }

  public int firstIndex() {
    return first;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public boolean isFull() {
    return data.length == size;
  }

  public boolean isNotEmpty() {
    return size > 0;
  }

  @Override
  public Iterator<V> iterator() {
    return new RingBufferIterator<>(data, first, size);
  }

  @SuppressWarnings("unchecked")
  public V last() {
    return (V) data[last];
  }

  public int lastIndex() {
    return last;
  }

  public int size() {
    return size;
  }

  public Stream<V> stream() {
    return StreamUtils.createStream(this);
  }

  @Copy
  public Object[] toArray() {
    final Object[] result = new Object[size];
    int i = 0;
    for (final V v : this) {
      result[i++] = v;
    }
    return result;
  }

  public V[] toArray(final Function<Integer, V[]> arrayFactory) {
    final V[] result = arrayFactory.apply(data.length);
    int i = 0;
    for (final V v : this) {
      result[i++] = v;
    }
    return result;
  }

}