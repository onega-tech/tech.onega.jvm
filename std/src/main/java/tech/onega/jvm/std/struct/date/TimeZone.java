package tech.onega.jvm.std.struct.date;

import java.time.ZoneId;
import com.fasterxml.jackson.annotation.JsonValue;
import tech.onega.jvm.std.lang.Equals;
import tech.onega.jvm.std.struct.cache.Cache;
import tech.onega.jvm.std.struct.string.Strings;

final public class TimeZone {

  private static final Cache<String, TimeZone, RuntimeException> ZONE_CACHE = Cache.copyOnWrite(128, 1024, name -> {
    ZoneId zoneId = null;
    try {
      zoneId = java.util.TimeZone.getTimeZone(name).toZoneId();
    }
    catch (final Exception e) {
      throw new IllegalStateException(String.format("Zone with name %s not found", name), e);
    }
    if (zoneId == null) {
      throw new IllegalStateException(String.format("Zone with name %s not found", name));
    }
    return new TimeZone(zoneId);
  });

  public static final TimeZone UTC = of("UTC");

  public static TimeZone of(final String name) {
    return ZONE_CACHE.get(name);
  }

  public static TimeZone of(final ZoneId zoneId) {
    return of(zoneId.getId());
  }

  public static TimeZone system() {
    String userTimeZone = System.getProperty("user.timezone");
    if (Strings.isBlank(userTimeZone)) {
      userTimeZone = ZoneId.systemDefault().getId();
    }
    return of(userTimeZone);
  }

  private final ZoneId zoneId;

  private TimeZone(final ZoneId zoneId) {
    this.zoneId = zoneId;
  }

  @Override
  public boolean equals(final Object obj) {
    return Equals.check(this, obj, f -> new Object[] { f.zoneId });
  }

  @Override
  public int hashCode() {
    return zoneId.hashCode();
  }

  @JsonValue
  @Override
  public String toString() {
    return zoneId.toString();
  }

  public ZoneId toZoneId() {
    return zoneId;
  }

}
