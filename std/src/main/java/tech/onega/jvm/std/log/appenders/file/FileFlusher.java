package tech.onega.jvm.std.log.appenders.file;

import tech.onega.jvm.std.lang.Exec;
import tech.onega.jvm.std.log.LogFormatter;
import tech.onega.jvm.std.log.LogMessage;

class FileFlusher extends AsyncFlusher<LogMessage> {

  private final FileBytesAppender appender;

  private final FileRotator rotator;

  private final LogFormatter formatter;

  public FileFlusher(
    final String appenderName,
    final long flushIntervalMillis,
    final int bufferSize,
    final LogFormatter formatter,
    final FileBytesAppender appender,
    final FileRotator rotator) {
    super(appenderName, flushIntervalMillis, bufferSize);
    this.formatter = formatter;
    this.appender = appender;
    this.rotator = rotator;
  }

  @Override
  protected void onClose() throws InterruptedException {
    Exec.quietly(this.rotator::rotate);
    this.appender.close();
  }

  @Override
  protected void onFlush(final Iterable<LogMessage> messages, final int messageCount) throws InterruptedException {
    try {
      this.appender.append(this.formatter.format(messages, messageCount));
      this.rotator.rotate();
    }
    catch (final Throwable e) {
      if (e instanceof InterruptedException) {
        throw (InterruptedException) e;
      }
      else {
        e.printStackTrace();
      }
    }
  }

  @Override
  protected void onStart() throws InterruptedException {
    this.rotator.gc();
  }

}