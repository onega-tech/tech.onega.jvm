package tech.onega.jvm.std.io.writer;

import java.util.UUID;
import org.testng.annotations.Test;
import tech.onega.jvm.std.io.reader.IOReaderBytes;
import tech.onega.jvm.std.lang.RandUtils;
import tech.onega.jvm.std.validate.Check;

public class IOWriterBytesTest {

  @Test
  public void testInt128() {
    for (var i = 0; i < 100; i++) {
      final var uuid = UUID.randomUUID();
      final var writer = new IOWriterBytes(16);
      writer.writeInt128(uuid);
      final var reader = new IOReaderBytes(writer.toBytes());
      Check.isFalse(reader.eof());
      Check.equals(reader.readInt128(), uuid);
      Check.isTrue(reader.eof());
    }
  }

  @Test
  public void testInt32() {
    final var writer = new IOWriterBytes(16);
    final var i1 = RandUtils.randInt();
    final var i2 = RandUtils.randInt();
    final var i3 = RandUtils.randInt();
    writer.writeInt32(i1);
    writer.writeInt32(i2);
    writer.writeInt32(i3);
    Check.equals(writer.size(), 12);
    Check.isTrue(writer.size() <= writer.capacity());
    final var reader = new IOReaderBytes(writer.toBytes());
    Check.isFalse(reader.eof());
    Check.equals(reader.available(), 12);
    Check.equals(reader.readInt32(), i1);
    Check.equals(reader.readInt32(), i2);
    Check.equals(reader.readInt32(), i3);
    Check.isTrue(reader.eof());
    Check.equals(reader.available(), 0);
  }

  @Test
  public void testInt64() {
    final var writer = new IOWriterBytes(16);
    final var l1 = RandUtils.randLong();
    final var l2 = RandUtils.randLong();
    final var l3 = RandUtils.randLong();
    writer.writeInt64(l1);
    writer.writeInt64(l2);
    writer.writeInt64(l3);
    Check.equals(writer.size(), 24);
    Check.isTrue(writer.size() <= writer.capacity());
    final var reader = new IOReaderBytes(writer.toBytes());
    Check.isFalse(reader.eof());
    Check.equals(reader.available(), 24);
    Check.equals(reader.readInt64(), l1);
    Check.equals(reader.readInt64(), l2);
    Check.equals(reader.readInt64(), l3);
    Check.isTrue(reader.eof());
    Check.equals(reader.available(), 0);
  }

  @Test
  public void testInt8Array() {
    for (var i = 0; i < 100; i++) {
      final var data = RandUtils.randBytes(1024);
      final var writer = new IOWriterBytes();
      writer.writeInt64(RandUtils.randLong());
      writer.writeInt8Array(data);
      final var reader = new IOReaderBytes(writer.toBytes());
      reader.readInt64();
      final var dataReaded = reader.readInt8Array();
      Check.equals(data, dataReaded);
    }
  }

  @Test
  public void testUInt16() {
    final var writer = new IOWriterBytes(16);
    final var c1 = (char) RandUtils.randInt();
    final var c2 = (char) RandUtils.randInt();
    final var c3 = (char) RandUtils.randInt();
    writer.writeUInt16(c1);
    writer.writeUInt16(c2);
    writer.writeUInt16(c3);
    Check.equals(writer.size(), 6);
    Check.isTrue(writer.size() <= writer.capacity());
    final var reader = new IOReaderBytes(writer.toBytes());
    Check.isFalse(reader.eof());
    Check.equals(reader.available(), 6);
    Check.equals(reader.readUInt16(), c1);
    Check.equals(reader.readUInt16(), c2);
    Check.equals(reader.readUInt16(), c3);
    Check.isTrue(reader.eof());
    Check.equals(reader.available(), 0);
  }

}
