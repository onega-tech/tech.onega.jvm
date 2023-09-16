package org.apache.commons.logging;

@Deprecated
final public class LogConfigurationException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public LogConfigurationException() {
    super();
  }

  public LogConfigurationException(final String message) {
    super(message);
  }

  public LogConfigurationException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public LogConfigurationException(final Throwable cause) {
    super(cause);
  }

}
