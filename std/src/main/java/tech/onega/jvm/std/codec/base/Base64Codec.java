package tech.onega.jvm.std.codec.base;

import java.nio.ByteBuffer;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.struct.bytes.IBytes;

@ThreadSafe
final public class Base64Codec {

  private static final java.util.Base64.Encoder ENCODER = java.util.Base64.getEncoder();

  private static final java.util.Base64.Encoder ENCODER_URL_SAFE = java.util.Base64.getUrlEncoder();

  private static final java.util.Base64.Decoder DECODER = java.util.Base64.getDecoder();

  private static final java.util.Base64.Decoder DECODER_URL_SAFE = java.util.Base64.getUrlDecoder();

  public static ByteBuffer decode(final ByteBuffer encoded, final boolean urlSafe) {
    return urlSafe ? DECODER_URL_SAFE.decode(encoded) : DECODER.decode(encoded);
  }

  public static IBytes decode(final IBytes encoded, final boolean urlSafe) {
    final var decodedBytes = decode(encoded.toByteBuffer(), urlSafe).array();
    return IBytes.wrap(decodedBytes);
  }

  public static IBytes decode(final String encoded, final boolean urlSafe) {
    final var encodedByteBuffer = ByteBuffer.wrap(encoded.getBytes()).asReadOnlyBuffer();
    final var decodedBytes = decode(encodedByteBuffer, urlSafe).array();
    return IBytes.wrap(decodedBytes);
  }

  public static ByteBuffer encode(final ByteBuffer decoded, final boolean urlSafe) {
    return urlSafe ? ENCODER_URL_SAFE.encode(decoded) : ENCODER.encode(decoded);
  }

  public static IBytes encode(final IBytes decoded, final boolean urlSafe) {
    final var encodedBytes = encode(decoded.toByteBuffer(), urlSafe).array();
    return IBytes.wrap(encodedBytes);
  }

  public static String encodeAsString(final byte[] decoded, final boolean urlSafe) {
    final var decodedByteBuffer = ByteBuffer.wrap(decoded).asReadOnlyBuffer();
    final var encodedBytes = encode(decodedByteBuffer, urlSafe).array();
    return IBytes.wrap(encodedBytes).toString("utf8");
  }

  public static String encodeAsString(final IBytes decoded, final boolean urlSafe) {
    final var encodedBytes = encode(decoded.toByteBuffer(), urlSafe).array();
    return IBytes.wrap(encodedBytes).toString("utf8");
  }

}
