package org.apache.log4j;

import java.util.Enumeration;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RepositorySelector;

@Deprecated
final public class LogManager {

  static final public String CONFIGURATOR_CLASS_KEY = "log4j.configuratorClass";

  static public final String DEFAULT_CONFIGURATION_FILE = "log4j.properties";

  static final public String DEFAULT_CONFIGURATION_KEY = "log4j.configuration";

  public static final String DEFAULT_INIT_OVERRIDE_KEY = "log4j.defaultInitOverride";

  public static Logger exists(final String name) {
    return getLogger(name);
  }

  @SuppressWarnings("rawtypes")
  public static Enumeration getCurrentLoggers() {
    return new Enumeration() {

      @Override
      public boolean hasMoreElements() {
        return false;
      }

      @Override
      public Object nextElement() {
        return null;
      }

    };
  }

  @SuppressWarnings("rawtypes")
  public static Logger getLogger(final Class clazz) {
    return new Logger(tech.onega.jvm.std.log.Loggers.find(clazz));
  }

  public static Logger getLogger(final String name) {
    return new Logger(tech.onega.jvm.std.log.Loggers.find(name));
  }

  public static Logger getLogger(final String name, final LoggerFactory factory) {
    return getLogger(name);
  }

  public static LoggerRepository getLoggerRepository() {
    return null;
  }

  public static Logger getRootLogger() {
    return null;
  }

  public static void resetConfiguration() {
  }

  public static void setRepositorySelector(final RepositorySelector selector, final Object guard) {
  }

}
