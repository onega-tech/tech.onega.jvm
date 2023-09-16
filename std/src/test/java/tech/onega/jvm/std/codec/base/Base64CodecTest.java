package tech.onega.jvm.std.codec.base;

import org.testng.annotations.Test;
import tech.onega.jvm.std.struct.bytes.IBytes;
import tech.onega.jvm.std.validate.Check;

public class Base64CodecTest {

  @Test
  public void testEncodeDecode() throws Exception {
    final var data = "The quick brown fox jumps over the lazy dog.";
    final var base64 = "VGhlIHF1aWNrIGJyb3duIGZveCBqdW1wcyBvdmVyIHRoZSBsYXp5IGRvZy4=";
    final var hashEnc = Base64Codec.encode(IBytes.of(data), false);
    Check.equals(hashEnc.toString("utf8"), base64);
    Check.equals(Base64Codec.decode(hashEnc, false).toString("utf8"), data);
  }

}
