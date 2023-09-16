package org.apache.log4j.spi;

import java.util.Enumeration;
import org.apache.log4j.Appender;

@Deprecated
public interface AppenderAttachable {

  public void addAppender(Appender newAppender);

  @SuppressWarnings("rawtypes")
  public Enumeration getAllAppenders();

  public Appender getAppender(String name);

  public boolean isAttached(Appender appender);

  void removeAllAppenders();

  void removeAppender(Appender appender);

  void removeAppender(String name);

}
