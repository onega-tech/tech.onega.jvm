package tech.onega.jvm.std.lang;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;
import tech.onega.jvm.std.struct.iterator.IteratorUtils;

final public class Equals {

  @SuppressWarnings("unchecked")
  public static <T> boolean check(T a, Object b, Function<T, Object[]> fieldsResolver) {
    if (a == b) {
      return true;
    }
    if (b == null) {
      return false;
    }
    if (!isExtendedFrom(b.getClass(), a.getClass())) {
      return false;
    }
    var fA = fieldsResolver.apply(a);
    var fB = fieldsResolver.apply((T) b);
    return Equals.yes(fA, fB);
  }

  private static boolean isExtendedFrom(final Class<?> parentType, final Class<?> childType) {
    if (parentType == null || childType == null) {
      return false;
    }
    else if (parentType == childType) {
      return true;
    }
    return childType.isAssignableFrom(parentType);
  }

  public static <T> boolean no(final T a, final T b) {
    return !yes(a, b);
  }

  public static boolean yes(
    final byte[] a,
    final int limitA,
    final int offsetA,
    final byte[] b,
    final int limitB,
    final int offsetB) {
    if (a == b) {
      return true;
    }
    else if (a == null || a == null) {
      return false;
    }
    else if (limitA != limitB) {
      return false;
    }
    else {
      for (int i = 0; i < limitA; i++) {
        if (!yes(a[i + offsetA], b[i + offsetB])) {
          return false;
        }
      }
      return true;
    }
  }

  public static boolean yes(
    final Object[] a,
    final int limitA,
    final int offsetA,
    final Object[] b,
    final int limitB,
    final int offsetB) {
    if (a == b) {
      return true;
    }
    else if (a == null || a == null) {
      return false;
    }
    else if (limitA != limitB) {
      return false;
    }
    else {
      for (var i = 0; i < limitA; i++) {
        if (!yes(a[i + offsetA], b[i + offsetB])) {
          return false;
        }
      }
      return true;
    }
  }

  public static <T> boolean yes(final T a, final T b) {
    if (a == b) {
      return true;
    }
    else if (a == null || b == null) {
      return false;
    }
    else if (a instanceof Object[] && b instanceof Object[]) {
      var va = (Object[]) a;
      var vb = (Object[]) b;
      return yes(va, va.length, 0, vb, vb.length, 0);
    }
    else if (a instanceof byte[] && b instanceof byte[]) {
      return Arrays.equals((byte[]) a, (byte[]) b);
    }
    else if (a instanceof long[] && b instanceof long[]) {
      return Arrays.equals((long[]) a, (long[]) b);
    }
    else if (a instanceof int[] && b instanceof int[]) {
      return Arrays.equals((int[]) a, (int[]) b);
    }
    else if (a instanceof float[] && b instanceof float[]) {
      return Arrays.equals((float[]) a, (float[]) b);
    }
    else if (a instanceof short[] && b instanceof short[]) {
      return Arrays.equals((short[]) a, (short[]) b);
    }
    else if (a instanceof char[] && b instanceof char[]) {
      return Arrays.equals((char[]) a, (char[]) b);
    }
    else if (a instanceof boolean[] && b instanceof boolean[]) {
      return Arrays.equals((boolean[]) a, (boolean[]) b);
    }
    else if (a instanceof double[] && b instanceof double[]) {
      return Arrays.equals((double[]) a, (double[]) b);
    }
    else if (a instanceof Iterator && b instanceof Iterator) {
      return IteratorUtils.equals((Iterator<?>) a, (Iterator<?>) b);
    }
    else {
      return a.equals(b);
    }
  }

}
