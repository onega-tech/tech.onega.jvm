package tech.onega.jvm.std.io.writer;

import java.io.IOException;
import java.io.OutputStream;
import tech.onega.jvm.std.io.IOUtils;

final public class IOWriterStream extends IOWriterAbstract {

  private final OutputStream stream;

  private final byte[] buffer = new byte[8];

  public IOWriterStream(final OutputStream stream) {
    this.stream = stream;
  }

  @Override
  public OutputStream asOutputStream() {
    return this.stream;
  }

  @Override
  public void write(final byte[] bytes, final int limit, final int offset) {
    try {
      this.stream.write(bytes, offset, limit);
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void writeInt32(final int value) {
    final int limit = IOUtils.writeInt32(value, this.buffer, 0);
    this.write(this.buffer, limit, 0);
  }

  @Override
  public void writeInt64(final long value) {
    final int limit = IOUtils.writeInt64(value, this.buffer, 0);
    this.write(this.buffer, limit, 0);
  }

  @Override
  public void writeInt8(final byte bytez) {
    this.buffer[0] = bytez;
    this.write(this.buffer, 1, 0);
  }

  @Override
  public void writeUInt16(final char letter) {
    final int limit = IOUtils.writeUInt16(letter, this.buffer, 0);
    this.write(this.buffer, limit, 0);
  }

}
