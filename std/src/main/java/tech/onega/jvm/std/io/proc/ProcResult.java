package tech.onega.jvm.std.io.proc;

import java.io.ByteArrayOutputStream;
import javax.validation.constraints.NotNull;
import tech.onega.jvm.std.annotation.Immutable;
import tech.onega.jvm.std.annotation.Nullable;
import tech.onega.jvm.std.struct.bytes.IBytes;

@Immutable
final public class ProcResult {

  public final int exitCode;

  @Nullable
  public final IBytes out;

  @Nullable
  public final IBytes err;

  public ProcResult(final int exitCode, @Nullable final ByteArrayOutputStream out,
    @Nullable final ByteArrayOutputStream err) {
    this.exitCode = exitCode;
    this.out = out == null ? null : IBytes.wrap(out.toByteArray());
    this.err = err == null ? null : IBytes.wrap(err.toByteArray());
  }

  public ProcResult failIfError() {
    if (this.exitCode != 0) {
      throw new IllegalStateException(this.toString());
    }
    return this;
  }

  @NotNull
  public String getErrString() {
    return this.err == null ? "" : this.err.toStringUTF8();
  }

  @NotNull
  public String getOutString() {
    return this.out == null ? "" : this.out.toStringUTF8();
  }

  @Override
  public String toString() {
    return new StringBuilder()
      .append("exitCode: ").append(this.exitCode).append("\n")
      .append("\nout:\n").append(this.getOutString())
      .append("\nerr:\n").append(this.getErrString())
      .toString();
  }

}