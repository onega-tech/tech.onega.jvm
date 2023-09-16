package tech.onega.jvm.std.struct.iterator;

import java.util.Iterator;

final public class IteratorConcat<T> implements Iterator<T> {

  private final Iterator<Iterator<T>> iterators;

  private Iterator<T> cursor;

  public IteratorConcat(final Iterator<Iterator<T>> iterators) {
    this.iterators = iterators;
    this.cursor = null;
  }

  @Override
  public boolean hasNext() {
    while (iterators.hasNext()) {
      if (this.cursor == null) {
        this.cursor = iterators.next();
      }
      if (this.cursor.hasNext()) {
        return true;
      }
      else {
        this.cursor = null;
      }
    }
    return false;
  }

  @Override
  public T next() {
    return cursor.next();
  }

}
