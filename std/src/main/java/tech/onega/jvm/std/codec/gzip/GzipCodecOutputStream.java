package tech.onega.jvm.std.codec.gzip;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

final public class GzipCodecOutputStream extends GZIPOutputStream {

  public GzipCodecOutputStream(final OutputStream out, final int bufferSize, final boolean syncFlush,
    final int compressLevel) throws IOException {
    super(out, bufferSize, syncFlush);
    def.setLevel(compressLevel);
  }

}
