package tech.onega.jvm.std.io.proc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ProcessBuilder.Redirect;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import tech.onega.jvm.std.io.IOUtils;
import tech.onega.jvm.std.lang.Exec;
import tech.onega.jvm.std.struct.date.DateTime;
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.struct.map.IMap;

final public class Proc implements AutoCloseable {

  public static ProcBuilder newBuilder() {
    return new ProcBuilder();
  }

  private final Process process;

  private final ProcessHandle processHandle;

  private final File workDir;

  private final IMap<String, String> envs;

  private final CompletableFuture<ProcResult> resultFuture = new CompletableFuture<>();

  private final ProcPipe errPipe;

  private final ProcPipe outPipe;

  public Proc(final ProcBuilder builder) {
    try {
      final var resultErr = builder.errResult ? new ByteArrayOutputStream() : null;
      final var resultOut = builder.outResult ? new ByteArrayOutputStream() : null;
      this.envs = builder.envs.toIMap();
      this.workDir = builder.dir;
      this.process = Exec.lambda(() -> {
        final var b = new ProcessBuilder();
        b.command(builder.commandLine.split("[ ]+"));
        b.environment().putAll(builder.envs.toMap());
        b.redirectError(Redirect.PIPE);
        b.redirectInput(Redirect.PIPE);
        b.redirectOutput(Redirect.PIPE);
        b.directory(builder.dir);
        return b.start();
      });
      this.processHandle = this.process.toHandle();
      if (builder.inStream != null) {
        IOUtils.copyStreams(builder.inStream, this.process.getOutputStream());
      }
      if (builder.logger != null) {
        builder.logger.accept(builder.commandLine);
      }
      this.errPipe = new ProcPipe(this.process.getErrorStream(), IList.of(resultErr, builder.errRedirect));
      this.outPipe = new ProcPipe(this.process.getInputStream(), IList.of(resultOut, builder.outRedirect));
      this.processHandle.onExit().handle((r, e) -> {
        this.closePipes();
        if (e != null) {
          this.resultFuture.completeExceptionally(e);
        }
        else {
          this.resultFuture.complete(new ProcResult(
            this.process.exitValue(),
            resultOut,
            resultErr));
        }
        return null;
      });
    }
    catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void close() {
    Exec.quietly(this.process::destroy);
    this.closePipes();
  }

  private void closePipes() {
    this.errPipe.close();
    this.outPipe.close();
  }

  public String commandLine() {
    return this.processHandle.info().commandLine().orElse(null);
  }

  public Duration cpuTime() {
    return this.process.info().totalCpuDuration().orElse(null);
  }

  public IMap<String, String> envs() {
    return this.envs;
  }

  public boolean isAlive() {
    return this.process.isAlive();
  }

  public void kill() {
    Exec.quietly(this.process::destroyForcibly);
    this.closePipes();
  }

  public long pid() {
    return this.process.pid();
  }

  public ProcResult result() {
    try {
      return this.resultFuture.get();
    }
    catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  public CompletableFuture<ProcResult> resultFuture() {
    return this.resultFuture;
  }

  public DateTime startTime() {
    return DateTime.ofTimestamp(this.process.info().startInstant().get());
  }

  public File workDir() {
    return this.workDir;
  }

}
