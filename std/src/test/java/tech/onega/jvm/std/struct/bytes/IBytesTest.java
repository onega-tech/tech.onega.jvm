package tech.onega.jvm.std.struct.bytes;

import java.math.BigInteger;
import org.testng.annotations.Test;
import tech.onega.jvm.std.lang.RandUtils;
import tech.onega.jvm.std.validate.Check;

public class IBytesTest {

  @Test
  public void testFromBigInteger() {
    for (var i = 0; i < 100; i++) {
      final var bigInt = new BigInteger(RandUtils.randBytes(32));
      final var bytes = IBytes.of(bigInt);
      Check.equals(bigInt, bytes.toBigInteger());
    }
  }

  @Test
  public void testFromHex() {
    final var src = IBytes.wrap(new byte[] { 1, 2, 3, 4 });
    final var hex = src.toHEX();
    final var from = IBytes.fromHEX(hex);
    Check.equals(src, from);
  }

  @Test
  public void testOfStream() {
    final var source = IBytes.of("test data");
    final var copy = IBytes.read(source.asReader().asInputStream());
    Check.equals(source.toString(), copy.toString());
  }

}
