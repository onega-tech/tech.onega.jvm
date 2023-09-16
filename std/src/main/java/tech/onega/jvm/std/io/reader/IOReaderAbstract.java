package tech.onega.jvm.std.io.reader;

import java.util.UUID;

abstract public class IOReaderAbstract implements IOReader {

  @Override
  public boolean readBool() {
    final var value = readInt8();
    switch (value) {
      case (byte) 1:
        return true;
      case (byte) 0:
        return false;
      default:
        throw new IllegalArgumentException("Wrong value");
    }
  }

  @Override
  public UUID readInt128() {
    final long h = readInt64();
    final long l = readInt64();
    return new UUID(h, l);
  }

  @Override
  public byte[] readInt8Array() {
    final int length = readInt32();
    final byte[] buffer = new byte[length];
    read(buffer, length, 0);
    return buffer;
  }

  @Override
  public String readString() {
    final var bytes = readInt8Array();
    return bytes.length == 0 ? null : new String(bytes);
  }

  @Override
  public char[] readUInt16Array() {
    final int size = readInt32();
    final char[] result = new char[size];
    for (int i = 0; i < size; i++) {
      result[i] = readUInt16();
    }
    return result;
  }

}