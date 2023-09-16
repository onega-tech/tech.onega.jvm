package tech.onega.jvm.std.struct.list;

import java.util.Arrays;
import java.util.Iterator;
import tech.onega.jvm.std.annotation.Mutable;
import tech.onega.jvm.std.annotation.NotThreadSafe;

/**
 * http://neerc.secna.ru/Algor/algo_base_ds_lists.html
 *
 * Мультисписки удобны для хранения графов.
 * Список-списков  - связанный список с несколькими головами
 */
@Mutable
@NotThreadSafe
final public class MultiListInt {

  private final int[] lists;

  private final int[] next;

  private final int[] values;

  private final int[] listsSize;

  private int cnt = 1;

  public MultiListInt(final int listCapacity, final int valuesCapacity) {
    lists = new int[listCapacity];
    listsSize = new int[listCapacity];
    next = new int[valuesCapacity + 1];
    values = new int[valuesCapacity + 1];
  }

  public MultiListInt addLast(final int list, final int value) {
    next[cnt] = lists[list];
    values[cnt] = value;
    lists[list] = cnt++;
    listsSize[list]++;
    return this;
  }

  public Iterable<Integer> indexes(final int list) {
    return () -> new Iterator<>() {

      private int i = lists[list];

      @Override
      public boolean hasNext() {
        return i != 0;
      }

      @Override
      public Integer next() {
        final int r = i;
        i = next[i];
        return r;
      }

    };
  }

  public int[] list(final int list) {
    final int[] result = new int[listsSize[list]];
    int z = result.length;
    for (int i = lists[list]; i != 0; i = next[i]) {
      result[--z] = values[i];
    }
    return result;
  }

  public int size(final int list) {
    return listsSize[list];
  }

  @Override
  public String toString() {
    return new StringBuilder(1024)
      .append("list:").append(Arrays.toString(lists)).append('\n')
      .append("next:").append(Arrays.toString(next)).append('\n')
      .append("data:").append(Arrays.toString(values))
      .toString();
  }

  public int value(final int index) {
    return values[index];
  }

  public int value(final int list, final int index) {
    int z = listsSize[list] - index;
    for (int i = lists[list]; i != 0; i = next[i]) {
      if (--z == 0) {
        return values[i];
      }
    }
    throw new ArrayIndexOutOfBoundsException();
  }

  public Iterable<Integer> values(final int list) {
    return () -> new Iterator<>() {

      private int i = lists[list];

      @Override
      public boolean hasNext() {
        return i != 0;
      }

      @Override
      public Integer next() {
        final int r = values[i];
        i = next[i];
        return r;
      }

    };
  }

}
