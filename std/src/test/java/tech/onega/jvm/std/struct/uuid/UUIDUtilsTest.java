package tech.onega.jvm.std.struct.uuid;

import java.util.UUID;
import org.testng.annotations.Test;
import tech.onega.jvm.std.validate.Check;

public class UUIDUtilsTest {

  @Test
  public void testFromToBigInteger() {
    for (var i = 0; i < 10_000; i++) {
      final var uuid = UUID.randomUUID();
      final var bInt = UUIDUtils.toBigInteger(uuid);
      final var uuidFromBInt = UUIDUtils.fromBigInteger(bInt);
      Check.equals(uuid, uuidFromBInt);
    }
  }

}
