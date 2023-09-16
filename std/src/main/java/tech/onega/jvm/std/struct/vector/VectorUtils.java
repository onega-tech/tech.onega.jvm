package tech.onega.jvm.std.struct.vector;

import java.util.Collection;
import java.util.Iterator;
import tech.onega.jvm.std.struct.hash.Hash;
import tech.onega.jvm.std.struct.iterable.IterableUtils;

final public class VectorUtils {

  public static final int DEFAULT_INITIAL_CAPACITY = 16;

  public static final int DEFAULT_MAX_CAPACITY = Integer.MAX_VALUE - 8;

  public static boolean equals(final Object objectA, final Object objectB) {
    if (objectA == objectB) {
      return true;
    }
    else if (objectA == null || objectB == null || !(objectA instanceof Vector) || !(objectB instanceof Vector)) {
      return false;
    }
    final Vector<?> vectorA = (Vector<?>) objectA;
    final Vector<?> vectorB = (Vector<?>) objectB;
    if (vectorA.size() != vectorB.size()) {
      return false;
    }
    return IterableUtils.equals(vectorA, vectorB);
  }

  public static <V> Collection<V> wrapToCollection(final Vector<V> vector) {
    return new Collection<>() {

      @Override
      public boolean add(final V value) {
        getMutable().add(value);
        return true;
      }

      @Override
      public boolean addAll(final Collection<? extends V> values) {
        getMutable().addAll(values);
        return true;
      }

      @Override
      public void clear() {
        getMutable().clear();
      }

      @SuppressWarnings("unchecked")
      @Override
      public boolean contains(final Object value) {
        return vector.contains((V) value);
      }

      @Override
      public boolean containsAll(final Collection<?> value) {
        for (final Object v : value) {
          if (!contains(v)) {
            return false;
          }
        }
        return true;
      }

      public boolean equals(final Object o) {
        if (o instanceof Iterable) {
          return IterableUtils.equals(this, (Iterable<?>) o);
        }
        return false;
      }

      private MVector<V> getMutable() {
        if (!(vector instanceof MVector)) {
          throw new IllegalStateException("Vector not mutable");
        }
        return (MVector<V>) vector;
      }

      public int hashCode() {
        return Hash.iterable(this);
      }

      @Override
      public boolean isEmpty() {
        return vector.isEmpty();
      }

      @Override
      public Iterator<V> iterator() {
        return vector.iterator();
      }

      @SuppressWarnings("unchecked")
      @Override
      public boolean remove(final Object value) {
        getMutable().remove((V) value);
        return true;
      }

      @SuppressWarnings("unchecked")
      @Override
      public boolean removeAll(final Collection<?> values) {
        final MVector<V> vectorM = getMutable();
        for (final Object v : values) {
          vectorM.remove((V) v);
        }
        return true;
      }

      @Override
      public boolean retainAll(final Collection<?> arg0) {
        return false;
      }

      @Override
      public int size() {
        return vector.size();
      }

      @Override
      public Object[] toArray() {
        return vector.toArray();
      }

      @Override
      public <T> T[] toArray(final T[] array) {
        vector.stream().toArray(size -> array);
        return array;
      }

    };
  }

}
