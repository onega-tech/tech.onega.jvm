package tech.onega.jvm.std.io.reader;

import java.io.IOException;
import java.io.InputStream;
import tech.onega.jvm.std.io.IOUtils;

final public class IOReaderStream extends IOReaderAbstract {

  private final InputStream inputStream;

  private final byte[] buffer8 = new byte[8];

  public IOReaderStream(final InputStream inputStream) {
    this.inputStream = inputStream;
  }

  @Override
  public InputStream asInputStream() {
    return this.inputStream;
  }

  @Override
  public int available() {
    try {
      return this.inputStream.available();
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean eof() {
    return this.available() <= 0;
  }

  @Override
  public int read(final byte[] buffer, final int limit, final int bufferOffset) {
    try {
      return this.inputStream.read(buffer, bufferOffset, limit);
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public int readInt32() {
    try {
      this.inputStream.read(this.buffer8, 0, 4);
      return IOUtils.readInt32(this.buffer8, 0);
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public long readInt64() {
    try {
      this.inputStream.read(this.buffer8, 0, 8);
      return IOUtils.readInt64(this.buffer8, 0);
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public byte readInt8() {
    try {
      return (byte) this.inputStream.read();
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public char readUInt16() {
    try {
      this.inputStream.read(this.buffer8, 0, 2);
      return IOUtils.readUInt16(this.buffer8, 0);
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

}
