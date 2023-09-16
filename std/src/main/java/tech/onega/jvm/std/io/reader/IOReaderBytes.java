package tech.onega.jvm.std.io.reader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import tech.onega.jvm.std.io.IOUtils;

final public class IOReaderBytes extends IOReaderAbstract {

  private final byte[] data;

  private int offset;

  public IOReaderBytes(final byte[] data) {
    this.data = data;
    this.offset = 0;
  }

  @Override
  public InputStream asInputStream() {
    return new ByteArrayInputStream(this.data, this.offset, this.data.length - this.offset);
  }

  @Override
  public int available() {
    return this.data.length - this.offset;
  }

  @Override
  public boolean eof() {
    return this.offset >= this.data.length;
  }

  public int offset() {
    return this.offset;
  }

  @Override
  public int read(final byte[] buffer, final int limit, final int bufferOffset) {
    final int available = this.available();
    final int canWriterToBuffer = buffer.length - bufferOffset;
    final int readed = Math.min(limit, Math.min(canWriterToBuffer, available));
    System.arraycopy(this.data, this.offset, buffer, bufferOffset, readed);
    this.offset += readed;
    return readed;
  }

  @Override
  public int readInt32() {
    final int r = IOUtils.readInt32(this.data, this.offset);
    this.offset += 4;
    return r;
  }

  @Override
  public long readInt64() {
    final long r = IOUtils.readInt64(this.data, this.offset);
    this.offset += 8;
    return r;
  }

  @Override
  public byte readInt8() {
    return this.data[this.offset++];
  }

  @Override
  public char readUInt16() {
    final char r = IOUtils.readUInt16(this.data, this.offset);
    this.offset += 2;
    return r;
  }

  public void writeToStream(final java.io.OutputStream out) {
    if (this.eof()) {
      return;
    }
    try {
      out.write(this.data, this.offset, this.data.length - this.offset);
      this.offset = this.data.length;
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

}
