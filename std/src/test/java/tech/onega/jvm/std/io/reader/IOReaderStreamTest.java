package tech.onega.jvm.std.io.reader;

import java.io.ByteArrayInputStream;
import java.util.UUID;
import org.testng.annotations.Test;
import tech.onega.jvm.std.io.writer.IOWriterBytes;
import tech.onega.jvm.std.lang.RandUtils;
import tech.onega.jvm.std.validate.Check;

public class IOReaderStreamTest {

  @Test
  public void testFirst() {
    for (var k = 0; k < 100; k++) {
      final var int8 = RandUtils.randBytes(1)[0];
      final var uint16 = RandUtils.randChar();
      final var int32 = RandUtils.randInt();
      final var int64 = RandUtils.randLong();
      final var int128 = UUID.randomUUID();
      final var writer = new IOWriterBytes();
      writer.writeInt8(int8);
      writer.writeUInt16(uint16);
      writer.writeInt32(int32);
      writer.writeInt64(int64);
      writer.writeInt128(int128);
      final var reader = new IOReaderStream(new ByteArrayInputStream(writer.toBytes()));
      Check.equals(reader.readInt8(), int8);
      Check.equals(reader.readUInt16(), uint16);
      Check.equals(reader.readInt32(), int32);
      Check.equals(reader.readInt64(), int64);
      Check.equals(reader.readInt128(), int128);
    }
  }

}
