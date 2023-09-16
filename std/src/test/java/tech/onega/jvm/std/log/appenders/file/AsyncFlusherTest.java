package tech.onega.jvm.std.log.appenders.file;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.testng.annotations.Test;
import tech.onega.jvm.std.validate.Check;

public class AsyncFlusherTest {

  private static class Flusher extends AsyncFlusher<Void> {

    private int result;

    private final AtomicInteger resultRef;

    public Flusher(final long flushDelayMillis, final int bufferSize, final AtomicInteger resultRef) {
      super("name", flushDelayMillis, bufferSize);
      this.resultRef = resultRef;
    }

    @Override
    protected void onClose() throws InterruptedException {
      this.resultRef.set(this.result);
    }

    @Override
    protected void onFlush(final Iterable<Void> values, final int count) throws InterruptedException {
      this.result += count;
    }

  }

  @Test
  public void test() throws Exception {
    final var resultRef = new AtomicInteger(0);
    final var flusher = new Flusher(1, 4000, resultRef);
    flusher.start();
    final var threadCount = 32;
    final var addPerThread = 10_000;
    final var resultExpected = threadCount * addPerThread;
    final var executor = Executors.newFixedThreadPool(threadCount);
    final var latch = new CountDownLatch(threadCount);
    for (var i = 0; i < threadCount; i++) {
      executor.execute(() -> {
        for (var k = 0; k < addPerThread; k++) {
          flusher.add(null, 2);
        }
        latch.countDown();
      });
    }
    latch.await();
    flusher.close();
    executor.shutdown();
    Check.equals(resultRef.get(), resultExpected);
  }

}
