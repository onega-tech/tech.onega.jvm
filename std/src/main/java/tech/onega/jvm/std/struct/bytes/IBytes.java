package tech.onega.jvm.std.struct.bytes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonValue;
import tech.onega.jvm.std.annotation.Copy;
import tech.onega.jvm.std.annotation.Immutable;
import tech.onega.jvm.std.annotation.Unsafe;
import tech.onega.jvm.std.annotation.Wrap;
import tech.onega.jvm.std.codec.base.Base64Codec;
import tech.onega.jvm.std.codec.hex.HexCodec;
import tech.onega.jvm.std.io.reader.IOReader;
import tech.onega.jvm.std.io.reader.IOReaderBytes;
import tech.onega.jvm.std.io.writer.IOWriter;
import tech.onega.jvm.std.io.writer.IOWriterBytes;
import tech.onega.jvm.std.lang.Equals;
import tech.onega.jvm.std.struct.uuid.UUIDUtils;

@Immutable
final public class IBytes {

  public static final IBytes EMPTY = IBytes.wrap(new byte[0]);

  public static IBytes concat(final IBytes... bytes) {
    int length = 0;
    for (final IBytes b : bytes) {
      length += b.length();
    }
    final byte[] result = new byte[length];
    int offset = 0;
    for (final IBytes b : bytes) {
      b.copyTo(result, offset);
      offset += b.length();
    }
    return IBytes.wrap(result);
  }

  public static IOWriterBytes createWriter() {
    return new IOWriterBytes();
  }

  public static IBytes fromBase64(final String base64, final boolean urlSafe) {
    return Base64Codec.decode(base64, urlSafe);
  }

  public static IBytes fromHEX(final String hex) {
    return HexCodec.decode(hex);
  }

  public static IBytes of(final BigInteger bigInteger) {
    return IBytes.wrap(bigInteger.toByteArray());
  }

  @Copy
  public static IBytes of(final byte[] bytes) {
    return of(bytes, bytes.length, 0);
  }

  @Copy
  public static IBytes of(final byte[] bytes, final int limit, final int offset) {
    final byte[] data = new byte[limit];
    System.arraycopy(bytes, offset, data, 0, limit);
    return new IBytes(data);
  }

  @Copy
  public static IBytes of(final ByteBuffer byteBuffer) {
    final byte[] data = new byte[byteBuffer.limit()];
    byteBuffer.get(data);
    return new IBytes(data);
  }

  public static IBytes of(final long value) {
    final byte[] bytes = {
      (byte) ((value >>> 56) & 0xFF),
      (byte) ((value >>> 48) & 0xFF),
      (byte) ((value >>> 40) & 0xFF),
      (byte) ((value >>> 32) & 0xFF),
      (byte) ((value >>> 24) & 0xFF),
      (byte) ((value >>> 16) & 0xFF),
      (byte) ((value >>> 8) & 0xFF),
      (byte) ((value >>> 0) & 0xFF)
    };
    return new IBytes(bytes);
  }

  @Copy
  public static IBytes of(final String string) {
    return of(string, StandardCharsets.UTF_8);
  }

  public static IBytes of(final String string, final Charset charset) {
    return new IBytes(string.getBytes(charset));
  }

  public static IBytes of(final UUID uuid) {
    return of(UUIDUtils.toBigInteger(uuid));
  }

  /**
   * Read from current position and increment buyte buffer position
   */
  @Copy
  public static IBytes read(final ByteBuffer byteBuffer, final int limit) {
    return read(byteBuffer, limit, byteBuffer.position());
  }

  /**
   * Read and increment buyte buffer position
   */
  @Copy
  public static IBytes read(final ByteBuffer byteBuffer, final int limit, final int offset) {
    final byte[] data = new byte[limit];
    byteBuffer.position(offset);
    byteBuffer.get(data);
    byteBuffer.position(offset + limit);
    return new IBytes(data);
  }

  @Copy
  public static IBytes read(final java.io.InputStream in) {
    return read(in, 1024, 1024);
  }

  @Copy
  public static IBytes read(final java.io.InputStream in, final int initialSize, final int bufferSize) {
    final IOWriterBytes writer = new IOWriterBytes(initialSize);
    writer.readAll(in, bufferSize);
    return writer.toIBytes();
  }

  public static IBytes readFrom(final IOReader reader) {
    return IBytes.wrap(reader.readInt8Array());
  }

  @Wrap
  @Unsafe
  public static IBytes wrap(@Unsafe final byte[] bytes) {
    return new IBytes(bytes);
  }

  private final byte[] bytes;

  private final int hash;

  private IBytes(final byte[] bytes) {
    this.bytes = bytes;
    hash = Arrays.hashCode(bytes);
  }

  public InputStream asInputStream() {
    return asReader().asInputStream();
  }

  public IOReaderBytes asReader() {
    return new IOReaderBytes(bytes);
  }

  @Copy
  public IBytes copy(final int limit, final int offset) {
    final var dest = new byte[limit - offset];
    copyTo(dest, 0, limit, offset);
    return IBytes.wrap(dest);
  }

  @Copy
  public void copyTo(final byte[] dest, final int destOffset) {
    copyTo(dest, destOffset, length(), 0);
  }

  @Copy
  public void copyTo(final byte[] dest, final int destOffset, final int limit, final int offset) {
    System.arraycopy(bytes, offset, dest, destOffset, limit);
  }

  @Override
  public boolean equals(final Object obj) {
    return Equals.check(this, obj, f -> new Object[] { f.bytes });
  }

  public byte get(final int index) {
    return bytes[index];
  }

  @Override
  public int hashCode() {
    return hash;
  }

  public boolean isEmpty() {
    return length() == 0;
  }

  public int length() {
    return bytes.length;
  }

  @Copy
  public IBytes slice(final int limit, final int offset) {
    final byte[] result = new byte[limit];
    copyTo(result, 0, limit, offset);
    return IBytes.wrap(result);
  }

  @Copy
  public byte[] toArray() {
    return Arrays.copyOf(bytes, bytes.length);
  }

  public String toBase64() {
    return toBase64(true);
  }

  public String toBase64(final boolean urlSafe) {
    return Base64Codec.encodeAsString(this, urlSafe);
  }

  @Copy
  public BigInteger toBigInteger() {
    return new BigInteger(bytes);
  }

  public ByteBuffer toByteBuffer() {
    return ByteBuffer.wrap(bytes).asReadOnlyBuffer();
  }

  @JsonValue
  public String toHEX() {
    return HexCodec.encode(this);
  }

  @Override
  public String toString() {
    return Arrays.toString(bytes);
  }

  @Copy
  public String toString(final Charset charset) {
    return new String(bytes, charset);
  }

  @Copy
  public String toString(final String charsetName) {
    return new String(bytes, Charset.forName(charsetName));
  }

  @Copy
  public String toStringUTF8() {
    return toString(StandardCharsets.UTF_8);
  }

  public UUID toUUID() {
    return UUIDUtils.fromBigInteger(toBigInteger());
  }

  @Unsafe
  public byte[] unwrap() {
    return bytes;
  }

  @Copy
  public void writeTo(final IOWriter writer) {
    writer.write(bytes, bytes.length, 0);
  }

  @Copy
  public void writeTo(final OutputStream outputStream) throws IOException {
    outputStream.write(bytes, 0, bytes.length);
  }

  @Copy
  public void writeToAsArray(final IOWriter writer) {
    writer.writeInt8Array(bytes);
  }

}
