package tech.onega.jvm.std.codec.hex;

import tech.onega.jvm.std.struct.bytes.IBytes;

final public class HexCodec {

  public static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
    'f' };

  public static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
    'F' };

  public static byte[] decode(final char[] data) {
    final var len = data.length;
    if ((len & 0x01) != 0) {
      throw new IllegalArgumentException("Odd number of characters.");
    }
    final var out = new byte[len / 2];
    for (var i = 0; i < len; i += 2) {
      final var cH = data[i];
      final var cL = data[i + 1];
      final var h = ('0' <= cH && cH <= '9') ? cH - '0'
        : ('A' <= cH && cH <= 'F') ? cH - 'A' + 10 : ('a' <= cH && cH <= 'f') ? cH - 'a' + 10 : -1;
      final var l = ('0' <= cL && cL <= '9') ? cL - '0'
        : ('A' <= cL && cL <= 'F') ? cL - 'A' + 10 : ('a' <= cL && cL <= 'f') ? cL - 'a' + 10 : -1;
      out[i / 2] = (byte) (h * 16 + l);
    }
    return out;
  }

  public static IBytes decode(final String hex) {
    return IBytes.wrap(decode(hex.toCharArray()));
  }

  public static byte[] decodeAsBytes(final String hex) {
    return decode(hex.toCharArray());
  }

  public static String encode(final byte[] data) {
    return new String(encode(data, DIGITS_LOWER));
  }

  public static char[] encode(final byte[] data, final char[] toDigits) {
    final int l = data.length;
    final char[] out = new char[l << 1];
    for (int i = 0, j = 0; i < l; i++) {
      out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
      out[j++] = toDigits[0x0F & data[i]];
    }
    return out;
  }

  public static String encode(final IBytes data) {
    return new String(encode(data.toArray(), DIGITS_LOWER));
  }

}
