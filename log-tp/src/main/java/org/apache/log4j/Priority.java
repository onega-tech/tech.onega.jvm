package org.apache.log4j;

@Deprecated
public class Priority {

  public final static int OFF_INT = Integer.MAX_VALUE;

  public final static int FATAL_INT = 50000;

  public final static int ERROR_INT = 40000;

  public final static int WARN_INT = 30000;

  public final static int INFO_INT = 20000;

  public final static int DEBUG_INT = 10000;

  public final static int ALL_INT = Integer.MIN_VALUE;

  @Deprecated
  final static public Priority FATAL = new Level(FATAL_INT, "FATAL", 0);

  @Deprecated
  final static public Priority ERROR = new Level(ERROR_INT, "ERROR", 3);

  @Deprecated
  final static public Priority WARN = new Level(WARN_INT, "WARN", 4);

  @Deprecated
  final static public Priority INFO = new Level(INFO_INT, "INFO", 6);

  @Deprecated
  final static public Priority DEBUG = new Level(DEBUG_INT, "DEBUG", 7);

  @Deprecated
  public static Priority[] getAllPossiblePriorities() {
    return null;
  }

  @Deprecated
  public static Priority toPriority(final int val) {
    return null;
  }

  @Deprecated
  public static Priority toPriority(final int val, final Priority defaultPriority) {
    return null;
  }

  @Deprecated
  public static Priority toPriority(final String sArg) {
    return null;
  }

  @Deprecated
  public static Priority toPriority(final String sArg, final Priority defaultPriority) {
    return null;
  }

  @Override
  public boolean equals(final Object o) {
    return false;
  }

  public final int getSyslogEquivalent() {
    return -1;
  }

  public boolean isGreaterOrEqual(final Priority r) {
    return false;
  }

  public final int toInt() {
    return -1;
  }

  @Override
  final public String toString() {
    return "";
  }

}
