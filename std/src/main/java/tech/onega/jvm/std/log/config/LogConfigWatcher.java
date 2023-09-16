package tech.onega.jvm.std.log.config;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import tech.onega.jvm.std.lang.Exec;
import tech.onega.jvm.std.log.Loggers;
import tech.onega.jvm.std.struct.bytes.IBytes;
import tech.onega.jvm.std.struct.date.DateTime;

final public class LogConfigWatcher implements AutoCloseable {

  private final AtomicReference<Thread> workerRef = new AtomicReference<>(null);

  private volatile boolean running = false;

  private final int bufferSize = 16 * 1024;

  private final DateTimeFormatter LOG_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  @Override
  public void close() {
    stop();
  }

  private IBytes loadConfigContent(final URI uri) throws RuntimeException {
    try (InputStream is = new BufferedInputStream(uri.toURL().openConnection().getInputStream(), bufferSize)) {
      return IBytes.read(is);
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void log(final String message) {
    System.out.println(String.format(
      "%s - StdLoggerFactoryMonitor - %s",
      DateTime.now().toLocalDateTime().format(LOG_DATE_FORMAT),
      message));
  }

  private void run(final URI uri, final CountDownLatch latch, final Duration monitorInterval) {
    var config = loadConfigContent(uri);
    updateConfig(uri, config);
    latch.countDown();
    while (running) {
      try {
        TimeUnit.MILLISECONDS.sleep(monitorInterval.toMillis());
        final var loadedConfig = loadConfigContent(uri);
        if (!config.equals(loadedConfig)) {
          config = loadedConfig;
          updateConfig(uri, config);
        }
      }
      catch (final Exception e) {
        if (e instanceof InterruptedException) {
          break;
        }
        else {
          e.printStackTrace();
        }
      }
    }
  }

  public void start(final File file, final Duration monitorInterval) {
    start(file.toURI(), monitorInterval);
  }

  public void start(final String uri, final Duration monitorInterval) {
    if (uri.contains(":/")) {
      start(URI.create(uri), monitorInterval);
    }
    else {
      start(new File(uri), monitorInterval);
    }
  }

  public void start(final URI uri, final Duration monitorInterval) {
    stop();
    final var latch = new CountDownLatch(1);
    final var worker = new Thread(() -> run(uri, latch, monitorInterval), "jxx-log-config-monitor");
    worker.setDaemon(true);
    running = true;
    worker.start();
    workerRef.set(worker);
    Exec.quietly(latch::await);
  }

  public void stop() {
    final var thread = workerRef.get();
    if (thread != null) {
      running = false;
      thread.interrupt();
      Exec.quietly(thread::join);
    }
  }

  private void updateConfig(final URI uri, final IBytes content) {
    log("Try update config from uri: " + uri);
    Loggers.getFactory().configureYaml(content.toStringUTF8());
    log("Successful update config from uri: " + uri);
  }

}
