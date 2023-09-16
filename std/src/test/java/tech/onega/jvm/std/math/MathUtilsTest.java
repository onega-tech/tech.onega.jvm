package tech.onega.jvm.std.math;

import org.testng.annotations.Test;
import tech.onega.jvm.std.validate.Check;

public class MathUtilsTest {

  @Test
  public void testLog2() {
    Check.isTrue(MathUtils.log2(0) == 0);
    Check.isTrue(MathUtils.log2(8) == 3);
    Check.isTrue(MathUtils.log2(16) == 4);
    Check.isTrue(MathUtils.log2(24) == 4);
    Check.isTrue(MathUtils.log2(32) == 5);
    Check.isTrue(MathUtils.log2(40) == 5);
    Check.isTrue(MathUtils.log2(48) == 5);
    Check.isTrue(MathUtils.log2(56) == 5);
  }

  @Test
  public void testPow() {
    Check.isTrue(MathUtils.pow(0, 0) == 1);
    Check.isTrue(MathUtils.pow(3, 3) == 27);
    Check.isTrue(MathUtils.pow(6, 6) == 46656);
    Check.isTrue(MathUtils.pow(9, 9) == 387420489);
  }

  @Test
  public void testPow2() {
    Check.isTrue(MathUtils.pow2(0) == 1);
    Check.isTrue(MathUtils.pow2(4) == 16);
    Check.isTrue(MathUtils.pow2(8) == 256);
  }

}
