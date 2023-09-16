package tech.onega.jvm.std.struct.iterator;

import java.util.Iterator;
import java.util.function.Function;

final public class IteratorMap<P, R> implements Iterator<R> {

  private final Iterator<P> iterator;

  private final Function<P, R> mapper;

  public IteratorMap(final Iterator<P> iterator, final Function<P, R> func) {
    this.iterator = iterator;
    this.mapper = func;
  }

  @Override
  public boolean hasNext() {
    return iterator.hasNext();
  }

  @Override
  public R next() {
    return mapper.apply(iterator.next());
  }

}
