package tech.onega.jvm.std.crypto;

import java.util.Arrays;
import org.testng.annotations.Test;
import tech.onega.jvm.std.lang.RandUtils;
import tech.onega.jvm.std.struct.bytes.IBytes;
import tech.onega.jvm.std.validate.Check;

public class NewHopeTest {

  @Test
  public void test() {
    final var iters = 32;
    for (var i = 1; i <= iters; i++) {
      final var saltA = RandUtils.randIBytes(32 * i);
      final var privateA = NewHope.privateA(saltA);
      Check.equals(privateA.length(), 6272);
      final var publicA = NewHope.publicA(privateA);
      Check.equals(publicA.length(), 2176);
      final var saltB = RandUtils.randIBytes(32 * i);
      final var privateB = NewHope.privateB(publicA, saltB);
      Check.equals(privateB.length(), 4224);
      final var publicB = NewHope.publicB(privateB);
      Check.equals(publicB.length(), 4096);
      final var aggregmentB = NewHope.aggregmentB(privateB, publicB);
      final var aggregmentA = NewHope.aggregmentA(privateA, publicB);
      Check.equals(aggregmentA.length(), 128);
      Check.equals(aggregmentB.length(), 128);
      Check.equals(aggregmentA, aggregmentB);
    }
  }

  @Test
  public void test2() {
    final var sha3Bits = 1024;
    final var saltA = new byte[sha3Bits / 8];
    Arrays.fill(saltA, (byte) 1);
    final var saltB = new byte[sha3Bits / 8];
    Arrays.fill(saltB, (byte) 2);
    final var privateA = NewHope.privateA(IBytes.wrap(saltA));
    final var publicA = NewHope.publicA(privateA);
    final var privateB = NewHope.privateB(publicA, IBytes.wrap(saltB));
    final var publicB = NewHope.publicB(privateB);
    final var aggregmentB = NewHope.aggregmentB(privateB, publicB);
    final var aggregmentA = NewHope.aggregmentA(privateA, publicB);
    Check.equals(aggregmentA, aggregmentB);
  }

}
