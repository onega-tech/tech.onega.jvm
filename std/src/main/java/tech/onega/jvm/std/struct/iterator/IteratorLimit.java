package tech.onega.jvm.std.struct.iterator;

import java.util.Iterator;

final public class IteratorLimit<T> implements Iterator<T> {

  private final Iterator<T> iterator;

  private final int limit;

  private int pos;

  public IteratorLimit(final Iterator<T> iterator, final int limit) {
    this.iterator = iterator;
    this.limit = limit;
    this.pos = 0;
  }

  @Override
  public boolean hasNext() {
    return (limit >= 0) ? (pos < limit && iterator.hasNext()) : iterator.hasNext();
  }

  public int limit() {
    return limit;
  }

  @Override
  public T next() {
    pos++;
    return iterator.next();
  }

}
