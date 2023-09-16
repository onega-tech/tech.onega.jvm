package tech.onega.jvm.std.struct.date;

import org.testng.annotations.Test;
import tech.onega.jvm.std.validate.Check;

public class DateTimeIntervalTest {

  @Test
  public void testInInterval() {
    Check.isTrue(
      DateTimeInterval.of(DateTime.parseTime("12:00"), DateTime.parseTime("13:00"))
        .inInterval(DateTime.parseTime("12:01")));
    Check.isFalse(
      DateTimeInterval.of(DateTime.ofTime(12, 02), DateTime.ofTime(13, 00))
        .inInterval(
          DateTimeInterval.of(DateTime.ofTime(12, 01), DateTime.ofTime(12, 02))));
  }

}
