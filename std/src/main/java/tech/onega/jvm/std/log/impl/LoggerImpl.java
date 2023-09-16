package tech.onega.jvm.std.log.impl;

import java.lang.StackWalker.StackFrame;
import java.util.concurrent.atomic.AtomicReference;
import tech.onega.jvm.std.annotation.Immutable;
import tech.onega.jvm.std.annotation.Nullable;
import tech.onega.jvm.std.log.LogAppender;
import tech.onega.jvm.std.log.LogLevel;
import tech.onega.jvm.std.log.LogMessage;
import tech.onega.jvm.std.log.Logger;
import tech.onega.jvm.std.log.LoggerContext;
import tech.onega.jvm.std.log.LoggerFactory;
import tech.onega.jvm.std.struct.date.DateTime;
import tech.onega.jvm.std.struct.list.IList;

@Immutable
final public class LoggerImpl implements Logger {

  private static final int STACK_DEPTH = 1;

  private final LoggerFactory factory;

  private final AtomicReference<LoggerContext> contextRef;

  private final String name;

  public LoggerImpl(final LoggerFactory factory, final String name) {
    this.factory = factory;
    contextRef = new AtomicReference<>(null);
    this.name = name;
  }

  @Override
  public void debug(final Object message) {
    logImpl(1, LogLevel.DEBUG, null, message, null);
  }

  @Override
  public void debug(final Object message, final Object... params) {
    logImpl(1, LogLevel.DEBUG, null, message, params);
  }

  @Override
  public void debug(final Throwable throwable) {
    logImpl(1, LogLevel.DEBUG, throwable, null, null);
  }

  @Override
  public void debug(final Throwable throwable, final Object message) {
    logImpl(1, LogLevel.DEBUG, throwable, message, null);
  }

  @Override
  public void debug(final Throwable throwable, final Object message, final Object... params) {
    logImpl(1, LogLevel.DEBUG, throwable, message, params);
  }

  @Override
  public void error(final Object message) {
    logImpl(1, LogLevel.ERROR, null, message, null);
  }

  @Override
  public void error(final Object message, final Object... params) {
    logImpl(1, LogLevel.ERROR, null, message, params);
  }

  @Override
  public void error(final Throwable throwable) {
    logImpl(1, LogLevel.ERROR, throwable, null, null);
  }

  @Override
  public void error(final Throwable throwable, final Object message) {
    logImpl(1, LogLevel.ERROR, throwable, message, null);
  }

  @Override
  public void error(final Throwable throwable, final Object message, final Object... params) {
    logImpl(1, LogLevel.ERROR, throwable, message, params);
  }

  @Nullable
  private LoggerContext getContext() {
    LoggerContext context = contextRef.get();
    if (context == null || context.getVersion() != factory.getVersion()) {
      context = factory.createContext(name);
      contextRef.set(context);
    }
    return context;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void info(final Object message) {
    logImpl(1, LogLevel.INFO, null, message, null);
  }

  @Override
  public void info(final Object message, final Object... params) {
    logImpl(1, LogLevel.INFO, null, message, params);
  }

  @Override
  public void info(final Throwable throwable) {
    logImpl(1, LogLevel.INFO, throwable, null, null);
  }

  @Override
  public void info(final Throwable throwable, final Object message) {
    logImpl(1, LogLevel.INFO, throwable, message, null);
  }

  @Override
  public void info(final Throwable throwable, final Object message, final Object... params) {
    logImpl(1, LogLevel.INFO, throwable, message, params);
  }

  @Override
  public boolean isDebugEnabled() {
    final LoggerContext context = getContext();
    return context == null ? false : context.isEnabled(LogLevel.DEBUG);
  }

  @Override
  public boolean isErrorEnabled() {
    final LoggerContext context = getContext();
    return context == null ? false : context.isEnabled(LogLevel.ERROR);
  }

  @Override
  public boolean isInfoEnabled() {
    final LoggerContext context = getContext();
    return context == null ? false : context.isEnabled(LogLevel.INFO);
  }

  @Override
  public boolean isLogLevelEnabled(final LogLevel level) {
    return getContext().isEnabled(level);
  }

  @Override
  public void log(final int stackDepth, final LogLevel level, final Object message) {
    logImpl(stackDepth + 1, level, null, message, null);
  }

  @Override
  public void log(final int stackDepth, final LogLevel level, final Object message, final Object... params) {
    logImpl(stackDepth + 1, level, null, message, params);
  }

  @Override
  public void log(final int stackDepth, final LogLevel level, final Throwable throwable) {
    logImpl(stackDepth + 1, level, throwable, null, null);
  }

  @Override
  public void log(final int stackDepth, final LogLevel level, final Throwable throwable, final Object message) {
    logImpl(stackDepth + 1, level, throwable, message, null);
  }

  @Override
  public void log(final int stackDepth, final LogLevel level, final Throwable throwable, final Object message,
    final Object... params) {
    logImpl(stackDepth + 1, level, throwable, message, params);
  }

  private void logImpl(final int stackDepth, final LogLevel level, final Throwable throwable, final Object message,
    final Object[] params) {
    final LoggerContext context = getContext();
    if (context == null || !context.isEnabled(level)) {
      return;
    }
    final IList<LogAppender> appenders = context.getAppenders(level);
    if (appenders.isEmpty()) {
      return;
    }
    final StackFrame location = StackWalker.getInstance().walk(s -> s.skip(stackDepth + STACK_DEPTH).findAny().get());
    final String mes = message != null ? String.valueOf(message) : throwable != null ? throwable.getMessage() : "";
    final LogMessage logMessage = new LogMessage(DateTime.now(), level, throwable, mes, IList.of(params), location);
    for (final LogAppender appender : appenders) {
      try {
        appender.append(logMessage);
      }
      catch (final Exception e) {
        e.printStackTrace();
      }
    }
  }

}
