package tech.onega.jvm.std.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import tech.onega.jvm.std.annotation.Copy;
import tech.onega.jvm.std.annotation.Immutable;
import tech.onega.jvm.std.lang.Equals;
import tech.onega.jvm.std.struct.bytes.IBytes;
import tech.onega.jvm.std.struct.uuid.UUIDUtils;

@Immutable
final public class BigNumber {

  public static final BigNumber ZERO = new BigNumber(BigDecimal.ZERO);

  public static final BigNumber ONE = new BigNumber(BigDecimal.ONE);

  public static BigNumber fromHex(final String hex) {
    return of(new BigInteger(hex, 16));
  }

  public static BigNumber of(final BigDecimal value) {
    return new BigNumber(value);
  }

  public static BigNumber of(final BigInteger value) {
    return of(new BigDecimal(value));
  }

  public static BigNumber of(final int value) {
    return of(new BigDecimal(value));
  }

  @JsonCreator
  public static BigNumber of(final String value) {
    return of(new BigDecimal(value));
  }

  public static BigNumber of(final UUID value) {
    final var bi = UUIDUtils.toBigInteger(value);
    final var bd = new BigDecimal(bi);
    return of(bd);
  }

  private final BigDecimal value;

  private BigNumber(final BigDecimal value) {
    this.value = value;
  }

  @Override
  public boolean equals(final Object obj) {
    return Equals.check(this, obj, f -> new Object[] { f.value });
  }

  /**
   * The integer digits is the number of digits to the left of the decimal point.
   */
  public int getIntegerDigits() {
    return value.signum() == 0 ? 1 : value.precision() - value.scale();
  }

  public int getPrecision() {
    return value.precision();
  }

  /**
   * The scale is the number of digits to the right of the decimal point.
   */
  public int getScale() {
    return value.scale();
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Copy
  public BigNumber increment() {
    return of(value.add(BigDecimal.ONE));
  }

  public boolean isNegative() {
    return value.signum() == -1;
  }

  public boolean isPositive() {
    return value.signum() == 1;
  }

  public boolean isZero() {
    return value.signum() == 0;
  }

  public BigDecimal toBigDecimal() {
    return value;
  }

  public BigInteger toBigInteger() {
    return value.toBigInteger();
  }

  public IBytes toBytes() {
    return IBytes.wrap(value.unscaledValue().toByteArray());
  }

  public String toHex() {
    return value.unscaledValue().toString(16);
  }

  @JsonValue
  public String toJsonValue() {
    return value.toPlainString();
  }

  @Override
  public String toString() {
    return value.toPlainString();
  }

  public UUID toUUID() {
    return UUIDUtils.fromBigInteger(toBigInteger());
  }

}
