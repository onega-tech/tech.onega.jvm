package tech.onega.jvm.std.io.writer;

import java.util.UUID;
import tech.onega.jvm.std.struct.bytes.IBytes;

abstract public class IOWriterAbstract implements IOWriter {

  @Override
  public void writeBool(final boolean value) {
    writeInt8(value ? (byte) 1 : (byte) 0);
  }

  @Override
  public void writeInt128(final UUID value) {
    final long h = value.getMostSignificantBits();
    final long l = value.getLeastSignificantBits();
    writeInt64(h);
    writeInt64(l);
  }

  @Override
  public void writeInt32(final Integer value) {
    writeInt32(value == null ? 0 : value);
  }

  @Override
  public void writeInt8Array(final byte[] bytes) {
    writeInt32(bytes.length);
    write(bytes, bytes.length, 0);
  }

  @Override
  public void writeInt8Array(final IBytes bytes) {
    writeInt8Array(bytes.unwrap());
  }

  @Override
  public void writeString(final String value) {
    final var bytes = value == null ? new byte[0] : value.getBytes();
    writeInt8Array(bytes);
  }

  @Override
  public void writeUInt16Array(final char[] letters) {
    writeInt32(letters.length);
    for (final char letter : letters) {
      writeUInt16(letter);
    }
  }

}
