package tech.onega.jvm.std.log.jul;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.LogRecord;
import tech.onega.jvm.std.log.LogLevel;
import tech.onega.jvm.std.log.Logger;
import tech.onega.jvm.std.log.Loggers;

final public class JulHandler extends java.util.logging.Handler {

  private static final JulHandler INSTANCE = new JulHandler();

  private static final int STACK_DEPTH = 6;

  public static void install() {
    final java.util.logging.Logger rootLogger = java.util.logging.LogManager.getLogManager().getLogger("");
    boolean installed = false;
    for (final java.util.logging.Handler handler : rootLogger.getHandlers()) {
      if (handler == INSTANCE) {
        installed = true;
      }
      else {
        rootLogger.removeHandler(handler);
      }
    }
    if (!installed) {
      rootLogger.addHandler(INSTANCE);
    }
  }

  private static LogLevel resolveLevel(final java.util.logging.Level level) {
    if (level.intValue() < java.util.logging.Level.CONFIG.intValue()) {
      return LogLevel.DEBUG;
    }
    else if (level.intValue() >= java.util.logging.Level.CONFIG.intValue()
      && level.intValue() < java.util.logging.Level.SEVERE.intValue()) {
        return LogLevel.INFO;
      }
    else if (level.intValue() >= java.util.logging.Level.SEVERE.intValue()
      && level.intValue() < java.util.logging.Level.OFF.intValue()) {
        return LogLevel.ERROR;
      }
    return null;
  }

  private static String resolveMessage(final LogRecord record) {
    String message = record.getMessage();
    if (message == null) {
      return "";
    }
    final ResourceBundle bundle = record.getResourceBundle();
    if (bundle != null) {
      try {
        message = bundle.getString(message);
      }
      catch (final MissingResourceException e) {
      }
    }
    final Object[] params = record.getParameters();
    if (params != null && params.length > 0) {
      try {
        message = MessageFormat.format(message, params);
      }
      catch (final IllegalArgumentException e) {
        return message;
      }
    }
    return message;
  }

  @Override
  public void close() throws SecurityException {
  }

  @Override
  public void flush() {
  }

  @Override
  public void publish(final java.util.logging.LogRecord record) {
    final LogLevel level = resolveLevel(record.getLevel());
    if (level == null) {
      return;
    }
    String loggerName = record.getLoggerName();
    if (loggerName == null) {
      loggerName = "jul";
    }
    final Logger logger = Loggers.find(loggerName);
    final String message = resolveMessage(record);
    final Throwable throwable = record.getThrown();
    final Object[] parameters = record.getParameters();
    if (throwable == null && parameters == null) {
      logger.log(STACK_DEPTH, level, message);
    }
    else if (throwable != null && parameters == null) {
      logger.log(STACK_DEPTH, level, throwable, message);
    }
    else if (throwable == null && parameters != null) {
      logger.log(STACK_DEPTH, level, message, parameters);
    }
    else if (throwable != null && parameters != null) {
      logger.log(STACK_DEPTH, level, throwable, message, parameters);
    }
  }

}
