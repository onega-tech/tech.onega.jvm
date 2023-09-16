package org.apache.log4j;

import java.util.Enumeration;
import java.util.ResourceBundle;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;

@Deprecated
public class Category implements AppenderAttachable {

  protected static final int STACK_DEPTH = 1;

  @Deprecated
  public static Logger exists(final String name) {
    return LogManager.exists(name);
  }

  @SuppressWarnings("rawtypes")
  @Deprecated
  public static Enumeration getCurrentCategories() {
    return LogManager.getCurrentLoggers();
  }

  @Deprecated
  public static LoggerRepository getDefaultHierarchy() {
    return LogManager.getLoggerRepository();
  }

  @Deprecated
  public static Category getInstance(@SuppressWarnings("rawtypes") final Class clazz) {
    return LogManager.getLogger(clazz);
  }

  @Deprecated
  public static Category getInstance(final String name) {
    return LogManager.getLogger(name);
  }

  @Deprecated
  final public static Category getRoot() {
    return null;
  }

  private static tech.onega.jvm.std.log.LogLevel resolvePriority(final Priority priority) {
    if (priority == Priority.DEBUG) {
      return tech.onega.jvm.std.log.LogLevel.DEBUG;
    }
    else if (priority == Priority.ERROR || priority == Priority.FATAL) {
      return tech.onega.jvm.std.log.LogLevel.ERROR;
    }
    else if (priority == Priority.WARN || priority == Priority.INFO) {
      return tech.onega.jvm.std.log.LogLevel.INFO;
    }
    return null;
  }

  @Deprecated
  public static void shutdown() {
  }

  protected final tech.onega.jvm.std.log.Logger logger;

  protected Category(final tech.onega.jvm.std.log.Logger logger) {
    this.logger = logger;
  }

  @Override
  synchronized public void addAppender(final Appender newAppender) {
  }

  public void assertLog(final boolean assertion, final String msg) {
  }

  public void callAppenders(final LoggingEvent event) {
  }

  synchronized void closeNestedAppenders() {
  }

  public void debug(final Object message) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.DEBUG, message);
  }

  public void debug(final Object message, final Throwable t) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.DEBUG, t, message);
  }

  public void error(final Object message) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.ERROR, message);
  }

  public void error(final Object message, final Throwable t) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.ERROR, t, message);
  }

  public void fatal(final Object message) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.ERROR, message);
  }

  public void fatal(final Object message, final Throwable t) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.ERROR, t, message);
  }

  public boolean getAdditivity() {
    return false;
  }

  @SuppressWarnings("rawtypes")
  @Override
  synchronized public Enumeration getAllAppenders() {
    return null;
  }

  @Override
  synchronized public Appender getAppender(final String name) {
    return null;
  }

  @Deprecated
  public Priority getChainedPriority() {
    return null;
  }

  public Level getEffectiveLevel() {
    return null;
  }

  @Deprecated
  public LoggerRepository getHierarchy() {
    return null;
  }

  final public Level getLevel() {
    return null;
  }

  public LoggerRepository getLoggerRepository() {
    return null;
  }

  public final String getName() {
    return null;
  }

  final public Category getParent() {
    return null;
  }

  @Deprecated
  final public Level getPriority() {
    return null;
  }

  public ResourceBundle getResourceBundle() {
    return null;
  }

  public void info(final Object message) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.INFO, message);
  }

  public void info(final Object message, final Throwable t) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.INFO, t, message);
  }

  @Override
  public boolean isAttached(final Appender appender) {
    return false;
  }

  public boolean isDebugEnabled() {
    return this.logger.isDebugEnabled();
  }

  public boolean isEnabledFor(final Priority priority) {
    final tech.onega.jvm.std.log.LogLevel level = resolvePriority(priority);
    return level == null ? false : this.logger.isLogLevelEnabled(level);
  }

  public boolean isInfoEnabled() {
    return this.logger.isInfoEnabled();
  }

  public void l7dlog(final Priority priority, final String key, final Object[] params, final Throwable t) {
    final tech.onega.jvm.std.log.LogLevel level = resolvePriority(priority);
    if (level != null) {
      this.logger.log(STACK_DEPTH, level, t, key, params);
    }
  }

  public void l7dlog(final Priority priority, final String key, final Throwable t) {
    final tech.onega.jvm.std.log.LogLevel level = resolvePriority(priority);
    if (level != null) {
      this.logger.log(STACK_DEPTH, level, t, key);
    }
  }

  public void log(final Priority priority, final Object message) {
    final tech.onega.jvm.std.log.LogLevel level = resolvePriority(priority);
    if (level != null) {
      this.logger.log(STACK_DEPTH, level, message);
    }
  }

  public void log(final Priority priority, final Object message, final Throwable t) {
    final tech.onega.jvm.std.log.LogLevel level = resolvePriority(priority);
    if (level != null) {
      this.logger.log(STACK_DEPTH, level, t, message);
    }
  }

  public void log(final String callerFQCN, final Priority priority, final Object message, final Throwable t) {
    final tech.onega.jvm.std.log.LogLevel level = resolvePriority(priority);
    if (level != null) {
      this.logger.log(STACK_DEPTH, level, t, message);
    }
  }

  @Override
  synchronized public void removeAllAppenders() {
  }

  @Override
  synchronized public void removeAppender(final Appender appender) {
  }

  @Override
  synchronized public void removeAppender(final String name) {
  }

  public void setAdditivity(final boolean additive) {
  }

  final void setHierarchy(final LoggerRepository repository) {
  }

  public void setLevel(final Level level) {
  }

  @Deprecated
  public void setPriority(final Priority priority) {
  }

  public void setResourceBundle(final ResourceBundle bundle) {
  }

  public void warn(final Object message) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.INFO, message);
  }

  public void warn(final Object message, final Throwable t) {
    this.logger.log(STACK_DEPTH, tech.onega.jvm.std.log.LogLevel.INFO, t, message);
  }

}
