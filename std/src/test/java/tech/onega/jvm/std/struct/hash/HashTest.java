package tech.onega.jvm.std.struct.hash;

import java.nio.charset.Charset;
import org.testng.annotations.Test;
import tech.onega.jvm.std.struct.bytes.IBytes;
import tech.onega.jvm.std.validate.Check;

public class HashTest {

  @Test
  public void testCrc16() {
    Check.equals(0xBB3D, Hash.crc16(IBytes.of("123456789", Charset.forName("ascii"))));
  }

}
