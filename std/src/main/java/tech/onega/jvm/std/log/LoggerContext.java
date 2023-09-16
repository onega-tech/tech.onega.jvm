package tech.onega.jvm.std.log;

import tech.onega.jvm.std.annotation.Immutable;
import tech.onega.jvm.std.struct.list.IList;

@Immutable
final public class LoggerContext {

  private final String name;

  private final long version;

  private final IList<LogAppender> debugAppenders;

  private final IList<LogAppender> infoAppenders;

  private final IList<LogAppender> errorAppenders;

  public LoggerContext(
    final String name,
    final long version,
    final IList<LogAppender> debugAppenders,
    final IList<LogAppender> infoAppenders,
    final IList<LogAppender> errorAppenders) {
    this.name = name;
    this.version = version;
    this.debugAppenders = debugAppenders == null ? IList.empty() : debugAppenders;
    this.infoAppenders = infoAppenders == null ? IList.empty() : infoAppenders;
    this.errorAppenders = errorAppenders == null ? IList.empty() : errorAppenders;
  }

  public IList<LogAppender> getAppenders(final LogLevel level) {
    switch (level) {
      case DEBUG:
        return debugAppenders;
      case ERROR:
        return errorAppenders;
      case INFO:
        return infoAppenders;
    }
    return null;
  }

  public String getName() {
    return name;
  }

  public long getVersion() {
    return version;
  }

  public boolean isEnabled(final LogLevel level) {
    switch (level) {
      case DEBUG:
        return debugAppenders.isNotEmpty();
      case ERROR:
        return errorAppenders.isNotEmpty();
      case INFO:
        return infoAppenders.isNotEmpty();
    }
    return false;
  }

}
