package tech.onega.jvm.std.log;

import java.lang.StackWalker.StackFrame;
import javax.validation.constraints.NotNull;
import tech.onega.jvm.std.annotation.Immutable;
import tech.onega.jvm.std.struct.date.DateTime;
import tech.onega.jvm.std.struct.list.IList;

@Immutable
final public class LogMessage {

  private final LogLevel level;

  private final Throwable throwable;

  private final String message;

  private final DateTime created;

  private final IList<Object> params;

  private final StackFrame location;

  public LogMessage(
    final DateTime created,
    final LogLevel level,
    final Throwable throwable,
    final String message,
    final IList<Object> params,
    final StackFrame location) {
    this.created = created;
    this.level = level;
    this.throwable = throwable;
    this.location = location;
    this.params = params;
    this.message = message;
  }

  public DateTime getCreated() {
    return created;
  }

  public LogLevel getLevel() {
    return level;
  }

  public StackFrame getLocation() {
    return location;
  }

  public String getMessage() {
    return message;
  }

  @NotNull
  public IList<Object> getParams() {
    return params;
  }

  public Throwable getThrowable() {
    return throwable;
  }

}
