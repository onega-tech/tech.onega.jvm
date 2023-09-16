package tech.onega.jvm.std.lang;

import java.util.Arrays;
import org.testng.annotations.Test;
import tech.onega.jvm.std.validate.Check;

public class RandUtilsTest {

  @Test
  public void testRandBytesTo() {
    final var buffer = new byte[16];
    RandUtils.randBytesTo(buffer, 4, 0);
    RandUtils.randBytesTo(buffer, 4, 8);
    final var a = Arrays.copyOfRange(buffer, 0, 4);
    final var b = Arrays.copyOfRange(buffer, 4, 8);
    final var c = Arrays.copyOfRange(buffer, 8, 12);
    final var d = Arrays.copyOfRange(buffer, 12, 16);
    Check.equals(b, new byte[] { 0, 0, 0, 0 });
    Check.equals(b, d);
    Check.notEquals(a, b);
    Check.notEquals(a, c);
    Check.notEquals(a, d);
    Check.notEquals(c, b);
    Check.notEquals(c, d);
  }

}
