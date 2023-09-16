package tech.onega.jvm.std.crypto;

import org.testng.annotations.Test;
import tech.onega.jvm.std.codec.hex.HexCodec;
import tech.onega.jvm.std.struct.bytes.IBytes;
import tech.onega.jvm.std.validate.Check;

public class MD5Test {

  @Test
  public void testDigest() throws Exception {
    final var data = "The quick brown fox jumps over the lazy dog.";
    final var hash = "e4d909c290d0fb1ca068ffaddf22cbd0";
    Check.equals(HexCodec.encode(MD5.digest(IBytes.of(data))), hash);
  }

}
