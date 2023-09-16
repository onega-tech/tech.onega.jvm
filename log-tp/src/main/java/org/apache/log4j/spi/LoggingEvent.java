package org.apache.log4j.spi;

import java.util.Map;
import java.util.Set;
import org.apache.log4j.Category;
import org.apache.log4j.Level;

@Deprecated
final public class LoggingEvent implements java.io.Serializable {

  private static final long serialVersionUID = 1L;

  public String getFQNOfLoggerClass() {
    return null;
  }

  public Level getLevel() {
    return null;
  }

  public LocationInfo getLocationInformation() {
    return null;
  }

  public Category getLogger() {
    return null;
  }

  public String getLoggerName() {
    return null;
  }

  public Object getMDC(final String key) {
    return null;
  }

  public void getMDCCopy() {
  }

  public Object getMessage() {
    return null;
  }

  public String getNDC() {
    return null;
  }

  @SuppressWarnings("rawtypes")
  public Map getProperties() {
    return null;
  }

  public String getProperty(final String key) {
    return null;
  }

  @SuppressWarnings("rawtypes")
  public Set getPropertyKeySet() {
    return null;
  }

  public String getRenderedMessage() {
    return null;
  }

  public String getThreadName() {
    return null;
  }

  public ThrowableInformation getThrowableInformation() {
    return null;
  }

  public String[] getThrowableStrRep() {
    return null;
  }

  public long getTimeStamp() {
    return -1;
  }

  public boolean locationInformationExists() {
    return false;
  }

  public Object removeProperty(final String propName) {
    return null;
  }

  public void setProperty(final String propName, final String propValue) {
  }

}
