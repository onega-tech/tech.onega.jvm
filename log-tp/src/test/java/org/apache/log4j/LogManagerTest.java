package org.apache.log4j;

import org.testng.annotations.Test;

@SuppressWarnings("deprecation")
public class LogManagerTest {

  private final static org.apache.log4j.Logger LOGGER = org.apache.log4j.LogManager.getLogger(LogManagerTest.class);

  @Test
  public void testDebug() {
    LOGGER.debug("org.apache.log4j logging - DEBUG");
  }

  @Test
  public void testError() {
    LOGGER.error("org.apache.log4j logging - ERROR");
  }

  @Test
  public void testFatal() {
    LOGGER.fatal("org.apache.log4j logging - FATAL");
  }

  @Test
  public void testInfo() {
    LOGGER.info("org.apache.log4j logging - INFO");
  }

  @Test
  public void testTrace() {
    LOGGER.trace("org.apache.log4j logging - TRACE");
  }

  @Test
  public void testWarn() {
    LOGGER.warn("org.apache.log4j logging - WARN");
  }

}
