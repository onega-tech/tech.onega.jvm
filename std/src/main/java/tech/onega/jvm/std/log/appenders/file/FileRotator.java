package tech.onega.jvm.std.log.appenders.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.Set;
import java.util.regex.Pattern;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import tech.onega.jvm.std.codec.gzip.GzipCodecOutputStream;
import tech.onega.jvm.std.struct.date.DateTime;
import tech.onega.jvm.std.struct.set.ISet;
import tech.onega.jvm.std.struct.stream.StreamUtils;

class FileRotator {

  private static DateTimeFormatter createBackupFormatter(final File file, final boolean gzipEnable) {
    final String fileName = file.getName();
    final int firstDot = fileName.indexOf('.');
    final String prefix = firstDot > 0 ? fileName.substring(0, firstDot).trim() : "";
    String postfix = (firstDot > 0 && firstDot < fileName.length()) ? fileName.substring(firstDot).trim() : "";
    postfix = gzipEnable ? postfix + ".gz" : postfix;
    return new DateTimeFormatterBuilder()
      .appendLiteral(prefix)
      .appendLiteral('_')
      .appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
      .appendLiteral('-')
      .appendValue(ChronoField.MONTH_OF_YEAR, 2)
      .appendLiteral('-')
      .appendValue(ChronoField.DAY_OF_MONTH, 2)
      .appendLiteral('_')
      .appendValue(ChronoField.HOUR_OF_DAY, 2)
      .appendLiteral('-')
      .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
      .appendLiteral('-')
      .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
      .appendLiteral(postfix)
      .toFormatter();
  }

  private static Pattern createFilePattern(final File file) {
    final String fileName = file.getName();
    final int firstDot = fileName.indexOf('.');
    final String prefix = firstDot > 0 ? fileName.substring(0, firstDot).trim() : "";
    final String postfix = (firstDot > 0 && firstDot < fileName.length()) ? fileName.substring(firstDot).trim() : "";
    final String patternString = String.format(
      "%s\\_[0-9]{4}\\-[0-9]{2}\\-[0-9]{2}_[0-9]{2}\\-[0-9]{2}\\-[0-9]{2}%s(\\.gz)?",
      Pattern.quote(prefix),
      Pattern.quote(postfix));
    return Pattern.compile(patternString);
  }

  private final Set<? extends OpenOption> FILE_READ_OPTIONS = ISet.of(
    StandardOpenOption.READ,
    StandardOpenOption.WRITE)
    .toSet();

  private final File file;

  private final int maxFileSizeBytes;

  private final int maxBackups;

  private final int gzipLevel;

  private final Pattern filePattern;

  @JsonSerialize(using = ToStringSerializer.class)
  private final DateTimeFormatter backupFormatter;

  private final boolean gzipEnabled;

  private final boolean backupsEnabled;

  private final int copyTruncateBufferSizeBytes;

  public FileRotator(
    final File file,
    final int maxFileSizeBytes,
    final int maxBackups,
    final int gzipLevel,
    final int copyTruncateBufferSizeBytes) {
    this.file = file;
    this.maxFileSizeBytes = maxFileSizeBytes;
    this.maxBackups = maxBackups;
    this.gzipLevel = gzipLevel;
    this.gzipEnabled = gzipLevel > 0;
    this.backupsEnabled = maxBackups > 0;
    this.copyTruncateBufferSizeBytes = copyTruncateBufferSizeBytes;
    this.backupFormatter = this.backupsEnabled ? createBackupFormatter(file, this.gzipEnabled) : null;
    this.filePattern = this.backupsEnabled ? createFilePattern(file) : null;
  }

  private void copyTruncate() throws Exception {
    final File destFile = new File(this.file.getParentFile(), this.backupFormatter.format(DateTime.now().toLocalDateTime()));
    if (destFile.exists()) {
      destFile.delete();
    }
    final Path filePath = this.file.toPath();
    try (
      SeekableByteChannel source = filePath.getFileSystem().provider().newByteChannel(filePath, this.FILE_READ_OPTIONS)) {
      try (OutputStream dest = this.copyTruncateOutputStream(destFile)) {
        long nextPosition = 0;
        final ByteBuffer buffer = ByteBuffer.allocate(this.copyTruncateBufferSizeBytes);
        while (nextPosition < source.size()) {
          source.position(nextPosition);
          buffer.position(0);
          source.read(buffer);
          nextPosition += buffer.position();
          dest.write(buffer.array(), 0, buffer.position());
        }
      }
      source.truncate(0);
    }
  }

  private OutputStream copyTruncateOutputStream(final File destFile) throws Exception {
    return this.gzipEnabled
      ? new GzipCodecOutputStream(new FileOutputStream(destFile, false), this.copyTruncateBufferSizeBytes, false, this.gzipLevel)
      : new BufferedOutputStream(new FileOutputStream(destFile, false), this.copyTruncateBufferSizeBytes);
  }

  public void gc() {
    if (!this.backupsEnabled) {
      return;
    }
    final File[] logFiles = this.file.getParentFile().listFiles();
    StreamUtils
      .createStream(logFiles)
      .filter(f -> this.filePattern.matcher(f.getName()).matches())
      .sorted((a, b) -> b.getName().compareToIgnoreCase(a.getName()))
      .skip(this.maxBackups)
      .forEach(File::delete);
  }

  public void rotate() throws Exception {
    if (!this.backupsEnabled || this.file.length() < this.maxFileSizeBytes) {
      return;
    }
    this.copyTruncate();
    this.gc();
  }

}