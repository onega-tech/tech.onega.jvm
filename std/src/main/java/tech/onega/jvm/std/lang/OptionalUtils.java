package tech.onega.jvm.std.lang;

import java.util.Optional;
import tech.onega.jvm.std.struct.string.Strings;

final public class OptionalUtils {

  public static Optional<String> createOrBlank(final String value) {
    return Strings.isBlank(value) ? Optional.empty() : Optional.of(value);
  }

}
