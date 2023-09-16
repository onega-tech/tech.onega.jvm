package tech.onega.jvm.std.struct.date;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.testng.annotations.Test;
import tech.onega.jvm.std.codec.json.JsonCodec;
import tech.onega.jvm.std.validate.Check;

public class DateTimeTest {

  @Test
  public void testEquals() {
    Check.equals(DateTime.parse("2000-01-01 00:00:00"), DateTime.ofDateTime(2000, 1, 1, 0, 0, 0));
    Check.isTrue(DateTime.ofDateTime(2000, 1, 1, 0, 0, 0).equals(DateTime.ofDateTime(2000, 1, 1, 0, 0, 0)));
  }

  @Test
  public void testGetPrevios() {
    final var current = DateTime.parseDate("2015-09-14");//monday
    Check.equals(current.getPrevios(7), DateTime.parseDate("2015-09-13"));
    Check.equals(current.getPrevios(6), DateTime.parseDate("2015-09-12"));
    Check.equals(current.getPrevios(5), DateTime.parseDate("2015-09-11"));
    Check.equals(current.getPrevios(4), DateTime.parseDate("2015-09-10"));
    Check.equals(current.getPrevios(3), DateTime.parseDate("2015-09-09"));
    Check.equals(current.getPrevios(2), DateTime.parseDate("2015-09-08"));
    Check.equals(current.getPrevios(1), DateTime.parseDate("2015-09-07"));
  }

  @Test
  public void testJson() {
    final var date = DateTime.parse("2000-01-01 01:01:01");
    final var json = JsonCodec.toString(date);
    final var fromJson = JsonCodec.parse(json, DateTime.class);
    Check.equals(date, fromJson);
  }

  @Test
  public void testMax() {
    final var a = DateTime.parseDate("2015-09-14");
    final var b = DateTime.parseDate("2014-09-14");
    Check.equals(DateTime.max(a, b), a);
  }

  @Test
  public void testMilliSeconds() {
    Check.equals(DateTime.ofDateTime(2001, 1, 1, 1, 1, 1, 7_000_000).getMillis(), 7);
  }

  @Test
  public void testMin() {
    final var a = DateTime.parseDate("2015-09-14");
    final var b = DateTime.parseDate("2014-09-14");
    Check.equals(DateTime.min(a, b), b);
  }

  @Test
  public void testOfDefault() {
    final var stdTime = DateTime.parse("2018-02-01 19:07:30");
    Check.equals(TimeZone.UTC, stdTime.getZone());
    Check.equals(stdTime.getYear(), 2018);
    Check.equals(stdTime.getMonth(), 2);
    Check.equals(stdTime.getDayOfMonth(), 1);
    Check.equals(stdTime.getDayOfYear(), 32);
    Check.equals(stdTime.getDayOfWeek(), 4);
    Check.equals(stdTime.getHours(), 19);
    Check.equals(stdTime.getMinutes(), 7);
    Check.equals(stdTime.getSeconds(), 30);
    Check.equals(stdTime.getNano(), 0);
  }

  @Test
  public void testOfStringZone() {
    final var stdTime = DateTime.parse("2018-02-01 19:07:30", TimeZone.system());
    Check.equals(TimeZone.system(), stdTime.getZone());
    Check.equals(stdTime.getYear(), 2018);
    Check.equals(stdTime.getMonth(), 2);
    Check.equals(stdTime.getDayOfMonth(), 1);
    Check.equals(stdTime.getDayOfYear(), 32);
    Check.equals(stdTime.getDayOfWeek(), 4);
    Check.equals(stdTime.getHours(), 19);
    Check.equals(stdTime.getMinutes(), 7);
    Check.equals(stdTime.getSeconds(), 30);
    Check.equals(stdTime.getNano(), 0);
  }

  @Test
  public void testOfZonedDateTime() {
    final var zonedDateTime = ZonedDateTime.of(2018, 2, 1, 19, 7, 30, 5, ZoneId.of("UTC"));
    final var time = DateTime.ofDateTime(zonedDateTime);
    Check.equals(time.getYear(), 2018);
    Check.equals(time.getMonth(), 2);
    Check.equals(time.getDayOfMonth(), 1);
    Check.equals(time.getHours(), 19);
    Check.equals(time.getMinutes(), 7);
    Check.equals(time.getSeconds(), 30);
    Check.equals(time.getNano(), 5);
    Check.equals(time.getZone(), TimeZone.UTC);
  }

  @Test
  public void testPlusMunisDuration() {
    final var dateTime = DateTime.parse("2018-02-02 19:07:30");
    Check.equals(dateTime.plus(Duration.ofDays(1)).toRFC1123(), "Sat, 3 Feb 2018 19:07:30 GMT");
    Check.equals(dateTime.minus(Duration.ofDays(1)).toRFC1123(), "Thu, 1 Feb 2018 19:07:30 GMT");
  }

  @Test
  public void testYear() {
    final var stdTime = DateTime.parse("2018-02-01 19:07:30");
    Check.equals(stdTime.getYear(), 2018);
    Check.equals(stdTime.withYear(1000).getYear(), 1000);
  }

  @Test
  public void testZoneOf() {
    Check.notNull(TimeZone.of("Europe/Moscow"));
  }

  @Test
  public void testZoneOffset() {
    Check.equals(DateTime.now().getZoneOffsetInSeconds(), 0);
    Check.equals(DateTime.now().getZoneOffsetInHours(), 0);
    final var offsetMsk = DateTime.now(TimeZone.of("Europe/Moscow")).getZoneOffsetInHours();
    Check.isTrue((offsetMsk == 3) || (offsetMsk == 4));
  }

  @Test
  public void testZoneSystem() {
    Check.notNull(TimeZone.system());
  }

}
