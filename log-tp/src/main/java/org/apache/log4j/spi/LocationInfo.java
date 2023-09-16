package org.apache.log4j.spi;

@Deprecated
final public class LocationInfo implements java.io.Serializable {

  private static final long serialVersionUID = 1L;

  public final static String NA = "?";

  public static final LocationInfo NA_LOCATION_INFO = null;

  public String getClassName() {
    return null;
  }

  public String getFileName() {
    return null;
  }

  public String getLineNumber() {
    return null;
  }

  public String getMethodName() {
    return null;
  }

}
