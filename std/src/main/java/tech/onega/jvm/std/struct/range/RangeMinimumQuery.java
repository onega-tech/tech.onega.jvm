package tech.onega.jvm.std.struct.range;

import java.util.Arrays;
import tech.onega.jvm.std.annotation.Mutable;
import tech.onega.jvm.std.annotation.NotThreadSafe;

@Mutable
@NotThreadSafe
final public class RangeMinimumQuery {

  private int maxValue;

  private final int size;

  private final int[] values;

  private final int[] indexes;

  public RangeMinimumQuery(final int size) {
    this(size, Integer.MAX_VALUE);
  }

  public RangeMinimumQuery(final int size, final int maxValue) {
    this.maxValue = maxValue;
    this.size = size;
    values = new int[2 * size];
    indexes = new int[2 * size];
    Arrays.fill(values, maxValue);
    Arrays.fill(indexes, 0);
    for (int i = 0; i < size; i++) {
      indexes[size + i] = i;
    }
  }

  RangeMinimumQuery(final int[] a) {
    size = a.length;
    values = new int[2 * size + 1];
    indexes = new int[2 * size + 1];
    for (int i = 0; i < size; i++) {
      values[size + i] = a[i];
      indexes[size + i] = i;
    }
    for (int v = size - 1; v > 0; v--) {
      final int l = v << 1;
      final int r = l + 1;
      if (values[l] < values[r]) {
        values[v] = values[l];
        indexes[v] = indexes[l];
      }
      else {
        values[v] = values[r];
        indexes[v] = indexes[r];
      }
    }
  }

  public int get(final int i) {
    return values[i + size];
  }

  public int minIndex() {
    return values[1] < maxValue ? indexes[1] : -1;
  }

  /**
  *
  * Получить номер минимального элемента на отрезке [l, r] (если нет, то -1)
  * @param l
  * @param r
  * @return
  */
  public int minIndex(int l, int r) {
    l += size;
    r += size;
    int min = Integer.MAX_VALUE;
    int result = -1;
    while (l <= r) {
      if ((l & 1) == 1) {
        if (min > values[l]) {
          min = values[l];
          result = indexes[l];
        }
      }
      if ((r & 1) == 0) {
        if (min > values[r]) {
          min = values[r];
          result = indexes[r];
        }
      }
      l = (l + 1) >> 1;
      r = (r - 1) >> 1;
    }
    return result;
  }

  /**
   * Получить значение минимального элемента на отрезке [l, r]
   * @param l
   * @param r
   * @return
   */
  public int minVal(int l, int r) {
    l += size;
    r += size;
    int result = Integer.MAX_VALUE;
    while (l <= r) {
      if ((l & 1) == 1) {
        result = Math.min(result, values[l]);
      }
      if ((r & 1) == 0) {
        result = Math.min(result, values[r]);
      }
      l = (l + 1) >> 1;
      r = (r - 1) >> 1;
    }
    return result;
  }

  public RangeMinimumQuery set(final int index, final int value) {
    final int i = index + size;
    values[i] = value;
    for (int v = i >> 1; v > 0; v >>= 1) {
      final int l = v << 1;
      final int r = l + 1;
      if (values[l] < values[r]) {
        values[v] = values[l];
        indexes[v] = indexes[l];
      }
      else {
        values[v] = values[r];
        indexes[v] = indexes[r];
      }
    }
    return this;
  }

}