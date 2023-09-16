package tech.onega.jvm.std.struct.uuid;

import java.math.BigInteger;
import java.util.UUID;

final public class UUIDUtils {

  private static final BigInteger B = BigInteger.ONE.shiftLeft(64); // 2^64

  private static final BigInteger L = BigInteger.valueOf(Long.MAX_VALUE);

  public static UUID fromBigInteger(final BigInteger value) {
    final BigInteger[] parts = value.divideAndRemainder(B);
    BigInteger hi = parts[0];
    BigInteger lo = parts[1];
    if (L.compareTo(lo) < 0) {
      lo = lo.subtract(B);
    }
    if (L.compareTo(hi) < 0) {
      hi = hi.subtract(B);
    }
    return new UUID(hi.longValueExact(), lo.longValueExact());
  }

  public static BigInteger toBigInteger(final UUID value) {
    BigInteger lo = BigInteger.valueOf(value.getLeastSignificantBits());
    BigInteger hi = BigInteger.valueOf(value.getMostSignificantBits());
    if (hi.signum() < 0) {
      hi = hi.add(B);
    }
    if (lo.signum() < 0) {
      lo = lo.add(B);
    }
    return lo.add(hi.multiply(B));
  }

}
