package tech.onega.jvm.std.io.reader;

import java.io.ByteArrayOutputStream;
import java.util.UUID;
import org.testng.annotations.Test;
import tech.onega.jvm.std.io.writer.IOWriterStream;
import tech.onega.jvm.std.lang.RandUtils;
import tech.onega.jvm.std.validate.Check;

public class IOReaderWriterStreamTest {

  @Test
  public void testFirst() throws Exception {
    for (var k = 0; k < 100; k++) {
      final var int8 = RandUtils.randBytes(1)[0];
      final var uint16 = RandUtils.randChar();
      final var int32 = RandUtils.randInt();
      final var int64 = RandUtils.randLong();
      final var int128 = UUID.randomUUID();
      try (var baos = new ByteArrayOutputStream()) {
        final var writer = new IOWriterStream(baos);
        writer.writeInt8(int8);
        writer.writeUInt16(uint16);
        writer.writeInt32(int32);
        writer.writeInt64(int64);
        writer.writeInt128(int128);
        final var bytes = baos.toByteArray();
        final var reader = new IOReaderBytes(bytes);
        Check.equals(reader.readInt8(), int8);
        Check.equals(reader.readUInt16(), uint16);
        Check.equals(reader.readInt32(), int32);
        Check.equals(reader.readInt64(), int64);
        Check.equals(reader.readInt128(), int128);
      }
    }
  }

}
