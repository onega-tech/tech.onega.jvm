package tech.onega.jvm.std.log.config;

import java.io.File;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.testng.annotations.Test;
import tech.onega.jvm.std.log.Logger;
import tech.onega.jvm.std.log.Loggers;

public class LogConfigMonitorTest {

  private final static Logger LOGGER = Loggers.find(LogConfigMonitorTest.class);

  @Test(enabled = false)
  public void test() throws Exception {
    try (LogConfigWatcher monitor = new LogConfigWatcher()) {
      monitor.start(new File("src/test/resources/log.properties"), Duration.ofSeconds(1));
      for (int i = 0; i < 180; i++) {
        LOGGER.debug("Hello world");
        TimeUnit.SECONDS.sleep(1);
      }
    }
  }

}
