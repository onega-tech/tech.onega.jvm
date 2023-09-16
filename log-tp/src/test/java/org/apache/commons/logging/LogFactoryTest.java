package org.apache.commons.logging;

import org.testng.annotations.Test;

@SuppressWarnings("deprecation")
public class LogFactoryTest {

  private final static org.apache.commons.logging.Log LOGGER = org.apache.commons.logging.LogFactory
    .getLog(LogFactoryTest.class);

  @Test
  public void testDebug() {
    LOGGER.debug("org.apache.commons.logging logging - DEBUG");
  }

  @Test
  public void testError() {
    LOGGER.error("org.apache.commons.logging logging - ERROR");
  }

  @Test
  public void testFatal() {
    LOGGER.fatal("org.apache.commons.logging logging - FATAL");
  }

  @Test
  public void testInfo() {
    LOGGER.info("org.apache.commons.logging logging - INFO");
  }

  @Test
  public void testTrace() {
    LOGGER.trace("org.apache.commons.logging logging - TRACE");
  }

  @Test
  public void testWarn() {
    LOGGER.warn("org.apache.commons.logging logging - WARN");
  }

}
