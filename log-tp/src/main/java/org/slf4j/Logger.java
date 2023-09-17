package org.slf4j;

public interface Logger {

  String ROOT_LOGGER_NAME = "ROOT";

  void debug(Marker marker, String msg);

  void debug(Marker marker, String format, Object arg);

  void debug(Marker marker, String format, Object... arguments);

  void debug(Marker marker, String format, Object arg1, Object arg2);

  void debug(Marker marker, String msg, Throwable t);

  void debug(String msg);

  void debug(String format, Object arg);

  void debug(String format, Object... arguments);

  void debug(String format, Object arg1, Object arg2);

  void debug(String msg, Throwable t);

  void error(Marker marker, String msg);

  void error(Marker marker, String format, Object arg);

  void error(Marker marker, String format, Object... arguments);

  void error(Marker marker, String format, Object arg1, Object arg2);

  void error(Marker marker, String msg, Throwable t);

  void error(String msg);

  void error(String format, Object arg);

  void error(String format, Object... arguments);

  void error(String format, Object arg1, Object arg2);

  void error(String msg, Throwable t);

  String getName();

  void info(Marker marker, String msg);

  void info(Marker marker, String format, Object arg);

  void info(Marker marker, String format, Object... arguments);

  void info(Marker marker, String format, Object arg1, Object arg2);

  void info(Marker marker, String msg, Throwable t);

  void info(String msg);

  void info(String format, Object arg);

  void info(String format, Object... arguments);

  void info(String format, Object arg1, Object arg2);

  void info(String msg, Throwable t);

  boolean isDebugEnabled();

  boolean isDebugEnabled(Marker marker);

  boolean isErrorEnabled();

  boolean isErrorEnabled(Marker marker);

  boolean isInfoEnabled();

  boolean isInfoEnabled(Marker marker);

  boolean isTraceEnabled();

  boolean isTraceEnabled(Marker marker);

  boolean isWarnEnabled();

  boolean isWarnEnabled(Marker marker);

  void trace(Marker marker, String msg);

  void trace(Marker marker, String format, Object arg);

  void trace(Marker marker, String format, Object... argArray);

  void trace(Marker marker, String format, Object arg1, Object arg2);

  void trace(Marker marker, String msg, Throwable t);

  void trace(String msg);

  void trace(String format, Object arg);

  void trace(String format, Object... arguments);

  void trace(String format, Object arg1, Object arg2);

  void trace(String msg, Throwable t);

  void warn(Marker marker, String msg);

  void warn(Marker marker, String format, Object arg);

  void warn(Marker marker, String format, Object... arguments);

  void warn(Marker marker, String format, Object arg1, Object arg2);

  void warn(Marker marker, String msg, Throwable t);

  void warn(String msg);

  void warn(String format, Object arg);

  void warn(String format, Object... arguments);

  void warn(String format, Object arg1, Object arg2);

  void warn(String msg, Throwable t);

}