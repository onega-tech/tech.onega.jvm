package org.slf4j;

@Deprecated
public interface Logger {

  final public String ROOT_LOGGER_NAME = "ROOT";

  public void debug(Marker marker, String msg);

  public void debug(Marker marker, String format, Object arg);

  public void debug(Marker marker, String format, Object... arguments);

  public void debug(Marker marker, String format, Object arg1, Object arg2);

  public void debug(Marker marker, String msg, Throwable t);

  public void debug(String msg);

  public void debug(String format, Object arg);

  public void debug(String format, Object... arguments);

  public void debug(String format, Object arg1, Object arg2);

  public void debug(String msg, Throwable t);

  public void error(Marker marker, String msg);

  public void error(Marker marker, String format, Object arg);

  public void error(Marker marker, String format, Object... arguments);

  public void error(Marker marker, String format, Object arg1, Object arg2);

  public void error(Marker marker, String msg, Throwable t);

  public void error(String msg);

  public void error(String format, Object arg);

  public void error(String format, Object... arguments);

  public void error(String format, Object arg1, Object arg2);

  public void error(String msg, Throwable t);

  public String getName();

  public void info(Marker marker, String msg);

  public void info(Marker marker, String format, Object arg);

  public void info(Marker marker, String format, Object... arguments);

  public void info(Marker marker, String format, Object arg1, Object arg2);

  public void info(Marker marker, String msg, Throwable t);

  public void info(String msg);

  public void info(String format, Object arg);

  public void info(String format, Object... arguments);

  public void info(String format, Object arg1, Object arg2);

  public void info(String msg, Throwable t);

  public boolean isDebugEnabled();

  public boolean isDebugEnabled(Marker marker);

  public boolean isErrorEnabled();

  public boolean isErrorEnabled(Marker marker);

  public boolean isInfoEnabled();

  public boolean isInfoEnabled(Marker marker);

  public boolean isTraceEnabled();

  public boolean isTraceEnabled(Marker marker);

  public boolean isWarnEnabled();

  public boolean isWarnEnabled(Marker marker);

  public void trace(Marker marker, String msg);

  public void trace(Marker marker, String format, Object arg);

  public void trace(Marker marker, String format, Object... argArray);

  public void trace(Marker marker, String format, Object arg1, Object arg2);

  public void trace(Marker marker, String msg, Throwable t);

  public void trace(String msg);

  public void trace(String format, Object arg);

  public void trace(String format, Object... arguments);

  public void trace(String format, Object arg1, Object arg2);

  public void trace(String msg, Throwable t);

  public void warn(Marker marker, String msg);

  public void warn(Marker marker, String format, Object arg);

  public void warn(Marker marker, String format, Object... arguments);

  public void warn(Marker marker, String format, Object arg1, Object arg2);

  public void warn(Marker marker, String msg, Throwable t);

  public void warn(String msg);

  public void warn(String format, Object arg);

  public void warn(String format, Object... arguments);

  public void warn(String format, Object arg1, Object arg2);

  public void warn(String msg, Throwable t);

}