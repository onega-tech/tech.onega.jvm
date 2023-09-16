package tech.onega.jvm.std.validate;

import javax.validation.constraints.Max;
import org.testng.annotations.Test;
import tech.onega.jvm.std.struct.bytes.IBytes;

class CheckTest {

  private static class InvalidMock {

    @Max(0)
    final int v = 1;

  }

  private static class ValidMock {
  }

  @Test
  void testEquals() {
    Check.equals(new byte[] { 1, 2, 3 }, new byte[] { 1, 2, 3 });
    Check.notEquals(new byte[] { 1, 2 }, new byte[] { 1, 2, 3 });
    Check.equals(IBytes.of(new byte[] { 1, 2, 3 }), IBytes.of(new byte[] { 1, 2, 3 }));
    Check.notEquals(IBytes.of(new byte[] { 1, 2 }), IBytes.of(new byte[] { 1, 2, 3 }));
  }

  @Test
  void testValid() {
    Check.valid(new ValidMock());
    Check.withThrow(() -> Check.valid(new InvalidMock()));
  }

}
