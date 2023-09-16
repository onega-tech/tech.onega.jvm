package tech.onega.jvm.std.struct.array;

import java.util.ArrayList;
import org.testng.annotations.Test;
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.validate.Check;

public class ArrayUtilsTest {

  @Test
  public void testCopyTo() {
    final var dest = new ArrayList<>();
    ArrayUtils.copyTo(new Double[] { 1D, 2D, 3D }, 2, 1, dest);
    Check.equals(dest, ArrayUtils.asList(2D, 3D));
  }

  @Test
  public void testFilter() {
    Check.equals(
      ArrayUtils.filter(new Integer[] { 1, 2, 3 }, 3, 0, v -> v > 2),
      new Integer[] { 3 });
  }

  @Test
  public void testInstanceOf() {
    Check.isTrue(new Object[0] instanceof Object[]);
    Check.isTrue(new Integer[0] instanceof Object[]);
  }

  @Test
  public void testMap() {
    final var data = new Integer[] { 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 };
    final var result = new Integer[] { 0, 0, 0, 0, 0 };
    ArrayUtils.<Integer, Integer, RuntimeException>map(data, 3, 1, result, 1, a -> a * 2);
    Check.equals(result, new Integer[] { 0, 9 * 2, 8 * 2, 7 * 2, 0 });
  }

  @Test
  public void testMerge() {
    final var data = ArrayUtils.merge(new Object[] { 1, 2, 3 }, IList.of(23, 34), ArrayUtils.asList(56, 89), 0,
      12);
    Check.equals(data, new Object[] { 1, 2, 3, 23, 34, 56, 89, 0, 12 });
  }

  @Test
  public void testReverse() {
    final var data = new Object[] { 1, 2, 3, 4, 5 };
    final var result = new Object[5];
    ArrayUtils.reverse(data, result, 3);
    Check.equals(result, new Object[] { 3, 2, 1, null, null });
  }

  @Test
  public void testReverseSelf() {
    final var data = new Object[] { 1, 2, 3, 4, 5 };
    ArrayUtils.reverse(data, 3);
    Check.equals(data, new Object[] { 3, 2, 1, 4, 5 });
  }

  @Test
  public void testSort() {
    final var data = new Integer[] { 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 };
    ArrayUtils.<Integer>sort(data, 3, 1, Integer::compare);
    Check.equals(data, new Integer[] { 10, 7, 8, 9, 6, 5, 4, 3, 2, 1 });
  }

}
