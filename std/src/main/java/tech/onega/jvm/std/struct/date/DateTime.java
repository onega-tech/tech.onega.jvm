package tech.onega.jvm.std.struct.date;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAmount;
import java.util.Comparator;
import com.fasterxml.jackson.annotation.JsonValue;
import tech.onega.jvm.std.annotation.Copy;
import tech.onega.jvm.std.annotation.Immutable;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.lang.Equals;
import tech.onega.jvm.std.lang.ObjectUtils;

@Immutable
@ThreadSafe
public class DateTime implements Comparable<DateTime> {

  public static DateTimeFormatter FORMATTER_SIMPLE_DATE_TIME = new DateTimeFormatterBuilder()
    .parseCaseInsensitive()
    .parseLenient()
    .appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
    .appendLiteral("-")
    .appendValue(ChronoField.MONTH_OF_YEAR, 2)
    .appendLiteral("-")
    .appendValue(ChronoField.DAY_OF_MONTH, 2)
    .appendLiteral(" ")
    .appendValue(ChronoField.HOUR_OF_DAY, 2)
    .appendLiteral(':')
    .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
    .appendLiteral(':')
    .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
    .toFormatter();

  public static Comparator<DateTime> COMPARATOR = DateTime::compare;

  public static int compare(DateTime a, DateTime b) {
    return a.compareTo(b);
  }

  public static DateTime max(DateTime a, DateTime b) {
    return ObjectUtils.max(COMPARATOR, a, b);
  }

  public static DateTime min(DateTime a, DateTime b) {
    return ObjectUtils.min(COMPARATOR, a, b);
  }

  public static DateTime now() {
    return new DateTime(Clock.systemUTC().instant(), TimeZone.UTC);
  }

  public static DateTime now(TimeZone zone) {
    return new DateTime(Clock.systemUTC().instant(), zone);
  }

  public static DateTime nowSystem() {
    return now(TimeZone.system());
  }

  public static DateTime ofCalendar(java.util.Calendar calendar) {
    return ofCalendar(calendar, TimeZone.UTC);
  }

  public static DateTime ofCalendar(java.util.Calendar calendar, TimeZone zone) {
    return new DateTime(calendar.toInstant(), zone);
  }

  public static DateTime ofDate(java.sql.Date date) {
    return ofDate(date, TimeZone.UTC);
  }

  public static DateTime ofDate(java.sql.Date date, TimeZone zone) {
    LocalDateTime localDateTime = LocalDateTime.of(date.toLocalDate(), LocalTime.MIDNIGHT);
    return ofDateTime(localDateTime, zone);
  }

  public static DateTime ofDate(java.util.Date date) {
    return ofDate(date, TimeZone.UTC);
  }

  public static DateTime ofDate(java.util.Date date, TimeZone zone) {
    return new DateTime(date.toInstant(), zone);
  }

  public static DateTime ofDate(LocalDate date) {
    return ofDate(date, TimeZone.UTC);
  }

  public static DateTime ofDate(LocalDate date, TimeZone zone) {
    LocalDateTime localDateTime = LocalDateTime.of(date, LocalTime.MIDNIGHT);
    return ofDateTime(localDateTime, zone);
  }

  public static DateTime ofDateTime(int year, int month, int dayOfMonth, int hour,
    int minute, int second) {
    return ofDateTime(year, month, dayOfMonth, hour, minute, second, 0);
  }

  public static DateTime ofDateTime(int year, int month, int dayOfMonth, int hour,
    int minute, int second, int nanoOfSeconds) {
    return ofDateTime(year, month, dayOfMonth, hour, minute, second, nanoOfSeconds, TimeZone.UTC);
  }

  public static DateTime ofDateTime(int year, int month, int dayOfMonth, int hour,
    int minute, int second, int nanoOfSeconds, TimeZone zone) {
    LocalDateTime localDateTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSeconds);
    return ofDateTime(localDateTime, zone);
  }

  public static DateTime ofDateTime(int year, int month, int dayOfMonth, int hour,
    int minute, int second, TimeZone zone) {
    return ofDateTime(year, month, dayOfMonth, hour, minute, second, 0, zone);
  }

  public static DateTime ofDateTime(LocalDateTime localDateTime) {
    return ofDateTime(localDateTime, TimeZone.UTC);
  }

  public static DateTime ofDateTime(LocalDateTime localDateTime, TimeZone zone) {
    ZoneOffset zoneOffset = zone.toZoneId().getRules().getOffset(localDateTime);
    return new DateTime(localDateTime.toInstant(zoneOffset), zone);
  }

  public static DateTime ofDateTime(ZonedDateTime zonedDateTime) {
    TimeZone zone = TimeZone.of(zonedDateTime.getZone().getId());
    return ofDateTime(zonedDateTime.toLocalDateTime(), zone);
  }

  public static DateTime ofTime(int hour, int minute) {
    return ofTime(hour, minute, TimeZone.UTC);
  }

  public static DateTime ofTime(int hour, int minute, TimeZone zone) {
    return ofDateTime(1970, 1, 1, hour, minute, 0, 0, zone);
  }

  public static DateTime ofTime(java.sql.Time time) {
    return ofTime(time, TimeZone.UTC);
  }

  public static DateTime ofTime(java.sql.Time time, TimeZone zone) {
    LocalDateTime localDateTime = LocalDateTime.of(LocalDate.EPOCH, time.toLocalTime());
    return ofDateTime(localDateTime, zone);
  }

  public static DateTime ofTime(LocalTime time) {
    return ofTime(time, TimeZone.UTC);
  }

  public static DateTime ofTime(LocalTime time, TimeZone zone) {
    LocalDateTime localDateTime = LocalDateTime.of(LocalDate.EPOCH, time);
    return ofDateTime(localDateTime, zone);
  }

  public static DateTime ofTimestamp(Instant instant) {
    return new DateTime(instant, TimeZone.UTC);
  }

  public static DateTime ofTimestamp(Instant instant, TimeZone zone) {
    return new DateTime(instant, zone);
  }

  public static DateTime ofTimestamp(java.sql.Timestamp timestamp) {
    return ofTimestamp(timestamp, TimeZone.UTC);
  }

  public static DateTime ofTimestamp(java.sql.Timestamp timestamp, TimeZone zone) {
    return new DateTime(timestamp.toInstant(), zone);
  }

  public static DateTime ofTimestampMillis(long epochMillis) {
    return ofTimestampMillis(epochMillis, TimeZone.UTC);
  }

  public static DateTime ofTimestampMillis(long epochMillis, TimeZone zone) {
    return new DateTime(Instant.ofEpochMilli(epochMillis), zone);
  }

  public static DateTime ofTimestampSeconds(long epochSeconds) {
    return ofTimestampSeconds(epochSeconds, TimeZone.UTC);
  }

  public static DateTime ofTimestampSeconds(long epochSeconds, long nano) {
    return ofTimestampSeconds(epochSeconds, nano, TimeZone.UTC);
  }

  public static DateTime ofTimestampSeconds(long epochSeconds, long nano, TimeZone zone) {
    return new DateTime(Instant.ofEpochSecond(epochSeconds, nano), zone);
  }

  public static DateTime ofTimestampSeconds(long epochSeconds, TimeZone zone) {
    return new DateTime(Instant.ofEpochSecond(epochSeconds), zone);
  }

  /**
   * 2018-02-01 19:07:30
   */
  public static DateTime parse(String dateTime) {
    return parse(dateTime, TimeZone.UTC);
  }

  public static DateTime parse(String dateTime, DateTimeFormatter formatter) {
    ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateTime, formatter);
    return ofDateTime(zonedDateTime);
  }

  public static DateTime parse(String dateTime, DateTimeFormatter formatter, TimeZone zone) {
    LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
    return ofDateTime(localDateTime, zone);
  }

  /**
   * 2018-02-01 19:07:30
   */
  public static DateTime parse(String dateTime, TimeZone zone) {
    LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DateTime.FORMATTER_SIMPLE_DATE_TIME);
    return ofDateTime(localDateTime, zone);
  }

  /**
   * 2018-02-01
   */
  public static DateTime parseDate(String date) {
    return parseDate(date, TimeZone.UTC);
  }

  /**
   * 2018-02-01
   */
  public static DateTime parseDate(String date, TimeZone zone) {
    return parse(date + " 00:00:00", zone);
  }

  /**
   * 00:00:00 or 00:00
   */
  public static DateTime parseTime(String time) {
    return parseTime(time, TimeZone.UTC);
  }

  /**
   * 00:00:00 or 00:00
   */
  public static DateTime parseTime(String time, TimeZone zone) {
    if (time.length() == 5) {
      return parse(String.format("1970-01-01 %s:00", time), zone);
    }
    else {
      return parse("1970-01-01 " + time, zone);
    }
  }

  private final TimeZone zone;

  private final Instant timestamp;

  private DateTime(Instant timestamp, TimeZone zone) {
    this.zone = zone;
    this.timestamp = timestamp;
  }

  @Override
  public int compareTo(DateTime dateTime) {
    return this.timestamp.compareTo(dateTime.timestamp);
  }

  @Override
  public boolean equals(Object obj) {
    return Equals.check(this, obj, f -> new Object[] { f.timestamp });
  }

  public String format(DateTimeFormatter formatter) {
    return this.toZonedDateTime().format(formatter);
  }

  public int getDayOfMonth() {
    return this.toLocalDate().getDayOfMonth();
  }

  public int getDayOfWeek() {
    return this.toLocalDate().getDayOfWeek().getValue();
  }

  public int getDayOfYear() {
    return this.toLocalDate().getDayOfYear();
  }

  public int getHours() {
    return this.toLocalTime().getHour();
  }

  public int getMillis() {
    return this.getNano() / 1_000_000;
  }

  public int getMinutes() {
    return this.toLocalTime().getMinute();
  }

  public int getMonth() {
    return this.toLocalDate().getMonthValue();
  }

  public int getNano() {
    return this.toLocalTime().getNano();
  }

  @Copy
  public DateTime getPrevios(int dayOfWeek) {
    LocalDate cur = this.toLocalDate().minusDays(1);
    while (cur.getDayOfWeek().getValue() != dayOfWeek) {
      cur = cur.minusDays(1);
    }
    return this.withDate(cur);
  }

  public int getSeconds() {
    return this.toLocalTime().getSecond();
  }

  public int getYear() {
    return this.toLocalDateTime().getYear();
  }

  public TimeZone getZone() {
    return this.zone;
  }

  public int getZoneOffsetInHours() {
    return this.getZoneOffsetInSeconds() / 60 / 60;
  }

  public int getZoneOffsetInSeconds() {
    return this.zone.toZoneId().getRules().getOffset(this.timestamp).getTotalSeconds();
  }

  @Override
  public int hashCode() {
    return this.timestamp.hashCode();
  }

  public boolean isAfter(DateTime dateTime) {
    return this.timestamp.isAfter(dateTime.timestamp);
  }

  public boolean isBefore(DateTime dateTime) {
    return this.timestamp.isBefore(dateTime.timestamp);
  }

  public boolean isWeekend() {
    switch (this.toLocalDate().getDayOfWeek()) {
      case SATURDAY:
      case SUNDAY:
        return true;
      default:
        return false;
    }
  }

  @Copy
  public DateTime minus(TemporalAmount duration) {
    return ofDateTime(this.toLocalDateTime().minus(duration), this.zone);
  }

  @Copy
  public DateTime minusDays(long days) {
    return ofDateTime(this.toLocalDateTime().minusDays(days), this.zone);
  }

  @Copy
  public DateTime minusHours(long hours) {
    return ofDateTime(this.toLocalDateTime().minusHours(hours), this.zone);
  }

  @Copy
  public DateTime minusMinutes(long minutes) {
    return ofDateTime(this.toLocalDateTime().minusMinutes(minutes), this.zone);
  }

  @Copy
  public DateTime minusMonths(long months) {
    return ofDateTime(this.toLocalDateTime().minusMonths(months), this.zone);
  }

  @Copy
  public DateTime minusNano(long nanos) {
    return ofDateTime(this.toLocalDateTime().minusNanos(nanos), this.zone);
  }

  @Copy
  public DateTime minusSeconds(long seconds) {
    return ofDateTime(this.toLocalDateTime().minusSeconds(seconds), this.zone);
  }

  @Copy
  public DateTime minusYears(long years) {
    return ofDateTime(this.toLocalDateTime().minusYears(years), this.zone);
  }

  @Copy
  public DateTime plus(TemporalAmount duration) {
    return ofDateTime(this.toLocalDateTime().plus(duration), this.zone);
  }

  @Copy
  public DateTime plusDays(long days) {
    return ofDateTime(this.toLocalDateTime().plusDays(days), this.zone);
  }

  @Copy
  public DateTime plusHours(long hours) {
    return ofDateTime(this.toLocalDateTime().plusHours(hours), this.zone);
  }

  @Copy
  public DateTime plusMinutes(long minutes) {
    return ofDateTime(this.toLocalDateTime().plusMinutes(minutes), this.zone);
  }

  @Copy
  public DateTime plusMonths(long months) {
    return ofDateTime(this.toLocalDateTime().plusMonths(months), this.zone);
  }

  @Copy
  public DateTime plusNano(long nanos) {
    return ofDateTime(this.toLocalDateTime().plusNanos(nanos), this.zone);
  }

  @Copy
  public DateTime plusSeconds(long seconds) {
    return ofDateTime(this.toLocalDateTime().plusSeconds(seconds), this.zone);
  }

  @Copy
  public DateTime plusYears(long years) {
    return ofDateTime(this.toLocalDateTime().plusYears(years), this.zone);
  }

  public java.util.Date toDate() {
    return new java.util.Date(this.toUTCTimestampMilli());
  }

  public Instant toInstant() {
    return this.timestamp;
  }

  public LocalDate toLocalDate() {
    return LocalDate.ofInstant(this.timestamp, this.zone.toZoneId());
  }

  public LocalDateTime toLocalDateTime() {
    return LocalDateTime.ofInstant(this.timestamp, this.zone.toZoneId());
  }

  public LocalTime toLocalTime() {
    return LocalTime.ofInstant(this.timestamp, this.zone.toZoneId());
  }

  @JsonValue
  public String toRFC1123() {
    return DateTimeFormatter.RFC_1123_DATE_TIME.format(this.toZonedDateTime());
  }

  @Override
  public String toString() {
    return this.toRFC1123();
  }

  public String toString(String pattern) {
    return DateTimeFormatter.ofPattern(pattern).format(this.toZonedDateTime());
  }

  public DateTime toUTC() {
    if (this.zone.equals(TimeZone.UTC)) {
      return this;
    }
    else {
      return new DateTime(this.timestamp, TimeZone.UTC);
    }
  }

  public long toUTCTimestampMilli() {
    return this.timestamp.toEpochMilli();
  }

  public long toUTCTimestampSeconds() {
    return this.timestamp.getEpochSecond();
  }

  public ZonedDateTime toZonedDateTime() {
    return this.toZonedDateTime(this.zone);
  }

  public ZonedDateTime toZonedDateTime(TimeZone zone) {
    return ZonedDateTime.ofInstant(this.timestamp, zone.toZoneId());
  }

  @Copy
  public DateTime withDate(int year, int month) {
    return ofDateTime(this.toLocalDateTime().withYear(year).withMonth(month), this.zone);
  }

  @Copy
  public DateTime withDate(int year, int month, int dayOfMonth) {
    return ofDateTime(this.toLocalDateTime().withYear(year).withMonth(month).withDayOfMonth(dayOfMonth), this.zone);
  }

  @Copy
  public DateTime withDate(LocalDate date) {
    return ofDateTime(LocalDateTime.of(date, this.toLocalTime()), this.zone);
  }

  @Copy
  public DateTime withDayOfMonth(int dayOfMonth) {
    return ofDateTime(this.toLocalDateTime().withDayOfMonth(dayOfMonth), this.zone);
  }

  @Copy
  public DateTime withDayOfYear(int dayOfYear) {
    return ofDateTime(this.toLocalDateTime().withDayOfYear(dayOfYear), this.zone);
  }

  @Copy
  public DateTime withHour(int hour) {
    return ofDateTime(this.toLocalDateTime().withHour(hour), this.zone);
  }

  @Copy
  public DateTime withMillis(int millis) {
    return ofDateTime(this.toLocalDateTime().withNano(millis * 1_000_000), this.zone);
  }

  @Copy
  public DateTime withMinute(int minute) {
    return ofDateTime(this.toLocalDateTime().withMinute(minute), this.zone);
  }

  @Copy
  public DateTime withMonth(int month) {
    return ofDateTime(this.toLocalDateTime().withMonth(month), this.zone);
  }

  @Copy
  public DateTime withNano(int nano) {
    return ofDateTime(this.toLocalDateTime().withNano(nano), this.zone);
  }

  @Copy
  public DateTime withSecond(int second) {
    return ofDateTime(this.toLocalDateTime().withSecond(second), this.zone);
  }

  @Copy
  public DateTime withTime(int hour, int minute) {
    return ofDateTime(this.toLocalDateTime().withHour(hour).withMinute(minute), this.zone);
  }

  @Copy
  public DateTime withTime(int hour, int minute, int second) {
    return ofDateTime(this.toLocalDateTime().withHour(hour).withMinute(minute).withSecond(second), this.zone);
  }

  @Copy
  public DateTime withTime(int hour, int minute, int second, int nanoOfSecond) {
    return ofDateTime(LocalDateTime.of(this.toLocalDate(), LocalTime.of(hour, minute, second, nanoOfSecond)),
      this.zone);
  }

  @Copy
  public DateTime withTime(LocalTime time) {
    return ofDateTime(LocalDateTime.of(this.toLocalDate(), time), this.zone);
  }

  @Copy
  public DateTime withYear(int year) {
    return ofDateTime(this.toLocalDateTime().withYear(year), this.zone);
  }

  @Copy
  public DateTime withZone(TimeZone zone) {
    return new DateTime(this.timestamp, zone);
  }

}
