package tech.onega.jvm.std.log.appenders.file;

import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.log.LogAppender;
import tech.onega.jvm.std.log.LogFormatter;
import tech.onega.jvm.std.log.LogMessage;
import tech.onega.jvm.std.log.formatters.SimpleFormatter;
import tech.onega.jvm.std.struct.map.IMap;

@JsonTypeInfo(visible = true, use = Id.CLASS)
@ThreadSafe
final public class FileAppender implements LogAppender {

  public enum Options {

      FILE_NAME("fileName", null),
      FULL_BLOCK_MILLIS("fullBlockMillis", "0"),
      FLUSH_INTERVAL_MILLIS("flushIntervalMillis", "300"),
      LOG_BUFFER_SIZE("logBufferSize", "1000"),
      MAX_BACKUPS("maxBackups", "0"),
      MAX_FILE_SIZE_KB("maxFileSizeKb", "0"),
      GZIP_LEVEL("gzipLevel", "0"),
      COPY_TRUNCATE_BUFFER_SIZE_KB("copyTruncateBufferSizeKb", "512");

    public final String defaults;

    public final String title;

    Options(final String title, final String defaults) {
      this.title = title;
      this.defaults = defaults;
    }

    public int getInt(final IMap<String, String> propeties) {
      return Integer.parseInt(propeties.get(this.title, this.defaults));
    }

    public long getLong(final IMap<String, String> propeties) {
      return Long.parseLong(propeties.get(this.title, this.defaults));
    }

    public String getString(final IMap<String, String> propeties) {
      return propeties.get(this.title, this.defaults);
    }

    @Override
    public String toString() {
      return this.title;
    }

  }

  public static FileAppender create(final String name, final IMap<String, String> propeties) throws IOException {
    return new FileAppender(name, new SimpleFormatter(), propeties);
  }

  private final FileFlusher flusher;

  private final long fullBlockMillis;

  private FileAppender(final String appenderName, final LogFormatter formatter, final IMap<String, String> propeties)
    throws IOException {
    final String fileName = Options.FILE_NAME.getString(propeties);
    if (fileName == null) {
      throw new IllegalArgumentException(
        String.format("Property [%s] not set for appender: %s", Options.FILE_NAME.title, appenderName));
    }
    final File file = new File(fileName);
    file.getParentFile().mkdirs();
    this.fullBlockMillis = Options.FULL_BLOCK_MILLIS.getLong(propeties);
    this.flusher = new FileFlusher(
      appenderName,
      Options.FLUSH_INTERVAL_MILLIS.getLong(propeties),
      Options.LOG_BUFFER_SIZE.getInt(propeties),
      formatter,
      new FileBytesAppender(file),
      new FileRotator(file,
        Options.MAX_FILE_SIZE_KB.getInt(propeties) * 1024,
        Options.MAX_BACKUPS.getInt(propeties),
        Options.GZIP_LEVEL.getInt(propeties),
        Options.COPY_TRUNCATE_BUFFER_SIZE_KB.getInt(propeties) * 1024));
    this.flusher.start();
  }

  @Override
  public void append(final LogMessage message) {
    this.flusher.add(message, this.fullBlockMillis);
  }

  @Override
  public void close() {
    this.flusher.close();
  }

}
