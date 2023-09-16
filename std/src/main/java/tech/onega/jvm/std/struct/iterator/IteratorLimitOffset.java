package tech.onega.jvm.std.struct.iterator;

import java.util.Iterator;

final public class IteratorLimitOffset<T> implements Iterator<T> {

  private final Iterator<T> iterator;

  private final int limit;

  private final int offset;

  private int pos;

  public IteratorLimitOffset(final Iterator<T> iterator, final int limit, final int offset) {
    this.iterator = iterator;
    this.limit = limit;
    this.offset = offset;
    IteratorUtils.skip(iterator, offset);
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

  public int offset() {
    return offset;
  }

}
