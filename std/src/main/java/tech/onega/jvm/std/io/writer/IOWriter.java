package tech.onega.jvm.std.io.writer;

import java.io.OutputStream;
import java.util.UUID;
import tech.onega.jvm.std.struct.bytes.IBytes;

public interface IOWriter {

  OutputStream asOutputStream();

  void write(byte[] bytes, int limit, int offset);

  void writeBool(boolean value);

  void writeInt128(UUID value);

  void writeInt32(int value);

  void writeInt32(Integer value);

  void writeInt64(long value);

  void writeInt8(byte bytez);

  void writeInt8Array(byte[] bytes);

  void writeInt8Array(IBytes bytes);

  void writeString(String value);

  void writeUInt16(char letter);

  void writeUInt16Array(char[] letters);

}
