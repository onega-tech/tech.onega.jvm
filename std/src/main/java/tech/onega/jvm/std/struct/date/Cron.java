package tech.onega.jvm.std.struct.date;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;
import tech.onega.jvm.std.lang.Equals;
import tech.onega.jvm.std.struct.hash.Hash;

// 0 0 * * * *          - the top of every hour of every day
// */10 * * * * *       - every ten seconds
// 0 0 8-10 * * *       - 8, 9 and 10 o'clock of every day
// 0 0/30 8-10 * * *    - 8:00, 8:30, 9:00, 9:30 and 10 o'clock every day
// 0 0 9-17 * * MON-FRI - on the hour nine-to-five weekdays
// 0 0 0 25 12 ?        - every Christmas Day at midnight
final public class Cron {

  private static String[] commaDelimitedListToStringArray(final String str) {
    return delimitedListToStringArray(str, ",");
  }

  private static String deleteAny(final String inString, final String charsToDelete) {
    if (!hasLength(inString) || !hasLength(charsToDelete)) {
      return inString;
    }
    final StringBuilder sb = new StringBuilder();
    for (int i = 0; i < inString.length(); i++) {
      final char c = inString.charAt(i);
      if (charsToDelete.indexOf(c) == -1) {
        sb.append(c);
      }
    }
    return sb.toString();
  }

  private static String[] delimitedListToStringArray(final String str, final String delimiter) {
    return delimitedListToStringArray(str, delimiter, null);
  }

  private static String[] delimitedListToStringArray(final String str, final String delimiter,
    final String charsToDelete) {
    if (str == null) {
      return new String[0];
    }
    if (delimiter == null) {
      return new String[] { str };
    }
    final List<String> result = new ArrayList<>();
    if ("".equals(delimiter)) {
      for (int i = 0; i < str.length(); i++) {
        result.add(deleteAny(str.substring(i, i + 1), charsToDelete));
      }
    }
    else {
      int pos = 0;
      int delPos;
      while ((delPos = str.indexOf(delimiter, pos)) != -1) {
        result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
        pos = delPos + delimiter.length();
      }
      if (str.length() > 0 && pos <= str.length()) {
        result.add(deleteAny(str.substring(pos), charsToDelete));
      }
    }
    return toStringArray(result);
  }

  private static int findNext(final BitSet bits, final int value, final Calendar calendar, final int field,
    final int nextField, final List<Integer> lowerOrders) {
    int nextValue = bits.nextSetBit(value);
    if (nextValue == -1) {
      calendar.add(nextField, 1);
      reset(calendar, Arrays.asList(field));
      nextValue = bits.nextSetBit(0);
    }
    if (nextValue != value) {
      calendar.set(field, nextValue);
      reset(calendar, lowerOrders);
    }
    return nextValue;
  }

  private static int findNextDay(
    final Calendar calendar,
    final BitSet daysOfMonth,
    final int dayOfMonth,
    final BitSet daysOfWeek,
    final int dayOfWeek,
    final List<Integer> resets,
    final String expression) {
    int dayOfMonthCur = dayOfMonth;
    int dayOfWeekCur = dayOfWeek;
    int count = 0;
    final int max = 366;
    while ((!daysOfMonth.get(dayOfMonthCur) || !daysOfWeek.get(dayOfWeekCur - 1)) && count++ < max) {
      calendar.add(Calendar.DAY_OF_MONTH, 1);
      dayOfMonthCur = calendar.get(Calendar.DAY_OF_MONTH);
      dayOfWeekCur = calendar.get(Calendar.DAY_OF_WEEK);
      reset(calendar, resets);
    }
    if (count >= max) {
      throw new IllegalStateException("Overflow in day for expression=" + expression);
    }
    return dayOfMonthCur;
  }

  private static int[] getRange(final String field, final int min, final int max) {
    final int[] result = new int[2];
    if (field.contains("*")) {
      result[0] = min;
      result[1] = max - 1;
      return result;
    }
    if (!field.contains("-")) {
      result[0] = result[1] = Integer.valueOf(field);
    }
    else {
      final String[] split = delimitedListToStringArray(field, "-");
      if (split.length > 2) {
        throw new IllegalArgumentException("Range has more than two fields: " + field);
      }
      result[0] = Integer.valueOf(split[0]);
      result[1] = Integer.valueOf(split[1]);
    }
    if (result[0] >= max || result[1] >= max) {
      throw new IllegalArgumentException("Range exceeds maximum (" + max + "): " + field);
    }
    if (result[0] < min || result[1] < min) {
      throw new IllegalArgumentException("Range less than minimum (" + min + "): " + field);
    }
    return result;
  }

  private static boolean hasLength(final CharSequence str) {
    return str != null && str.length() > 0;
  }

  private static boolean hasLength(final String str) {
    return hasLength((CharSequence) str);
  }

  public static Cron of(final String expression) {
    return new Cron(expression);
  }

  private static String replace(final String inString, final String oldPattern, final String newPattern) {
    if (!hasLength(inString) || !hasLength(oldPattern) || newPattern == null) {
      return inString;
    }
    final StringBuilder sb = new StringBuilder();
    int pos = 0;
    int index = inString.indexOf(oldPattern);
    final int patLen = oldPattern.length();
    while (index >= 0) {
      sb.append(inString.substring(pos, index));
      sb.append(newPattern);
      pos = index + patLen;
      index = inString.indexOf(oldPattern, pos);
    }
    sb.append(inString.substring(pos));
    return sb.toString();
  }

  private static String replaceOrdinals(final String value, final String commaSeparatedList) {
    String valueCur = value;
    final String[] list = commaDelimitedListToStringArray(commaSeparatedList);
    for (int i = 0; i < list.length; i++) {
      final String item = list[i].toUpperCase();
      valueCur = replace(valueCur.toUpperCase(), item, "" + i);
    }
    return valueCur;
  }

  private static void reset(final Calendar calendar, final List<Integer> fields) {
    for (final int field : fields) {
      calendar.set(field, field == Calendar.DAY_OF_MONTH ? 1 : 0);
    }
  }

  private static void setDays(final BitSet bits, final String field, final int max) {
    String fieldCur = field;
    if (fieldCur.contains("?")) {
      fieldCur = "*";
    }
    setNumberHits(bits, fieldCur, 0, max);
  }

  private static void setDaysOfMonth(final BitSet bits, final String field) {
    final int max = 31;
    setDays(bits, field, max + 1);
    bits.clear(0);
  }

  private static void setMonths(final BitSet bits, final String value) {
    String valueCur = value;
    final int max = 12;
    valueCur = replaceOrdinals(valueCur, "FOO,JAN,FEB,MAR,APR,MAY,JUN,JUL,AUG,SEP,OCT,NOV,DEC");
    final BitSet months = new BitSet(13);
    setNumberHits(months, valueCur, 1, max + 1);
    for (int i = 1; i <= max; i++) {
      if (months.get(i)) {
        bits.set(i - 1);
      }
    }
  }

  private static void setNumberHits(final BitSet bits, final String value, final int min, final int max) {
    final String[] fields = delimitedListToStringArray(value, ",");
    for (final String field : fields) {
      if (!field.contains("/")) {
        final int[] range = getRange(field, min, max);
        bits.set(range[0], range[1] + 1);
      }
      else {
        final String[] split = delimitedListToStringArray(field, "/");
        if (split.length > 2) {
          throw new IllegalArgumentException("Incrementer has more than two fields: " + field);
        }
        final int[] range = getRange(split[0], min, max);
        if (!split[0].contains("-")) {
          range[1] = max - 1;
        }
        final int delta = Integer.valueOf(split[1]);
        for (int i = range[0]; i <= range[1]; i += delta) {
          bits.set(i);
        }
      }
    }
  }

  private static String[] tokenizeToStringArray(final String expression, final String delimiters) {
    return tokenizeToStringArray(expression.trim(), delimiters, true, true);
  }

  private static String[] tokenizeToStringArray(
    final String str, final String delimiters, final boolean trimTokens, final boolean ignoreEmptyTokens) {
    if (str == null) {
      return null;
    }
    final StringTokenizer st = new StringTokenizer(str, delimiters);
    final List<String> tokens = new ArrayList<>();
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      if (trimTokens) {
        token = token.trim();
      }
      if (!ignoreEmptyTokens || token.length() > 0) {
        tokens.add(token);
      }
    }
    return toStringArray(tokens);
  }

  private static String[] toStringArray(final Collection<String> collection) {
    if (collection == null) {
      return null;
    }
    return collection.toArray(new String[collection.size()]);
  }

  public static boolean validate(final String expression) {
    try {
      new Cron(expression);
      return true;
    }
    catch (final Exception e) {
      return false;
    }
  }

  private final BitSet seconds = new BitSet(60);

  private final BitSet minutes = new BitSet(60);

  private final BitSet hours = new BitSet(24);

  private final BitSet daysOfWeek = new BitSet(7);

  private final BitSet daysOfMonth = new BitSet(31);

  private final BitSet months = new BitSet(12);

  private final String expression;

  private Cron(final String expression) throws IllegalArgumentException {
    final String[] fields = tokenizeToStringArray(expression, " ");
    if (fields.length != 6) {
      throw new IllegalArgumentException(
        String.format("cron expression must consist of 6 fields (found %d in %s)", fields.length, expression));
    }
    this.expression = expression;
    setNumberHits(seconds, fields[0], 0, 60);
    setNumberHits(minutes, fields[1], 0, 60);
    setNumberHits(hours, fields[2], 0, 24);
    setDaysOfMonth(daysOfMonth, fields[3]);
    setMonths(months, fields[4]);
    setDays(daysOfWeek, replaceOrdinals(fields[5], "SUN,MON,TUE,WED,THU,FRI,SAT"), 8);
    if (daysOfWeek.get(7)) {
      daysOfWeek.set(0);
      daysOfWeek.clear(7);
    }
  }

  private void doNext(final Calendar calendar, final int dot) {
    final List<Integer> resets = new ArrayList<>();
    final int second = calendar.get(Calendar.SECOND);
    final List<Integer> emptyList = Collections.emptyList();
    final int updateSecond = findNext(seconds, second, calendar, Calendar.SECOND, Calendar.MINUTE, emptyList);
    if (second == updateSecond) {
      resets.add(Calendar.SECOND);
    }
    final int minute = calendar.get(Calendar.MINUTE);
    final int updateMinute = findNext(minutes, minute, calendar, Calendar.MINUTE, Calendar.HOUR_OF_DAY, resets);
    if (minute == updateMinute) {
      resets.add(Calendar.MINUTE);
    }
    else {
      doNext(calendar, dot);
    }
    final int hour = calendar.get(Calendar.HOUR_OF_DAY);
    final int updateHour = findNext(hours, hour, calendar, Calendar.HOUR_OF_DAY, Calendar.DAY_OF_WEEK, resets);
    if (hour == updateHour) {
      resets.add(Calendar.HOUR_OF_DAY);
    }
    else {
      doNext(calendar, dot);
    }
    final int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
    final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
    final int updateDayOfMonth = findNextDay(calendar, daysOfMonth, dayOfMonth, daysOfWeek, dayOfWeek, resets,
      expression);
    if (dayOfMonth == updateDayOfMonth) {
      resets.add(Calendar.DAY_OF_MONTH);
    }
    else {
      doNext(calendar, dot);
    }
    final int month = calendar.get(Calendar.MONTH);
    final int updateMonth = findNext(months, month, calendar, Calendar.MONTH, Calendar.YEAR, resets);
    if (month != updateMonth) {
      if (calendar.get(Calendar.YEAR) - dot > 4) {
        throw new IllegalStateException("Invalid cron expression led to runaway search for next trigger");
      }
      doNext(calendar, dot);
    }
  }

  @Override
  public boolean equals(final Object obj) {
    return Equals.check(this, obj, f -> new Object[] { f.months, f.daysOfMonth, f.daysOfWeek, f.hours, f.minutes, f.seconds });
  }

  @Override
  public int hashCode() {
    return Hash.codes(months, daysOfMonth, daysOfWeek, hours, minutes, seconds);
  }

  public DateTime next(final DateTime time) {
    final java.util.TimeZone timeZoneUTC = java.util.TimeZone.getTimeZone(TimeZone.UTC.toZoneId());
    final Calendar calendar = new GregorianCalendar(timeZoneUTC);
    calendar.setTimeInMillis(time.toUTCTimestampMilli());
    doNext(calendar, calendar.get(Calendar.YEAR));
    return DateTime.ofTimestamp(calendar.toInstant(), time.getZone());
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + ": " + expression;
  }

}
