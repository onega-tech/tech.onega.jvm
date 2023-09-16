package tech.onega.jvm.std.struct.iterator;

import java.util.Iterator;

final public class IteratorConcatA<T> implements Iterator<T> {

  private final Iterator<T>[] iterators;

  private int index;

  @SafeVarargs
  public IteratorConcatA(final Iterator<T>... iterators) {
    this.iterators = iterators;
    this.index = 0;
  }

  @Override
  public boolean hasNext() {
    if (iterators.length == 0) {
      return false;
    }
    while (index < iterators.length) {
      if (iterators[index].hasNext()) {
        return true;
      }
      index++;
    }
    return false;
  }

  @Override
  public T next() {
    return iterators[index].next();
  }

}
