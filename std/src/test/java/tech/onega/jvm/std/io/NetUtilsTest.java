package tech.onega.jvm.std.io;

import org.testng.annotations.Test;
import tech.onega.jvm.std.lang.RandUtils;
import tech.onega.jvm.std.validate.Check;

public class NetUtilsTest {

  @Test
  public void testIp4ToString() {
    for (var i = 0; i <= 1024; i++) {
      final var ipBuilder = new StringBuilder(20);
      final var ip = ipBuilder
        .append(RandUtils.randInt(0, 255))
        .append('.')
        .append(RandUtils.randInt(0, 255))
        .append('.')
        .append(RandUtils.randInt(0, 255))
        .append('.')
        .append(RandUtils.randInt(0, 255))
        .toString();
      Check.equals(NetUtils.ip4ToString(NetUtils.ip4ToInt(ip)), ip);
    }
  }

}
