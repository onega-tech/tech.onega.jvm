package tech.onega.jvm.std.struct.date;

import org.testng.annotations.Test;
import tech.onega.jvm.std.validate.Check;

public class ISO8601Test {

  @Test
  public void testParse() {
    final var time = DateTime.now().withMillis(DateTime.now().getMillis());
    final var text = ISO8601.toString(time);
    final var parsed = ISO8601.parse(text);
    Check.equals(time, parsed);
  }

}
