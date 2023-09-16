package tech.onega.jvm.std.io.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import tech.onega.jvm.std.io.IOUtils;
import tech.onega.jvm.std.struct.array.ArrayUtils;
import tech.onega.jvm.std.struct.bytes.IBytes;

final public class IOWriterBytes extends IOWriterAbstract {

  private static final int MAX_CAPACITY = Integer.MAX_VALUE - 8;

  private byte[] buffer;

  private int size;

  public IOWriterBytes() {
    this(1024);
  }

  public IOWriterBytes(final int initialSize) {
    if (initialSize < 0) {
      throw new IllegalArgumentException("Negative initial size: " + initialSize);
    }
    this.buffer = new byte[initialSize];
    this.size = 0;
  }

  @Override
  public OutputStream asOutputStream() {
    return new OutputStream() {

      @Override
      public void write(final byte data[], final int offset, final int length) {
        IOWriterBytes.this.write(data, length, offset);
      }

      @Override
      public void write(final int b) {
        IOWriterBytes.this.ensureCapacity(IOWriterBytes.this.size + 1);
        IOWriterBytes.this.buffer[IOWriterBytes.this.size] = (byte) b;
        IOWriterBytes.this.size++;
      }

    };
  }

  public int capacity() {
    return this.buffer.length;
  }

  private void ensureCapacity(final int needSize) {
    this.buffer = ArrayUtils.growBytes(this.buffer, needSize, MAX_CAPACITY);
  }

  public int read(final java.io.InputStream in, final int limit) {
    try {
      this.ensureCapacity(this.size + limit);
      final int readed = in.read(this.buffer, this.size, limit);
      if (readed > -1) {
        this.size += readed;
      }
      return readed;
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public int readAll(final java.io.InputStream in, final int bufferSize) {
    try {
      final int oldSize = this.size;
      while (true) {
        this.ensureCapacity(this.size + bufferSize);
        final int readed = in.read(this.buffer, this.size, bufferSize);//bufferSize
        if (readed == -1) {
          return this.size - oldSize;
        }
        else {
          this.size += readed;
        }
      }
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public int size() {
    return this.size;
  }

  public byte[] toBytes() {
    return Arrays.copyOf(this.buffer, this.size);
  }

  public IBytes toIBytes() {
    return IBytes.wrap(this.toBytes());
  }

  @Override
  public void write(final byte[] data, final int limit, final int offset) {
    if (offset < 0 || offset > data.length || limit < 0 || offset + limit - data.length > 0) {
      throw new IndexOutOfBoundsException();
    }
    this.ensureCapacity(this.size + limit);
    System.arraycopy(data, offset, this.buffer, this.size, limit);
    this.size += limit;
  }

  @Override
  public void writeInt32(final int value) {
    this.ensureCapacity(this.size + 4);
    this.size = IOUtils.writeInt32(value, this.buffer, this.size);
  }

  @Override
  public void writeInt64(final long value) {
    this.ensureCapacity(this.size + 8);
    this.size = IOUtils.writeInt64(value, this.buffer, this.size);
  }

  @Override
  public void writeInt8(final byte int8) {
    this.ensureCapacity(this.size + 1);
    this.buffer[this.size++] = int8;
  }

  @Override
  public void writeUInt16(final char value) {
    this.ensureCapacity(this.size + 2);
    this.size = IOUtils.writeUInt16(value, this.buffer, this.size);
  }

}
