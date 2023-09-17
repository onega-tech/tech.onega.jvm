package org.slf4j;

final public class LoggerFactory {

  public static ILoggerFactory getILoggerFactory() {
    return name -> getLogger(name);
  }

  public static org.slf4j.Logger getLogger(final Class<?> clazz) {
    return new LoggerImpl(tech.onega.jvm.std.log.Loggers.find(clazz));
  }

  public static org.slf4j.Logger getLogger(final String name) {
    return new LoggerImpl(tech.onega.jvm.std.log.Loggers.find(name));
  }

}
