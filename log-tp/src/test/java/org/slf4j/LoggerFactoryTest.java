package org.slf4j;

import org.testng.annotations.Test;

public class LoggerFactoryTest {

  private final static org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(LoggerFactoryTest.class);

  @Test
  public void testDebug() {
    LOGGER.debug("org.slf4j logging - DEBUG - {}", System.currentTimeMillis());
  }

  @Test
  public void testError() {
    LOGGER.error("org.slf4j logging - ERROR - {}", System.currentTimeMillis());
  }

  @Test
  public void testInfo() {
    LOGGER.info("org.slf4j logging - INFO - {}", System.currentTimeMillis());
  }

  @Test
  public void testTrace() {
    LOGGER.trace("org.slf4j logging - TRACE - {}", System.currentTimeMillis());
  }

  @Test
  public void testWarn() {
    LOGGER.warn("org.slf4j logging - WARN - {}", System.currentTimeMillis());
  }

}
