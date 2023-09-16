package tech.onega.jvm.std.log.appenders.file;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.testng.annotations.Test;
import tech.onega.jvm.std.lang.RandUtils;
import tech.onega.jvm.std.log.LogLevel;
import tech.onega.jvm.std.log.LogMessage;
import tech.onega.jvm.std.struct.date.DateTime;
import tech.onega.jvm.std.struct.hash.KV;
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.struct.map.IMap;

public class FileAppenderTest {

  @Test
  public void test() throws Exception {
    final var workersCount = 1;
    final var delayMinMillis = 0L;
    final var delayMaxMillis = 0L;
    final var workerMessagesCount = 1000;
    final var propeties = IMap.<String, String>of(
      KV.of(FileAppender.Options.FILE_NAME.title, "target/log/test.log"),
      KV.of(FileAppender.Options.MAX_BACKUPS.title, "3"),
      KV.of(FileAppender.Options.MAX_FILE_SIZE_KB.title, "128"),
      KV.of(FileAppender.Options.GZIP_LEVEL.title, "9"));
    final var executor = Executors.newFixedThreadPool(workersCount);
    final var location = StackWalker.getInstance().walk(s -> s.limit(1).findAny().get());
    final var latch = new CountDownLatch(workersCount);
    try (var appender = FileAppender.create("testAppender", propeties)) {
      for (var w = 0; w < workersCount; w++) {
        executor.execute(() -> {
          try {
            for (var m = 0; m < workerMessagesCount; m++) {
              final var message = new LogMessage(
                DateTime.now(),
                LogLevel.INFO,
                new Exception(),
                String.format("Message from: %s, number: %s", Thread.currentThread().getId(), m),
                IList.empty(),
                location);
              appender.append(message);
              TimeUnit.MILLISECONDS.sleep(RandUtils.randLong(delayMinMillis, delayMaxMillis));
            }
          }
          catch (final Exception e) {
            e.printStackTrace();
          }
          latch.countDown();
        });
      }
      latch.await();
      executor.shutdown();
    }
  }

}
