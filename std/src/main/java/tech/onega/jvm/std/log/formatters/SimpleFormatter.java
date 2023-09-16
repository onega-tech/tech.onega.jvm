package tech.onega.jvm.std.log.formatters;

import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.log.LogFormatter;
import tech.onega.jvm.std.log.LogMessage;
import tech.onega.jvm.std.struct.bytes.IBytes;
import tech.onega.jvm.std.struct.date.DateTime;
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.struct.string.Strings;

@ThreadSafe
final public class SimpleFormatter implements LogFormatter {

  public static void appendDateTime(final StringBuilder builder, final LogMessage message) {
    final DateTime date = message.getCreated();
    Strings.appendDigit(builder, date.getYear(), 4, false);
    builder.append('-');
    Strings.appendDigit(builder, date.getMonth(), 2, false);
    builder.append('-');
    Strings.appendDigit(builder, date.getDayOfMonth(), 2, false);
    builder.append(' ');
    Strings.appendDigit(builder, date.getHours(), 2, false);
    builder.append(':');
    Strings.appendDigit(builder, date.getMinutes(), 2, false);
    builder.append(':');
    Strings.appendDigit(builder, date.getSeconds(), 2, false);
    builder.append('.');
    Strings.appendDigit(builder, date.getMillis(), 3, false);
  }

  public static void appendIndent(final StringBuilder builder, final int length) {
    for (int i = 0; i < length; i++) {
      builder.append("  ");
    }
  }

  public static void appendLevel(final StringBuilder builder, final LogMessage message) {
    switch (message.getLevel()) {
      case DEBUG:
        builder.append("DEBUG");
        break;
      case ERROR:
        builder.append("ERROR");
        break;
      case INFO:
        builder.append("INFO ");
        break;
    }
  }

  public static void appendLocation(final StringBuilder builder, final LogMessage message) {
    builder.append(message.getLocation().getClassName());
    builder.append(':');
    builder.append(message.getLocation().getMethodName());
    builder.append(':');
    builder.append(message.getLocation().getLineNumber());
  }

  public static void appendLoggerMessage(final StringBuilder builder, final LogMessage logginMessage) {
    builder.append('[');
    appendDateTime(builder, logginMessage);
    builder.append("] [");
    appendLevel(builder, logginMessage);
    builder.append("] [");
    appendLocation(builder, logginMessage);
    builder.append("] - ");
    appendMessage(builder, logginMessage);
    appendThrowable(builder, logginMessage);
  }

  public static void appendMessage(final StringBuilder builder, final LogMessage logginMessage) {
    appendMessageImpl(builder, logginMessage.getMessage(), logginMessage.getParams());
  }

  public static void appendMessageImpl(final StringBuilder builder, final String message, final IList<Object> params) {
    if (Strings.isBlank(message)) {
      return;
    }
    if (params.isEmpty()) {
      builder.append(message);
      return;
    }
    final char[] letters = message.toCharArray();
    int paramIndex = 0;
    boolean pattern = false;
    for (int i = 0; i < letters.length; i++) {
      final char letter = letters[i];
      if (letter == '{') {
        pattern = true;
        continue;
      }
      else if (letter == '}' && pattern) {
        builder.append(params.get(paramIndex));
        pattern = false;
        paramIndex++;
        if (paramIndex == params.size()) {
          final int length = letters.length - (i + 1);
          if (length > 0) {
            builder.append(letters, (i + 1), length);
            break;
          }
        }
        continue;
      }
      else {
        builder.append(letter);
      }
    }
  }

  public static void appendThrowable(final StringBuilder builder, final LogMessage logginMessage) {
    if (logginMessage.getThrowable() == null) {
      return;
    }
    builder.append('\n');
    appendThrowable(builder, logginMessage.getThrowable(), 0);
  }

  public static void appendThrowable(final StringBuilder builder, final Throwable throwable, final int level) {
    builder
      .append(throwable.getClass().getCanonicalName())
      .append(": ")
      .append(throwable.getLocalizedMessage())
      .append('\n');
    for (final StackTraceElement stackTrace : throwable.getStackTrace()) {
      if (stackTrace.isNativeMethod()) {
        continue;
      }
      appendIndent(builder, level + 1);
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
      appendIndent(builder, level + 1);
      builder.append("Cause: ");
      appendThrowable(builder, cause, level + 1);
    }
  }

  @Override
  public IBytes format(final Iterable<LogMessage> messages, final int size) throws Exception {
    //get size
    int initSize = 0;
    for (final LogMessage message : messages) {
      initSize = initSize
        + (message.getMessage() == null ? 0 : message.getMessage().length())
        + 100
        + (message.getThrowable() != null ? 3000 : 0);
    }
    //format all
    final StringBuilder builder = new StringBuilder(initSize);
    for (final LogMessage message : messages) {
      appendLoggerMessage(builder, message);
      builder.append('\n');
    }
    return IBytes.wrap(builder.toString().getBytes());
  }

}
