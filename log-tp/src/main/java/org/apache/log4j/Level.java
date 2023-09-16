package org.apache.log4j;

import java.io.Serializable;

@Deprecated
final public class Level extends Priority implements Serializable {

  public static final int TRACE_INT = 5000;

  final static public Level OFF = new Level(OFF_INT, "OFF", 0);

  final static public Level FATAL = new Level(FATAL_INT, "FATAL", 0);

  final static public Level ERROR = new Level(ERROR_INT, "ERROR", 3);

  final static public Level WARN = new Level(WARN_INT, "WARN", 4);

  final static public Level INFO = new Level(INFO_INT, "INFO", 6);

  final static public Level DEBUG = new Level(DEBUG_INT, "DEBUG", 7);

  public static final Level TRACE = new Level(TRACE_INT, "TRACE", 7);

  final static public Level ALL = new Level(ALL_INT, "ALL", 7);

  static final long serialVersionUID = 3491141966387921974L;

  public static Level toLevel(final int val) {
    return null;
  }

  public static Level toLevel(final int val, final Level defaultLevel) {
    return null;
  }

  public static Level toLevel(final String sArg) {
    return null;
  }

  public static Level toLevel(final String sArg, final Level defaultLevel) {
    return null;
  }

  protected Level(final int level, final String levelStr, final int syslogEquivalent) {
  }

}