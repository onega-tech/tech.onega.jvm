package org.apache.commons.logging;

@Deprecated
final class LogImpl implements Log {

  private static final int STACK_DEPTH = 1;

  private final tech.onega.jvm.std.log.Logger logger;

  public LogImpl(final tech.onega.jvm.std.log.Logger logger) {
    this.logger = logger;
  }

  @Override
  public void debug(final Object message) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.DEBUG, message);
  }

  @Override
  public void debug(final Object message, final Throwable t) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.DEBUG, t, message);
  }

  @Override
  public void error(final Object message) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.ERROR, message);
  }

  @Override
  public void error(final Object message, final Throwable t) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.ERROR, t, message);
  }

  @Override
  public void fatal(final Object message) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.ERROR, message);
  }

  @Override
  public void fatal(final Object message, final Throwable t) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.ERROR, t, message);
  }

  @Override
  public void info(final Object message) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.INFO, message);
  }

  @Override
  public void info(final Object message, final Throwable t) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.INFO, t, message);
  }

  @Override
  public boolean isDebugEnabled() {
    return this.logger.isDebugEnabled();
  }

  @Override
  public boolean isErrorEnabled() {
    return this.logger.isErrorEnabled();
  }

  @Override
  public boolean isFatalEnabled() {
    return this.isErrorEnabled();
  }

  @Override
  public boolean isInfoEnabled() {
    return this.logger.isInfoEnabled();
  }

  @Override
  public boolean isTraceEnabled() {
    return this.isDebugEnabled();
  }

  @Override
  public boolean isWarnEnabled() {
    return this.isInfoEnabled();
  }

  @Override
  public void trace(final Object message) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.DEBUG, message);
  }

  @Override
  public void trace(final Object message, final Throwable t) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.DEBUG, t, message);
  }

  @Override
  public void warn(final Object message) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.INFO, message);
  }

  @Override
  public void warn(final Object message, final Throwable t) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.INFO, t, message);
  }

}
