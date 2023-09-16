package tech.onega.jvm.std.log.service;

import java.time.Duration;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.log.Loggers;
import tech.onega.jvm.std.log.config.LogConfigWatcher;

@ThreadSafe
public class LogService implements AutoCloseable {

  public static class Config {

    public Duration monitorInterval = Duration.ofSeconds(1);

    public String uri;

  }

  private final LogConfigWatcher watcher = new LogConfigWatcher();

  private final Config config;

  public LogService(final Config config) {
    this.config = config;
  }

  @Override
  public void close() {
    watcher.close();
  }

  public void start() {
    Loggers.configure(config.uri);
  }

  public void startWatching() {
    start();
    watcher.start(config.uri, config.monitorInterval);
  }

  public void stopWatching() {
    watcher.stop();
  }

}
