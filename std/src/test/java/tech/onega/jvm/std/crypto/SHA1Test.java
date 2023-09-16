package tech.onega.jvm.std.crypto;

import org.testng.annotations.Test;
import tech.onega.jvm.std.codec.hex.HexCodec;
import tech.onega.jvm.std.struct.bytes.IBytes;
import tech.onega.jvm.std.validate.Check;

public class SHA1Test {

  @Test
  public void testDigest() throws Exception {
    final var data = "The quick brown fox jumps over the lazy dog.";
    final var hash = "408d94384216f890ff7a0c3528e8bed1e0b01621";
    Check.equals(HexCodec.encode(SHA1.digest(IBytes.of(data))), hash);
  }

}
