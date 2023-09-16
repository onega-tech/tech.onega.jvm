package tech.onega.jvm.std.struct.array;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import tech.onega.jvm.std.annotation.NotThreadSafe;

@NotThreadSafe
final public class ArrayOutputStream extends OutputStream {

  private static final int MAX_CAPACITY = Integer.MAX_VALUE - 8;

  private byte buf[];

  private int count;

  public ArrayOutputStream() {
    this(32);
  }

  public ArrayOutputStream(final int size) {
    if (size < 0) {
      throw new IllegalArgumentException("Negative initial size: " + size);
    }
    buf = new byte[size];
  }

  @Override
  public void close() {
    buf = null;
  }

  private void ensureCapacity(final int needSize) {
    buf = ArrayUtils.growBytes(buf, needSize, MAX_CAPACITY);
  }

  public void reset() {
    count = 0;
  }

  public int size() {
    return count;
  }

  public byte[] toByteArray() {
    return Arrays.copyOf(buf, count);
  }

  public byte[] toByteArrayAndClose() {
    final byte[] data = toByteArray();
    close();
    return data;
  }

  @Override
  public String toString() {
    return new String(buf, 0, count);
  }

  @Override
  public void write(final byte b[], final int off, final int len) {
    if (off < 0 || off > b.length || len < 0 ||
      off + len - b.length > 0) {
      throw new IndexOutOfBoundsException();
    }
    ensureCapacity(count + len);
    System.arraycopy(b, off, buf, count, len);
    count += len;
  }

  @Override
  public void write(final int b) {
    ensureCapacity(count + 1);
    buf[count] = (byte) b;
    count += 1;
  }

  public void writeTo(final OutputStream out) throws IOException {
    out.write(buf, 0, count);
  }

}
