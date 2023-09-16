package tech.onega.jvm.std.math;

import java.util.UUID;
import org.testng.annotations.Test;
import tech.onega.jvm.std.validate.Check;

public class BigNumberTest {

  @Test
  public void testGetIntegerDigits() {
    Check.equals(BigNumber.ZERO.getIntegerDigits(), 1);
    Check.equals(BigNumber.of("100").getIntegerDigits(), 3);
    Check.equals(BigNumber.of("100.123").getIntegerDigits(), 3);
    Check.equals(BigNumber.of("-100.123").getIntegerDigits(), 3);
  }

  @Test
  public void testGetScale() {
    Check.equals(BigNumber.ZERO.getScale(), 0);
    Check.equals(BigNumber.of("100").getScale(), 0);
    Check.equals(BigNumber.of("100.123").getScale(), 3);
    Check.equals(BigNumber.of("-100.123").getScale(), 3);
  }

  @Test
  public void testIsNegative() {
    Check.isFalse(BigNumber.ZERO.isNegative());
    Check.isFalse(BigNumber.of("100").isNegative());
    Check.isTrue(BigNumber.of("-100").isNegative());
  }

  @Test
  public void testIsPositive() {
    Check.isFalse(BigNumber.ZERO.isPositive());
    Check.isTrue(BigNumber.of("100").isPositive());
    Check.isFalse(BigNumber.of("-100").isPositive());
  }

  @Test
  public void testIsZero() {
    Check.isTrue(BigNumber.ZERO.isZero());
    Check.isFalse(BigNumber.of("100").isZero());
    Check.isFalse(BigNumber.of("-100").isZero());
  }

  @Test
  public void toHex() {
    for (var i = 0; i < 100; i++) {
      final var number = BigNumber.of(UUID.randomUUID());
      Check.equals(number, BigNumber.fromHex(number.toHex()));
    }
  }

}
