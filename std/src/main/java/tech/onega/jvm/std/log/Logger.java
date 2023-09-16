package tech.onega.jvm.std.log;

import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.lang.Lambda;

@ThreadSafe
public interface Logger {

  void debug(Object message);

  void debug(Object message, Object... params);

  void debug(Throwable throwable);

  void debug(Throwable throwable, Object message);

  void debug(Throwable throwable, Object message, Object... params);

  void error(Object message);

  void error(Object message, Object... params);

  void error(Throwable throwable);

  void error(Throwable throwable, Object message);

  void error(Throwable throwable, Object message, Object... params);

  String getName();

  default <E extends Throwable> void ifDebugEnabled(final Lambda.Consumer<Logger, E> lambda) throws E {
    if (this.isDebugEnabled()) {
      lambda.invoke(this);
    }
  }

  default <E extends Throwable> void ifErrorEnabled(final Lambda.Consumer<Logger, E> lambda) throws E {
    if (this.isErrorEnabled()) {
      lambda.invoke(this);
    }
  }

  default <E extends Throwable> void ifInfoEnabled(final Lambda.Consumer<Logger, E> lambda) throws E {
    if (this.isInfoEnabled()) {
      lambda.invoke(this);
    }
  }

  void info(Object message);

  void info(Object message, Object... params);

  void info(Throwable throwable);

  void info(Throwable throwable, Object message);

  void info(Throwable throwable, Object message, Object... params);

  boolean isDebugEnabled();

  boolean isErrorEnabled();

  boolean isInfoEnabled();

  boolean isLogLevelEnabled(LogLevel level);

  void log(int stackDepth, LogLevel level, Object message);

  void log(int stackDepth, LogLevel level, Object message, Object... params);

  void log(int stackDepth, LogLevel level, Throwable throwable);

  void log(int stackDepth, LogLevel level, Throwable throwable, Object message);

  void log(int stackDepth, LogLevel level, Throwable throwable, Object message, Object... params);

}
