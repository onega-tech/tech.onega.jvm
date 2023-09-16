package tech.onega.jvm.std.crypto;

import org.testng.annotations.Test;
import tech.onega.jvm.std.codec.hex.HexCodec;
import tech.onega.jvm.std.lang.RandUtils;
import tech.onega.jvm.std.struct.bytes.IBytes;
import tech.onega.jvm.std.validate.Check;

public class SHA3Test {

  private static final byte[] V = { 104, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100 };

  private static final String HEX_224 = "dfb7f18c77e928bb56faeb2da27291bd790bc1045cde45f3210bb6c5";

  private static final String HEX_256 = "644bcc7e564373040999aac89e7622f3ca71fba1d972fd94a31c3bfbf24e3938";

  private static final String HEX_384 = "83bff28dde1b1bf5810071c6643c08e5b05bdb836effd70b403ea8ea0a634dc4997eb1053aa3593f590f9c63630dd90b";

  private static final String HEX_512 = "840006653e9ac9e95117a15c915caab81662918e925de9e004f774ff82d7079a40d4d27b1b372657c61d46d470304c88c788b3a4527ad074d1dccbee5dbaa99a";

  private static final byte[] B_224 = {
    -33, -73, -15, -116,
    119, -23, 40, -69,
    86, -6, -21, 45,
    -94, 114, -111, -67,
    121, 11, -63, 4,
    92, -34, 69, -13,
    33, 11, -74, -59
  };

  private static final byte[] B_256 = {
    100, 75, -52, 126,
    86, 67, 115, 4,
    9, -103, -86, -56,
    -98, 118, 34, -13,
    -54, 113, -5, -95,
    -39, 114, -3, -108,
    -93, 28, 59, -5,
    -14, 78, 57, 56
  };

  private static final byte[] B_384 = {
    -125, -65, -14, -115,
    -34, 27, 27, -11,
    -127, 0, 113, -58,
    100, 60, 8, -27,
    -80, 91, -37, -125,
    110, -1, -41, 11,
    64, 62, -88, -22,
    10, 99, 77, -60,
    -103, 126, -79, 5,
    58, -93, 89, 63,
    89, 15, -100, 99,
    99, 13, -39, 11
  };

  private static final byte[] B_512 = {
    -124, 0, 6, 101,
    62, -102, -55, -23,
    81, 23, -95, 92,
    -111, 92, -86, -72,
    22, 98, -111, -114,
    -110, 93, -23, -32,
    4, -9, 116, -1,
    -126, -41, 7, -102,
    64, -44, -46, 123,
    27, 55, 38, 87,
    -58, 29, 70, -44,
    112, 48, 76, -120,
    -57, -120, -77, -92,
    82, 122, -48, 116,
    -47, -36, -53, -18,
    93, -70, -87, -102
  };

  @Test
  public void testDigest() {
    final var secret = IBytes.wrap(new byte[] { 83, 101, 99, 114, 101, 116 });
    for (var byteLength = 64; byteLength <= 1024; byteLength += 64) {
      final var bits = byteLength * 8;
      final var hash = SHA3.digest(bits, secret);
      Check.notEquals(secret, hash);
      Check.equals(byteLength, hash.length());
    }
  }

  @Test
  public void testEncodeDecode() {
    this.testEncodeDecodeImpl(224, V, B_224, HEX_224);
    this.testEncodeDecodeImpl(256, V, B_256, HEX_256);
    this.testEncodeDecodeImpl(384, V, B_384, HEX_384);
    this.testEncodeDecodeImpl(512, V, B_512, HEX_512);
  }

  private void testEncodeDecodeImpl(final int bits, final byte[] v, final byte[] bytes, final String hex) {
    final var hash = SHA3.digest(bits, IBytes.wrap(v));
    Check.equals(hash.toArray(), bytes);
    Check.equals(HexCodec.encode(hash), hex);
  }

  @Test
  public void testLarge() {
    final var secret = RandUtils.randIBytes(100_000);
    final var hash = SHA3.digest(64, secret, 10_000);
    Check.equals(hash.length(), 64 / 8);
  }

}
