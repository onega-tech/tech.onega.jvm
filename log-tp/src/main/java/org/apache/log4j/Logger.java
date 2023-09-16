package org.apache.log4j;

import org.apache.log4j.spi.LoggerFactory;

@Deprecated
final public class Logger extends Category {

  public static Logger getLogger(final Class<?> clazz) {
    return LogManager.getLogger(clazz.getName());
  }

  public static Logger getLogger(final String name) {
    return LogManager.getLogger(name);
  }

  public static Logger getLogger(final String name, final LoggerFactory factory) {
    return LogManager.getLogger(name, factory);
  }

  public static Logger getRootLogger() {
    return LogManager.getRootLogger();
  }

  Logger(final tech.onega.jvm.std.log.Logger logger) {
    super(logger);
  }

  public boolean isTraceEnabled() {
    return this.isDebugEnabled();
  }

  public void trace(final Object message) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.DEBUG, message);
  }

  public void trace(final Object message, final Throwable t) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.DEBUG, t, message);
  }

}