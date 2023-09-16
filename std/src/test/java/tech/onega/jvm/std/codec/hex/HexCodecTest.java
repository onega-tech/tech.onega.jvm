package tech.onega.jvm.std.codec.hex;

import org.testng.annotations.Test;
import tech.onega.jvm.std.lang.RandUtils;
import tech.onega.jvm.std.validate.Check;

public class HexCodecTest {

  @Test
  public void testEncodeDecode() {
    for (var k = 0; k < 100; k++) {
      final var data = RandUtils.randIBytes(64);
      final var hex = HexCodec.encode(data);
      Check.equals(HexCodec.decode(hex), data);
    }
  }

}
