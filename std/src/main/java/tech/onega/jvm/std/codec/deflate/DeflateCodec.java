package tech.onega.jvm.std.codec.deflate;

import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import tech.onega.jvm.std.io.writer.IOWriterBytes;
import tech.onega.jvm.std.lang.Exec;
import tech.onega.jvm.std.struct.bytes.IBytes;

public class DeflateCodec {

  public static IBytes encode(final IBytes data, final int level) throws RuntimeException {
    final Deflater def = new Deflater(level);
    final IOWriterBytes writer = new IOWriterBytes(data.length());
    try (DeflaterOutputStream dos = new DeflaterOutputStream(writer.asOutputStream(), def, data.length(), false)) {
      data.asReader().writeToStream(dos);
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
    finally {
      Exec.quietly(def::end);
    }
    return writer.toIBytes();
  }

  public static IBytes decode(final IBytes compressedData) throws RuntimeException {
    return decode(compressedData, 4 * 1024 * 1024);
  }

  public static IBytes decode(final IBytes compressedData, final int bufferSizeInBytes)
    throws RuntimeException {
    final Inflater inflater = new Inflater();
    try (InflaterInputStream iis = new InflaterInputStream(compressedData.asReader().asInputStream(), inflater,
      bufferSizeInBytes)) {
      return IBytes.read(iis, compressedData.length(), bufferSizeInBytes);
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
    finally {
      Exec.quietly(inflater::end);
    }
  }

}
