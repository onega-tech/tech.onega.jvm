package org.apache.log4j.spi;

import java.util.Enumeration;
import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

@Deprecated
public interface LoggerRepository {

  public void addHierarchyEventListener(HierarchyEventListener listener);

  public void emitNoAppenderWarning(Category cat);

  public abstract Logger exists(String name);

  public abstract void fireAddAppenderEvent(Category logger, Appender appender);

  @SuppressWarnings("rawtypes")
  public Enumeration getCurrentCategories();

  @SuppressWarnings("rawtypes")
  public Enumeration getCurrentLoggers();

  public Logger getLogger(String name);

  public Logger getLogger(String name, LoggerFactory factory);

  public Logger getRootLogger();

  public Level getThreshold();

  boolean isDisabled(int level);

  public abstract void resetConfiguration();

  public void setThreshold(Level level);

  public void setThreshold(String val);

  public abstract void shutdown();

}
