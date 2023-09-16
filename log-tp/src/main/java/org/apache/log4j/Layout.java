package org.apache.log4j;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.OptionHandler;

@Deprecated
public abstract class Layout implements OptionHandler {

  public final static String LINE_SEP = System.getProperty("line.separator");

  public final static int LINE_SEP_LEN = LINE_SEP.length();

  abstract public String format(LoggingEvent event);

  public String getContentType() {
    return null;
  }

  public String getFooter() {
    return null;
  }

  public String getHeader() {
    return null;
  }

  abstract public boolean ignoresThrowable();

}