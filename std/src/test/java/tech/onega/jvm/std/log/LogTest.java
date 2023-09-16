package tech.onega.jvm.std.log;

import org.testng.annotations.Test;

public class LogTest {

  static {
    Loggers.configureFromResource("log.yaml");
  }

  private static final Logger LOGGER = Loggers.find(LogTest.class);

  @Test
  public void testDebug() {
    LOGGER.debug("testDebug {} / {}", 1, 2);
  }

  @Test
  public void testError() {
    try {
      try {
        throw new RuntimeException("Test exception level 1");
      }
      catch (final Exception e) {
        throw new RuntimeException("Test exception level 2", e);
      }
    }
    catch (final Exception e) {
      LOGGER.error(e, "testError {} / {}", 1, 2);
    }
  }

  @Test
  public void testInfo() {
    LOGGER.info("testInfo {} / {}", 1, 2);
  }

  @Test
  public void testLog() {
    LOGGER.log(0, LogLevel.DEBUG, "test log info {} / {}", 1, 2);
  }

}
