package tech.onega.jvm.std.io.reader;

import java.util.Arrays;
import org.testng.annotations.Test;
import tech.onega.jvm.std.lang.RandUtils;
import tech.onega.jvm.std.validate.Check;

public class IOReaderBytesTest {

  @Test
  public void test() {
    final var data = RandUtils.randBytes(32);
    final var readBuffer = new byte[256];
    final var reader = new IOReaderBytes(data);
    Check.equals(reader.available(), 32);
    Check.equals(reader.offset(), 0);
    Check.isFalse(reader.eof());
    Check.equals(reader.read(readBuffer, 128, 128), 32);
    Check.isTrue(reader.eof());
    Check.equals(reader.available(), 0);
    Check.equals(data, Arrays.copyOfRange(readBuffer, 128, 128 + 32));
  }

}
