package tech.onega.jvm.std.struct.date;

import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.struct.string.Strings;

@ThreadSafe
final public class ISO8601 {

  private static class Spec {

    char sign;

    int year;

    int month;

    int day;

    int hour;

    int min;

    int sec;

    int nano;

    String zone;

  }

  private static void appendZeroPaddedInt(final StringBuilder buf, int n, final int precision) {
    if (n < 0) {
      buf.append('-');
      n = -n;
    }
    for (int exp = precision - 1; exp > 0; exp--) {
      if (n < Math.pow(10, exp)) {
        buf.append('0');
      }
      else {
        break;
      }
    }
    buf.append(n);
  }

  public static DateTime parse(final String text) {
    final Spec spec = parseImpl(text);
    final TimeZone zone = Strings.isBlank(spec.zone) ? TimeZone.UTC : TimeZone.of(spec.zone);
    final int year = (spec.sign == '-' || spec.year == 0) ? -(spec.year + 1) : spec.year;
    return DateTime.ofDateTime(year, spec.month, spec.day, spec.hour, spec.min, spec.sec, spec.nano, zone);
  }

  private static Spec parseImpl(final String text) {
    if (text == null) {
      throw new IllegalArgumentException("argument can not be null");
    }
    final Spec spec = new Spec();
    int start;
    if (text.startsWith("-")) {
      spec.sign = '-';
      start = 1;
    }
    else if (text.startsWith("+")) {
      spec.sign = '+';
      start = 1;
    }
    else {
      spec.sign = '+';
      start = 0;
    }
    spec.year = Integer.parseInt(text.substring(start, start + 4));
    start += 4;
    if (text.charAt(start) != '-') {
      return null;
    }
    start++;
    spec.month = Integer.parseInt(text.substring(start, start + 2));
    start += 2;
    if (text.charAt(start) != '-') {
      return null;
    }
    start++;
    spec.day = Integer.parseInt(text.substring(start, start + 2));
    start += 2;
    if (text.charAt(start) != 'T') {
      return null;
    }
    start++;
    spec.hour = Integer.parseInt(text.substring(start, start + 2));
    start += 2;
    if (text.charAt(start) != ':') {
      return null;
    }
    start++;
    spec.min = Integer.parseInt(text.substring(start, start + 2));
    start += 2;
    if (text.charAt(start) != ':') {
      return null;
    }
    start++;
    spec.sec = Integer.parseInt(text.substring(start, start + 2));
    start += 2;
    if (text.charAt(start) != '.') {
      return null;
    }
    start++;
    spec.nano = Integer.parseInt(text.substring(start, start + 3)) * 1_000_000;
    start += 3;
    if (text.charAt(start) == '+' || text.charAt(start) == '-') {
      spec.zone = "UTC" + text.substring(start);
    }
    else if (text.substring(start).equals("Z")) {
      spec.zone = "UTC";
    }
    else {
      return null;
    }
    return spec;
  }

  public static String toString(final DateTime time) throws IllegalArgumentException {
    if (time == null) {
      throw new IllegalArgumentException("argument can not be null");
    }
    final StringBuilder buf = new StringBuilder();
    appendZeroPaddedInt(buf, time.getYear(), 4);
    buf.append('-');
    appendZeroPaddedInt(buf, time.getMonth(), 2);
    buf.append('-');
    appendZeroPaddedInt(buf, time.getDayOfMonth(), 2);
    buf.append('T');
    appendZeroPaddedInt(buf, time.getHours(), 2);
    buf.append(':');
    appendZeroPaddedInt(buf, time.getMinutes(), 2);
    buf.append(':');
    appendZeroPaddedInt(buf, time.getSeconds(), 2);
    buf.append('.');
    appendZeroPaddedInt(buf, time.getMillis(), 3);
    final int offset = time.getZoneOffsetInHours();
    if (offset != 0) {
      final int hours = Math.abs(offset / (60 * 1000) / 60);
      final int minutes = Math.abs(offset / (60 * 1000) % 60);
      buf.append(offset < 0 ? '-' : '+');
      appendZeroPaddedInt(buf, hours, 2);
      buf.append(':');
      appendZeroPaddedInt(buf, minutes, 2);
    }
    else {
      buf.append('Z');
    }
    return buf.toString();
  }

}