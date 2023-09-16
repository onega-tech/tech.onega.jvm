package tech.onega.jvm.std.log.appenders.console;

import com.fasterxml.jackson.annotation.JsonValue;
import tech.onega.jvm.std.log.LogAppender;
import tech.onega.jvm.std.log.LogFormatter;
import tech.onega.jvm.std.log.LogLevel;
import tech.onega.jvm.std.log.LogMessage;
import tech.onega.jvm.std.log.formatters.SimpleFormatter;
import tech.onega.jvm.std.struct.bytes.IBytes;
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.struct.map.IMap;

final public class ConsoleAppender implements LogAppender {

  public static ConsoleAppender create(final String name, final IMap<String, String> propeties) {
    return new ConsoleAppender(name, new SimpleFormatter(), propeties);
  }

  public static ConsoleAppender createDefault() {
    return new ConsoleAppender("console", new SimpleFormatter(), IMap.empty());
  }

  private final String name;

  private final LogFormatter formatter;

  public ConsoleAppender(
    final String name,
    final LogFormatter formatter,
    final IMap<String, String> props) {
    this.name = name;
    this.formatter = formatter;
  }

  @Override
  public void append(final LogMessage message) {
    try {
      final IBytes data = this.formatter.format(IList.of(message), 1);
      if (message.getLevel() == LogLevel.DEBUG || message.getLevel() == LogLevel.INFO) {
        synchronized (System.out) {
          data.asReader().writeToStream(System.out);
        }
      }
      else {
        synchronized (System.err) {
          data.asReader().writeToStream(System.err);
        }
      }
    }
    catch (final Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void close() {
  }

  @Override
  @JsonValue
  public String toString() {
    return this.getClass().getName() + "#" + this.name;
  }

}
