package tech.onega.jvm.std.log.appenders.file;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import org.testng.annotations.Test;
import tech.onega.jvm.std.validate.Check;

public class AtomicExchangerTest {

  private static class Incrementer {

    private int v1;

    private int v2;

    public int get() {
      return (this.v1 + this.v2) / 2;
    }

    public void increment() {
      this.v1++;
      this.v2++;
    }

  }

  @Test
  public void testExchange() throws Exception {
    final var threadCount = 32;
    final var incrementPerThread = 10_000;
    final var result = threadCount * incrementPerThread;
    final var executor = Executors.newFixedThreadPool(threadCount);
    final var latch = new CountDownLatch(threadCount);
    final var exchanger = new AtomicExchanger<Incrementer>();
    exchanger.exchange(new Incrementer());
    for (var i = 0; i < threadCount; i++) {
      executor.execute(() -> {
        for (var k = 0; k < incrementPerThread; k++) {
          final var inc = exchanger.exchange(null);
          inc.increment();
          exchanger.exchange(inc);
        }
        latch.countDown();
      });
    }
    latch.await();
    executor.shutdown();
    Check.equals(exchanger.exchange(null).get(), result);
  }

}
