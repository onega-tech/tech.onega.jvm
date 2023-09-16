package tech.onega.jvm.std.lang;

import java.time.Instant;

public class InstantUtils {

  public static Instant timestamp() {
    return Instant.ofEpochMilli(System.currentTimeMillis());
  }

}
