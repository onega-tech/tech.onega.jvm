package tech.onega.jvm.std.lang;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Random;
import java.util.UUID;
import tech.onega.jvm.std.math.BigNumber;
import tech.onega.jvm.std.struct.bytes.IBytes;
import tech.onega.jvm.std.struct.date.DateTime;

final public class RandUtils {

  private static final Random RANDOM = new SecureRandom();

  public static BigNumber randBigNumberPositive() {
    return BigNumber.of(randInt(1, Integer.MAX_VALUE));
  }

  public static IBytes randBits(final int bits) {
    return randIBytes(bits / 8);
  }

  public static boolean randBoolean() {
    return randInt(0, 1) == 1;
  }

  public static byte[] randBytes(final int length) {
    final byte[] result = new byte[length];
    if (length > 0) {
      RANDOM.nextBytes(result);
    }
    return result;
  }

  public static void randBytesTo(final byte[] buffer, final int limit, final int offset) {
    for (int i = offset; i < offset + limit; i++) {
      buffer[i] = (byte) randInt(-128, 127);
    }
  }

  public static char randChar() {
    return (char) randInt();
  }

  public static DateTime randDateTime() {
    return DateTime.ofTimestampMillis(randLong(0, DateTime.now().toUTCTimestampMilli()));
  }

  public static double randDouble() {
    return RANDOM.nextDouble();
  }

  public static Duration randDuration() {
    return Duration.ofMillis(RandUtils.randLong(0, Duration.ofDays(365).toMillis()));
  }

  public static <T extends Enum<?>> T randEnum(final Class<T> enumType) {
    final int x = RANDOM.nextInt(enumType.getEnumConstants().length);
    return enumType.getEnumConstants()[x];
  }

  public static float randFloat() {
    return RANDOM.nextFloat();
  }

  public static IBytes randIBytes(final int length) {
    return IBytes.wrap(randBytes(length));
  }

  public static int randInt() {
    return RANDOM.nextInt();
  }

  public static int randInt(final int minInclusive, final int maxInclusive) {
    final int range = maxInclusive - minInclusive + 1;
    final int fraction = (int) (range * RANDOM.nextDouble());
    return fraction + minInclusive;
  }

  public static long randLong() {
    return RANDOM.nextLong();
  }

  public static long randLong(final long minInclusive, final long maxInclusive) {
    final long range = maxInclusive - minInclusive + 1;
    final long fraction = (long) (range * RANDOM.nextDouble());
    return fraction + minInclusive;
  }

  public static String randString() {
    return randString(60, 32);
  }

  public static String randString(final int bits, final int radix) {
    return new BigInteger(bits, RANDOM).toString(radix);
  }

  public static String randString(final String prefix) {
    return new StringBuilder(32 + prefix.length())
      .append(prefix)
      .append('_')
      .append(randString())
      .toString();
  }

  public static String randStringWithPostfix(final String postfix) {
    return new StringBuilder(32 + postfix.length())
      .append(randString())
      .append(postfix)
      .toString();
  }

  public static UUID randUUID() {
    return UUID.randomUUID();
  }

}
