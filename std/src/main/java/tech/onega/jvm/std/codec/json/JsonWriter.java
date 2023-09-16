package tech.onega.jvm.std.codec.json;

import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

/**
 * Simple json writer.
 */
final class JsonWriter {

  /**
   * Date ISO8601 utils.
   */
  private static class DateISO8601 {

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

    private static int getYear(final Calendar cal) throws IllegalArgumentException {
      int year = cal.get(Calendar.YEAR);
      if (cal.isSet(Calendar.ERA) && cal.get(Calendar.ERA) == GregorianCalendar.BC) {
        year = 0 - year + 1;
      }
      if (year > 9999 || year < -9999) {
        throw new IllegalArgumentException(
          "Calendar has more than four year digits, cannot be formatted as ISO8601: " + year);
      }
      return year;
    }

    /**
     * The format of the date/time string is: YYYY-MM-DDThh:mm:ss.SSSTZD
     * Note that we cannot use java.text.SimpleDateFormat for formatting
     * because it can't handle years <= 0 and TZD's
     */
    public static String toString(final Calendar cal) {
      if (cal == null) {
        throw new IllegalArgumentException("argument can not be null");
      }
      final StringBuilder buf = new StringBuilder();
      DateISO8601.appendZeroPaddedInt(buf, DateISO8601.getYear(cal), 4);
      buf.append('-');
      DateISO8601.appendZeroPaddedInt(buf, cal.get(Calendar.MONTH) + 1, 2);
      buf.append('-');
      DateISO8601.appendZeroPaddedInt(buf, cal.get(Calendar.DAY_OF_MONTH), 2);
      buf.append('T');
      DateISO8601.appendZeroPaddedInt(buf, cal.get(Calendar.HOUR_OF_DAY), 2);
      buf.append(':');
      DateISO8601.appendZeroPaddedInt(buf, cal.get(Calendar.MINUTE), 2);
      buf.append(':');
      DateISO8601.appendZeroPaddedInt(buf, cal.get(Calendar.SECOND), 2);
      buf.append('.');
      DateISO8601.appendZeroPaddedInt(buf, cal.get(Calendar.MILLISECOND), 3);
      final TimeZone tz = cal.getTimeZone();
      final int offset = tz.getOffset(cal.getTimeInMillis());
      if (offset != 0) {
        final int hours = Math.abs(offset / (60 * 1000) / 60);
        final int minutes = Math.abs(offset / (60 * 1000) % 60);
        buf.append(offset < 0 ? '-' : '+');
        DateISO8601.appendZeroPaddedInt(buf, hours, 2);
        buf.append(':');
        DateISO8601.appendZeroPaddedInt(buf, minutes, 2);
      }
      else {
        buf.append('Z');
      }
      return buf.toString();
    }

    public static String toString(final Date date) {
      final Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      return DateISO8601.toString(calendar);
    }

  }

  private final Writer writer;

  private final boolean htmlSafe;

  private final boolean longAsString;

  private final DecimalFormat decimalFormat;

  public JsonWriter(final Writer writer, final boolean htmlSafe, final boolean longAsString) {
    this.writer = writer;
    this.htmlSafe = htmlSafe;
    this.longAsString = longAsString;
    final DecimalFormatSymbols formatSymbol = DecimalFormatSymbols.getInstance();
    formatSymbol.setDecimalSeparator('.');
    this.decimalFormat = new DecimalFormat("#.#######################");
    this.decimalFormat.setDecimalFormatSymbols(formatSymbol);
  }

  public void write(final Object data) throws IOException {
    this.writeObject(data);
  }

  private void writeBoolean(final boolean value) throws IOException {
    this.writer.write(Boolean.valueOf(value).toString());
  }

  private void writeBooleanArray(final boolean[] value) throws IOException {
    this.writer.append('[');
    for (int i = 0; i < value.length; i++) {
      this.writeBoolean(value[i]);
      if (i < value.length - 1) {
        this.writer.append(',');
      }
    }
    this.writer.append(']');
  }

  private void writeChar(final char value) throws IOException {
    this.writeString(String.valueOf(value));
  }

  private void writeCharArray(final char[] value) throws IOException {
    this.writer.append('[');
    for (int i = 0; i < value.length; i++) {
      this.writeChar(value[i]);
      if (i < value.length - 1) {
        this.writer.append(',');
      }
    }
    this.writer.append(']');
  }

  private void writeDouble(final double value) throws IOException {
    this.writer.write(this.decimalFormat.format(value));
  }

  private void writeDoubleArray(final double[] value) throws IOException {
    this.writer.append('[');
    for (int i = 0; i < value.length; i++) {
      this.writeDouble(value[i]);
      if (i < value.length - 1) {
        this.writer.append(',');
      }
    }
    this.writer.append(']');
  }

  private void writeFloat(final float value) throws IOException {
    this.writer.write(this.decimalFormat.format(value));
  }

  private void writeFloatArray(final float[] value) throws IOException {
    this.writer.append('[');
    for (int i = 0; i < value.length; i++) {
      this.writeFloat(value[i]);
      if (i < value.length - 1) {
        this.writer.append(',');
      }
    }
    this.writer.append(']');
  }

  private void writeHex(final char c) throws IOException {
    this.writer.write(String.format("\\u%04x", (int) c));
  }

  private void writeInt(final int value) throws IOException {
    this.writer.write(Integer.toString(value));
  }

  private void writeIntArray(final int[] value) throws IOException {
    this.writer.append('[');
    for (int i = 0; i < value.length; i++) {
      this.writeInt(value[i]);
      if (i < value.length - 1) {
        this.writer.append(',');
      }
    }
    this.writer.append(']');
  }

  private void writeIterable(final Iterable<?> value) throws IOException {
    final Iterator<?> iterator = value.iterator();
    Object current = null;
    Object next = null;
    this.writer.append('[');
    while (true) {
      if (!iterator.hasNext()) {
        if (next != null) {
          this.writeObject(next);
        }
        break;
      }
      if (next != null) {
        current = next;
        next = iterator.next();
      }
      else {
        next = iterator.next();
        continue;
      }
      this.writeObject(current);
      this.writer.append(',');
    }
    this.writer.append(']');
  }

  private void writeLong(final long value) throws IOException {
    if (!this.longAsString) {
      this.writer.write(Long.toString(value));
    }
    else {
      this.writeString(Long.toString(value));
    }
  }

  private void writeLongArray(final long[] value) throws IOException {
    this.writer.append('[');
    for (int i = 0; i < value.length; i++) {
      this.writeLong(value[i]);
      if (i < value.length - 1) {
        this.writer.append(',');
      }
    }
    this.writer.append(']');
  }

  private void writeMap(final Map<?, ?> value) throws IOException {
    this.writer.append('{');
    boolean start = true;
    for (final Map.Entry<?, ?> entry : value.entrySet()) {
      if (entry.getValue() == null) {
        continue;
      }
      if (!start) {
        this.writer.append(',');
      }
      else {
        start = false;
      }
      this.writeString(entry.getKey().toString());
      this.writer.append(':');
      this.writeObject(entry.getValue());
    }
    this.writer.append('}');
  }

  private void writeNull() throws IOException {
    this.writer.append("null");
  }

  private void writeObject(final Object value) throws IOException {
    if (value == null) {
      this.writeNull();
    }
    else if (value instanceof Map) {
      this.writeMap((Map<?, ?>) value);
    }
    else if (value instanceof Iterable) {
      this.writeIterable((Iterable<?>) value);
    }
    else if (value instanceof Integer) {
      this.writeInt((int) value);
    }
    else if (value instanceof int[]) {
      this.writeIntArray((int[]) value);
    }
    else if (value instanceof Short) {
      this.writeShort((short) value);
    }
    else if (value instanceof short[]) {
      this.writeShortArray((short[]) value);
    }
    else if (value instanceof Long) {
      this.writeLong((long) value);
    }
    else if (value instanceof long[]) {
      this.writeLongArray((long[]) value);
    }
    else if (value instanceof Float) {
      this.writeFloat((float) value);
    }
    else if (value instanceof float[]) {
      this.writeFloatArray((float[]) value);
    }
    else if (value instanceof Boolean) {
      this.writeBoolean((boolean) value);
    }
    else if (value instanceof boolean[]) {
      this.writeBooleanArray((boolean[]) value);
    }
    else if (value instanceof Double) {
      this.writeDouble((double) value);
    }
    else if (value instanceof double[]) {
      this.writeDoubleArray((double[]) value);
    }
    else if (value instanceof Calendar) {
      this.writeString(DateISO8601.toString((Calendar) value));
    }
    else if (value instanceof Date) {
      this.writeString(DateISO8601.toString((Date) value));
    }
    else if (value instanceof Character) {
      this.writeChar((char) value);
    }
    else if (value instanceof char[]) {
      this.writeCharArray((char[]) value);
    }
    else if (value instanceof Object[]) {
      this.writeObjectArray((Object[]) value);
    }
    else {
      this.writeString(value.toString());
    }
  }

  private void writeObjectArray(final Object[] value) throws IOException {
    this.writer.append('[');
    for (int i = 0; i < value.length; i++) {
      this.writeObject(value[i]);
      if (i < value.length - 1) {
        this.writer.append(',');
      }
    }
    this.writer.append(']');
  }

  private void writeShort(final short value) throws IOException {
    this.writer.append(Short.toString(value));
  }

  private void writeShortArray(final short[] value) throws IOException {
    this.writer.append('[');
    for (int i = 0; i < value.length; i++) {
      this.writeShort(value[i]);
      if (i < value.length - 1) {
        this.writer.append(',');
      }
    }
    this.writer.append(']');
  }

  private void writeString(final String value) throws IOException {
    this.writer.append('"');
    final char[] chars = value.toCharArray();
    for (final char c : chars) {
      switch (c) {
        case '"':
        case '\\':
          this.writer.append('\\');
          this.writer.append(c);
          break;
        case '\t':
          this.writer.append('\\');
          this.writer.append('t');
          break;
        case '\b':
          this.writer.append('\\');
          this.writer.append('b');
          break;
        case '\n':
          this.writer.append('\\');
          this.writer.append('n');
          break;
        case '\r':
          this.writer.append('\\');
          this.writer.append('r');
          break;
        case '\f':
          this.writer.append('\\');
          this.writer.append('f');
          break;
        case '<':
        case '>':
        case '&':
        case '=':
        case '\'':
          if (this.htmlSafe) {
            this.writeHex(c);
          }
          else {
            this.writer.append(c);
          }
          break;
        case '\u2028':
        case '\u2029':
          this.writeHex(c);
          break;
        default:
          if (c <= 0x1F) {
            this.writeHex(c);
          }
          else {
            this.writer.append(c);
          }
          break;
      }
    }
    this.writer.append('"');
  }

}
