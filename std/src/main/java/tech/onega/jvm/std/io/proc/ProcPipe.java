package tech.onega.jvm.std.io.proc;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import tech.onega.jvm.std.annotation.Nullable;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.lang.Exec;
import tech.onega.jvm.std.struct.list.IList;

@ThreadSafe
final class ProcPipe implements AutoCloseable {

  private static class Executor {

    private static final ExecutorService SERVICE = Executors.newCachedThreadPool();

  }

  private static int read(final InputStream in, final IList<OutputStream> outs, final byte[] buffer) throws Exception {
    final var len = in.read(buffer);
    if (len >= 0) {
      for (final var out : outs) {
        out.write(buffer, 0, len);
        out.flush();
      }
    }
    return len;
  }

  @Nullable
  private final Future<?> future;

  private volatile boolean stop = false;

  ProcPipe(final InputStream in, final IList<OutputStream> outs) {
    this(in, outs, 32 * 1024);
  }

  ProcPipe(final InputStream in, final IList<OutputStream> outs, final int bufferSize) {
    final var outStreams = outs.stream()
      .filter(o -> o != null)
      .collect(IList.collector());
    this.future = outStreams.isEmpty() ? null : Executor.SERVICE.submit(() -> {
      try {
        final var buffer = new byte[bufferSize];
        while (!this.stop) {
          if (read(in, outStreams, buffer) <= 0) {
            TimeUnit.MILLISECONDS.sleep(1);
          }
        }
        read(in, outStreams, buffer);
      }
      catch (final Exception e) {
        throw new RuntimeException(e);
      }
    });
  }

  @Override
  public void close() {
    if (this.future != null) {
      this.stop = true;
      try {
        this.future.get(5, TimeUnit.SECONDS);
      }
      catch (final Exception e) {
        Exec.quietly(() -> this.future.cancel(true));
      }
    }
  }

}