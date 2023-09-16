package tech.onega.jvm.std.struct.date;

import org.testng.annotations.Test;
import tech.onega.jvm.std.validate.Check;

public class CronTest {

  @Test
  public void test() {
    Check.equals(Cron.of("0 0 * * * *").next(DateTime.parse("2007-12-03 00:00:01")),
      DateTime.parse("2007-12-03 01:00:00"));
    Check.equals(Cron.of("*/10 * * * * *").next(DateTime.parse("2007-12-03 00:00:01")),
      DateTime.parse("2007-12-03 00:00:10"));
    Check.equals(Cron.of("0 0 8-10 * * *").next(DateTime.parse("2007-12-03 00:00:01")),
      DateTime.parse("2007-12-03 08:00:00"));
    Check.equals(Cron.of("0 0/30 8-10 * * *").next(DateTime.parse("2007-12-03 08:00:01")),
      DateTime.parse("2007-12-03 08:30:00"));
    Check.equals(Cron.of("0 0 9-17 * * MON-FRI").next(DateTime.parse("2007-12-03 08:00:01")),
      DateTime.parse("2007-12-03 09:00:00"));
    Check.equals(Cron.of("0 0 0 25 12 ?").next(DateTime.parse("2007-12-03 08:00:01")),
      DateTime.parse("2007-12-25 00:00:00"));
  }

}
