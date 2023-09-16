package tech.onega.jvm.std.io.proc;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;
import tech.onega.jvm.std.struct.map.MMap;

//processBuilder.environment().put("MAGICK_THREAD_LIMIT", "1");
//processBuilder.environment().put("OMP_NUM_THREADS", "1");
final public class ProcBuilder {

  String commandLine;

  final MMap<String, String> envs = MMap.create();

  File dir = new File(".");

  OutputStream errRedirect;

  OutputStream outRedirect;

  InputStream inStream;

  boolean errResult = true;

  boolean outResult = true;

  Consumer<String> logger;

  ProcBuilder() {
  }

  public ProcBuilder commandLine(final String commandLine, final Object... args) {
    this.commandLine = ((args == null) || (args.length == 0)) ? commandLine : String.format(commandLine, args);
    return this;
  }

  public ProcBuilder dir(final File dir) {
    this.dir = dir;
    return this;
  }

  public ProcBuilder env(final String key, final String value) {
    this.envs.add(key, value);
    return this;
  }

  public ProcBuilder errRedirect(final OutputStream errRedirect) {
    this.errRedirect = errRedirect;
    return this;
  }

  public ProcBuilder errResult(final boolean errResult) {
    this.errResult = errResult;
    return this;
  }

  public ProcBuilder inStream(final InputStream inStream) {
    this.inStream = inStream;
    return this;
  }

  public ProcBuilder logger(final Consumer<String> logger) {
    this.logger = logger;
    return this;
  }

  public ProcBuilder outRedirect(final OutputStream outRedirect) {
    this.outRedirect = outRedirect;
    return this;
  }

  public ProcBuilder outResult(final boolean outResult) {
    this.outResult = outResult;
    return this;
  }

  public Proc start() {
    return new Proc(this);
  }

}
