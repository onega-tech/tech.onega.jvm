package org.slf4j;

@SuppressWarnings("deprecation")
final class LoggerImpl implements org.slf4j.Logger {

  protected static final int STACK_DEPTH = 1;

  private final tech.onega.jvm.std.log.Logger logger;

  public LoggerImpl(final tech.onega.jvm.std.log.Logger logger) {
    this.logger = logger;
  }

  @Override
  public void debug(final Marker marker, final String msg) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.DEBUG, msg);
  }

  @Override
  public void debug(final Marker marker, final String format, final Object arg) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.DEBUG, format, arg);
  }

  @Override
  public void debug(final Marker marker, final String format, final Object... arguments) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.DEBUG, format, arguments);
  }

  @Override
  public void debug(final Marker marker, final String format, final Object arg1, final Object arg2) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.DEBUG, format, arg1, arg2);
  }

  @Override
  public void debug(final Marker marker, final String msg, final Throwable t) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.DEBUG, t, msg);
  }

  @Override
  public void debug(final String msg) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.DEBUG, msg);
  }

  @Override
  public void debug(final String format, final Object arg) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.DEBUG, format, arg);
  }

  @Override
  public void debug(final String format, final Object... arguments) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.DEBUG, format, arguments);
  }

  @Override
  public void debug(final String format, final Object arg1, final Object arg2) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.DEBUG, format, arg1, arg2);
  }

  @Override
  public void debug(final String msg, final Throwable t) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.DEBUG, t, msg);
  }

  @Override
  public void error(final Marker marker, final String msg) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.ERROR, msg);
  }

  @Override
  public void error(final Marker marker, final String format, final Object arg) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.ERROR, format, arg);
  }

  @Override
  public void error(final Marker marker, final String format, final Object... arguments) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.ERROR, format, format, arguments);
  }

  @Override
  public void error(final Marker marker, final String format, final Object arg1, final Object arg2) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.ERROR, format, format, arg1, arg2);
  }

  @Override
  public void error(final Marker marker, final String msg, final Throwable t) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.ERROR, t, msg);
  }

  @Override
  public void error(final String msg) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.ERROR, msg);
  }

  @Override
  public void error(final String format, final Object arg) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.ERROR, format, arg);
  }

  @Override
  public void error(final String format, final Object... arguments) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.ERROR, format, arguments);
  }

  @Override
  public void error(final String format, final Object arg1, final Object arg2) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.ERROR, format, arg1, arg2);
  }

  @Override
  public void error(final String msg, final Throwable t) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.ERROR, t, msg);
  }

  @Override
  public String getName() {
    return this.logger.getName();
  }

  @Override
  public void info(final Marker marker, final String msg) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.INFO, msg);
  }

  @Override
  public void info(final Marker marker, final String format, final Object arg) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.INFO, format, arg);
  }

  @Override
  public void info(final Marker marker, final String format, final Object... arguments) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.INFO, format, arguments);
  }

  @Override
  public void info(final Marker marker, final String format, final Object arg1, final Object arg2) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.INFO, format, arg1, arg2);
  }

  @Override
  public void info(final Marker marker, final String msg, final Throwable t) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.INFO, t, msg);
  }

  @Override
  public void info(final String msg) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.INFO, msg);
  }

  @Override
  public void info(final String format, final Object arg) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.INFO, format, arg);
  }

  @Override
  public void info(final String format, final Object... arguments) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.INFO, format, arguments);
  }

  @Override
  public void info(final String format, final Object arg1, final Object arg2) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.INFO, format, arg1, arg2);
  }

  @Override
  public void info(final String msg, final Throwable t) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.INFO, t, msg);
  }

  @Override
  public boolean isDebugEnabled() {
    return this.logger.isDebugEnabled();
  }

  @Override
  public boolean isDebugEnabled(final Marker marker) {
    return this.logger.isDebugEnabled();
  }

  @Override
  public boolean isErrorEnabled() {
    return this.logger.isErrorEnabled();
  }

  @Override
  public boolean isErrorEnabled(final Marker marker) {
    return this.logger.isErrorEnabled();
  }

  @Override
  public boolean isInfoEnabled() {
    return this.logger.isInfoEnabled();
  }

  @Override
  public boolean isInfoEnabled(final Marker marker) {
    return this.logger.isInfoEnabled();
  }

  @Override
  public boolean isTraceEnabled() {
    return this.logger.isDebugEnabled();
  }

  @Override
  public boolean isTraceEnabled(final Marker marker) {
    return this.logger.isDebugEnabled();
  }

  @Override
  public boolean isWarnEnabled() {
    return this.logger.isInfoEnabled();
  }

  @Override
  public boolean isWarnEnabled(final Marker marker) {
    return this.logger.isInfoEnabled();
  }

  @Override
  public void trace(final Marker marker, final String msg) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.DEBUG, msg);
  }

  @Override
  public void trace(final Marker marker, final String format, final Object arg) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.DEBUG, format, arg);
  }

  @Override
  public void trace(final Marker marker, final String format, final Object... argArray) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.DEBUG, format, argArray);
  }

  @Override
  public void trace(final Marker marker, final String format, final Object arg1, final Object arg2) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.DEBUG, format, arg1, arg2);
  }

  @Override
  public void trace(final Marker marker, final String msg, final Throwable t) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.DEBUG, t, msg);
  }

  @Override
  public void trace(final String msg) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.DEBUG, msg);
  }

  @Override
  public void trace(final String format, final Object arg) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.DEBUG, format, arg);
  }

  @Override
  public void trace(final String format, final Object... arguments) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.DEBUG, format, arguments);
  }

  @Override
  public void trace(final String format, final Object arg1, final Object arg2) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.DEBUG, format, arg1, arg2);
  }

  @Override
  public void trace(final String msg, final Throwable t) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.DEBUG, t, msg);
  }

  @Override
  public void warn(final Marker marker, final String msg) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.INFO, msg);
  }

  @Override
  public void warn(final Marker marker, final String format, final Object arg) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.INFO, format, arg);
  }

  @Override
  public void warn(final Marker marker, final String format, final Object... arguments) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.INFO, format, arguments);
  }

  @Override
  public void warn(final Marker marker, final String format, final Object arg1, final Object arg2) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.INFO, format, arg1, arg2);
  }

  @Override
  public void warn(final Marker marker, final String msg, final Throwable t) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.INFO, t, msg);
  }

  @Override
  public void warn(final String msg) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.INFO, msg);
  }

  @Override
  public void warn(final String format, final Object arg) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.INFO, format, arg);
  }

  @Override
  public void warn(final String format, final Object... arguments) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.INFO, format, arguments);
  }

  @Override
  public void warn(final String format, final Object arg1, final Object arg2) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.INFO, format, arg1, arg2);
  }

  @Override
  public void warn(final String msg, final Throwable t) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.INFO, t, msg);
  }

}
