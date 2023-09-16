package tech.onega.jvm.std.codec.lzz;

import org.testng.annotations.Test;
import tech.onega.jvm.std.lang.RandUtils;
import tech.onega.jvm.std.validate.Check;

class LzzCodecTest {

  @Test
  void test() {
    final var data = RandUtils.randIBytes(10240);
    final var compressed = LzzCodec.encode(data);
    final var decompressed = LzzCodec.decode(compressed);
    Check.notEquals(data, compressed);
    Check.equals(data, decompressed);
  }

}
