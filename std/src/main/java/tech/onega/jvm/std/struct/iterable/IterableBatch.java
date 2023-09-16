package tech.onega.jvm.std.struct.iterable;

import java.util.Iterator;
import tech.onega.jvm.std.annotation.Immutable;
import tech.onega.jvm.std.annotation.NotThreadSafe;
import tech.onega.jvm.std.annotation.ThreadSafe;

@Immutable
@ThreadSafe
final public class IterableBatch<T> implements Iterable<T> {

  @NotThreadSafe
  public static class BatchIterator<T> implements Iterator<T> {

    private final int batchSize;

    private final Loader<T> loader;

    private int offset;

    private Iterator<T> batchIterator;

    public BatchIterator(final int batchSize, final int startOffset, final Loader<T> loader) {
      this.offset = startOffset;
      this.batchSize = batchSize;
      this.loader = loader;
    }

    @Override
    public boolean hasNext() {
      if (batchIterator == null || !batchIterator.hasNext()) {
        try {
          batchIterator = loader.load(batchSize, offset);
          if (batchIterator == null || !batchIterator.hasNext()) {
            return false;
          }
          offset = offset + batchSize;
        }
        catch (final Exception e) {
          if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
          }
          throw new RuntimeException(e);
        }
      }
      return true;
    }

    @Override
    public T next() {
      return batchIterator.next();
    }

  }

  @FunctionalInterface
  public interface Loader<T> {

    Iterator<T> load(int limit, int offset) throws Exception;

  }

  private final int startOffset;

  private final int batchSize;

  private final Loader<T> loader;

  public IterableBatch(final int startOffset, final int batchSize, final Loader<T> loader) {
    this.startOffset = startOffset;
    this.batchSize = batchSize;
    this.loader = loader;
  }

  @Override
  public Iterator<T> iterator() {
    return new BatchIterator<>(startOffset, batchSize, loader);
  }

}
