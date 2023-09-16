package tech.onega.jvm.std.struct.date;

import tech.onega.jvm.std.annotation.Immutable;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.lang.Equals;
import tech.onega.jvm.std.struct.hash.Hash;

@Immutable
@ThreadSafe
final public class DateTimeInterval {

  public static DateTimeInterval of(final DateTime start) {
    return new DateTimeInterval(start, null);
  }

  public static DateTimeInterval of(final DateTime start, final DateTime end) {
    return new DateTimeInterval(start, end);
  }

  private final DateTime start;

  private final DateTime end;

  private DateTimeInterval(final DateTime start, final DateTime end) {
    if (start != null && end != null && start.compareTo(end) <= 0) {
      this.start = start;
      this.end = end;
    }
    else {
      this.start = end;
      this.end = start;
    }
  }

  public DateTime end() {
    return end;
  }

  @Override
  public boolean equals(final Object obj) {
    return Equals.check(this, obj, f -> new Object[] { f.start, f.end });
  }

  public DateTime from() {
    return start;
  }

  @Override
  public int hashCode() {
    return Hash.codes(start, end);
  }

  public boolean inInterval(final DateTime time) {
    if (start != null && start.compareTo(time) > 0) {
      return false;
    }
    else if (end != null && end.compareTo(time) < 0) {
      return false;
    }
    return true;
  }

  public boolean inInterval(final DateTimeInterval another) {
    return another.start.compareTo(start) >= 0 && another.end.compareTo(end) <= 0;
  }

  @Override
  public String toString() {
    return new StringBuilder()
      .append(String.valueOf(start))
      .append(" - ")
      .append(String.valueOf(end))
      .toString();
  }

}
