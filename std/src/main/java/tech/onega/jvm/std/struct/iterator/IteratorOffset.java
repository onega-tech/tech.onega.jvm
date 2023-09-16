package tech.onega.jvm.std.struct.iterator;

import java.util.Iterator;

final public class IteratorOffset<T> implements Iterator<T> {

  private final Iterator<T> iterator;

  private final int offset;

  public IteratorOffset(final Iterator<T> iterator, final int offset) {
    this.iterator = iterator;
    this.offset = offset;
    IteratorUtils.skip(iterator, offset);
  }

  @Override
  public boolean hasNext() {
    return iterator.hasNext();
  }

  @Override
  public T next() {
    return iterator.next();
  }

  public int offset() {
    return offset;
  }

}
