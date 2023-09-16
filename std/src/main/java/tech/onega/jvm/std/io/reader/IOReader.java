package tech.onega.jvm.std.io.reader;

import java.io.InputStream;
import java.util.UUID;

public interface IOReader {

  InputStream asInputStream();

  int available();

  boolean eof();

  int read(byte[] buffer, int limit, int offset);

  boolean readBool();

  UUID readInt128();

  int readInt32();

  long readInt64();

  byte readInt8();

  byte[] readInt8Array();

  String readString();

  char readUInt16();

  char[] readUInt16Array();

}
