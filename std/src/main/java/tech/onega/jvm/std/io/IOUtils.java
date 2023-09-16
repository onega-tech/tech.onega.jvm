package tech.onega.jvm.std.io;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.validation.constraints.NotNull;
import tech.onega.jvm.std.io.writer.IOWriterBytes;
import tech.onega.jvm.std.struct.bytes.IBytes;
import tech.onega.jvm.std.validate.Check;

final public class IOUtils {

  public static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

  public static final int EOF = -1;

  public static void closeResource(final AutoCloseable resource) {
    if (resource != null) {
      try {
        resource.close();
      }
      catch (final Throwable e) {
        //ignore
      }
    }
  }

  public static void closeResource(final Closeable resource) {
    if (resource != null) {
      try {
        resource.close();
      }
      catch (final Throwable e) {
        //ignore
      }
    }
  }

  public static void closeResources(final Iterable<? extends AutoCloseable> resources) {
    for (final AutoCloseable resource : resources) {
      closeResource(resource);
    }
  }

  @SafeVarargs
  public static <R extends AutoCloseable> void closeResources(final R... resources) {
    for (final AutoCloseable resource : resources) {
      closeResource(resource);
    }
  }

  public static long copyStreams(final InputStream input, final OutputStream output) throws IOException {
    return copyStreams(input, output, DEFAULT_BUFFER_SIZE);
  }

  public static long copyStreams(final InputStream input, final OutputStream output, final byte[] buffer)
    throws IOException {
    long count = 0;
    int n;
    while (EOF != (n = input.read(buffer))) {
      output.write(buffer, 0, n);
      count += n;
    }
    return count;
  }

  public static long copyStreams(final InputStream input, final OutputStream output, final int bufferSize)
    throws IOException {
    final var buffer = new byte[bufferSize];
    return copyStreams(input, output, buffer);
  }

  public static byte[] inputStreamToBytes(final InputStream in, final int bufferSize) {
    try (ByteArrayOutputStream out = new ByteArrayOutputStream(bufferSize)) {
      final byte[] buffer = new byte[bufferSize];
      int len;
      while ((len = in.read(buffer)) != -1) {
        out.write(buffer, 0, len);
      }
      return out.toByteArray();
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static IBytes inputStreamToIBytes(@NotNull final InputStream in) {
    Check.notNull(in, "Input stream can't be null");
    return inputStreamToIBytes(in, DEFAULT_BUFFER_SIZE);
  }

  public static IBytes inputStreamToIBytes(final InputStream in, final int bufferSize) {
    try {
      final IOWriterBytes out = new IOWriterBytes(bufferSize);
      final byte[] buffer = new byte[bufferSize];
      int len;
      while ((len = in.read(buffer)) != -1) {
        out.write(buffer, len, 0);
      }
      return out.toIBytes();
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static int readInt32(final byte[] data, final int offset) {
    return 0
      + ((data[offset] & 0xff) << 24)
      + ((data[offset + 1] & 0xff) << 16)
      + ((data[offset + 2] & 0xff) << 8)
      + ((data[offset + 3] & 0xff) << 0);
  }

  public static long readInt64(final byte[] data, final int offset) {
    return 0L
      + ((data[offset] & 0xffL) << 56)
      + ((data[offset + 1] & 0xffL) << 48)
      + ((data[offset + 2] & 0xffL) << 40)
      + ((data[offset + 3] & 0xffL) << 32)
      + ((data[offset + 4] & 0xffL) << 24)
      + ((data[offset + 5] & 0xffL) << 16)
      + ((data[offset + 6] & 0xffL) << 8)
      + ((data[offset + 7] & 0xffL) << 0);
  }

  public static char readUInt16(final byte[] data, final int offset) {
    return (char) (0
      + ((data[offset] & 0xff) << 8)
      + ((data[offset + 1] & 0xff) << 0));
  }

  public static int writeInt32(final int value, final byte[] buffer, int offset) {
    buffer[offset++] = (byte) (value >>> 24);
    buffer[offset++] = (byte) (value >>> 16);
    buffer[offset++] = (byte) (value >>> 8);
    buffer[offset++] = (byte) (value >>> 0);
    return offset;
  }

  public static int writeInt64(final long value, final byte[] buffer, int offset) {
    buffer[offset++] = (byte) (value >>> 56);
    buffer[offset++] = (byte) (value >>> 48);
    buffer[offset++] = (byte) (value >>> 40);
    buffer[offset++] = (byte) (value >>> 32);
    buffer[offset++] = (byte) (value >>> 24);
    buffer[offset++] = (byte) (value >>> 16);
    buffer[offset++] = (byte) (value >>> 8);
    buffer[offset++] = (byte) (value >>> 0);
    return offset;
  }

  public static int writeUInt16(final char value, final byte[] buffer, int offset) {
    buffer[offset++] = (byte) (value >>> 8 & 0xFF);
    buffer[offset++] = (byte) (value >>> 0 & 0xFF);
    return offset;
  }

}
