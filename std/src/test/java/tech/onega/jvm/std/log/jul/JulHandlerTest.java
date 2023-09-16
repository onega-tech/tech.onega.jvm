package tech.onega.jvm.std.log.jul;

import org.testng.annotations.Test;

public class JulHandlerTest {

  private static final java.util.logging.Logger LOGGER;
  static {
    JulHandler.install();
    LOGGER = java.util.logging.Logger.getLogger(JulHandlerTest.class.getName());
    LOGGER.setLevel(java.util.logging.Level.ALL);
  }

  @Test
  public void testFine() {
    LOGGER.log(java.util.logging.Level.FINE, "java.util.logging.Logger - FINE - {}", System.currentTimeMillis());
  }

  @Test
  public void testFiner() {
    LOGGER.log(java.util.logging.Level.FINER, "java.util.logging.Logger - FINER - {}", System.currentTimeMillis());
  }

  @Test
  public void testFinest() {
    LOGGER.log(java.util.logging.Level.FINEST, "java.util.logging.Logger - FINEST - {}", System.currentTimeMillis());
  }

  @Test
  public void testInfo() {
    LOGGER.log(java.util.logging.Level.INFO, "java.util.logging.Logger - INFO - {}", System.currentTimeMillis());
  }

  @Test
  public void testSevere() {
    LOGGER.log(java.util.logging.Level.SEVERE, "java.util.logging.Logger - SEVERE - {}", System.currentTimeMillis());
  }

  @Test
  public void testWarning() {
    LOGGER.log(java.util.logging.Level.WARNING, "java.util.logging.Logger - WARNING - {}", System.currentTimeMillis());
  }

}
