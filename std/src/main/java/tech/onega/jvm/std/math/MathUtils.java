package tech.onega.jvm.std.math;

final public class MathUtils {

  public static boolean inRange(final int value, final int fromInclusive, final int toInclusive) {
    return value >= fromInclusive && value <= toInclusive;
  }

  public static int log2(int value) {
    int log = 0;
    if ((value & 0xffff0000) != 0) {
      value >>>= 16;
      log = 16;
    }
    if (value >= 256) {
      value >>>= 8;
      log += 8;
    }
    if (value >= 16) {
      value >>>= 4;
      log += 4;
    }
    if (value >= 4) {
      value >>>= 2;
      log += 2;
    }
    return log + (value >>> 1);
  }

  public static int pow(int value, int pow) {
    int result = 1;
    while (pow > 0) {
      if ((pow & 1) == 1) {
        result *= value;
      }
      pow >>= 1;
      value *= value;
    }
    return result;
  }

  public static int pow2(final int pow) {
    return 1 << pow;
  }

  public static double round(final double v, final double p) {
    final double x = Math.pow(10, p);
    return Math.round(v * x) / x;
  }

}
