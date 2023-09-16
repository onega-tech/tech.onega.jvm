package tech.onega.jvm.std.io.proc;

import java.io.ByteArrayOutputStream;
import org.testng.annotations.Test;
import tech.onega.jvm.std.validate.Check;

public class ProcTest {

  @Test
  public void testProc() {
    for (var i = 0; i < 32; i++) {
      final var outStream = new ByteArrayOutputStream();
      final var errStream = new ByteArrayOutputStream();
      final var result = Proc.newBuilder()
        .commandLine("pwd")
        .outRedirect(outStream)
        .errRedirect(errStream)
        .start()
        .result();
      Check.equals(result.exitCode, 0);
      Check.isFalse(result.out.toStringUTF8().isBlank());
      Check.isTrue(result.err.toStringUTF8().isBlank());
      Check.equals(outStream.toByteArray(), result.out.toArray());
      Check.equals(errStream.toByteArray(), result.err.toArray());
    }
  }

}
