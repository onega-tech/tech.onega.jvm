package tech.onega.jvm.std.lang;

final public class ThrowableUtils {

  public static void append(final StringBuilder builder, final Throwable throwable) {
    append(builder, throwable, 0);
  }

  public static void append(final StringBuilder builder, final Throwable throwable, final int level) {
    writeSpace(level, builder);
    if (level > 0) {
      builder.append("Cause: ");
    }
    builder
      .append(throwable.getClass().getCanonicalName())
      .append(": ")
      .append(throwable.getLocalizedMessage())
      .append('\n');
    for (final StackTraceElement stackTrace : throwable.getStackTrace()) {
      if (stackTrace.isNativeMethod()) {
        continue;
      }
      writeSpace(level + 1, builder);
      builder
        .append(stackTrace.getClassName())
        .append('.')
        .append(stackTrace.getMethodName())
        .append(": ")
        .append(stackTrace.getLineNumber())
        .append('\n');
    }
    final Throwable cause = throwable.getCause();
    if (cause != null) {
      append(builder, cause, level + 1);
    }
  }

  public static String toString(final Throwable throwable) {
    final StringBuilder builder = new StringBuilder();
    append(builder, throwable, 0);
    return builder.toString();
  }

  private static void writeSpace(final int repeat, final StringBuilder builder) {
    for (int i = 0; i < repeat; i++) {
      builder.append(' ');
    }
  }

}
