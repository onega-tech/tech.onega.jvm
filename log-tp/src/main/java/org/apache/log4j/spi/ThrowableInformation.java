package org.apache.log4j.spi;

@Deprecated
final public class ThrowableInformation implements java.io.Serializable {

  private static final long serialVersionUID = 1L;

  public Throwable getThrowable() {
    return null;
  }

  public synchronized String[] getThrowableStrRep() {
    return null;
  }

}
