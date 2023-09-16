package tech.onega.jvm.std.log.appenders.file;

import java.io.File;
import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import tech.onega.jvm.std.io.IOUtils;
import tech.onega.jvm.std.struct.bytes.IBytes;
import tech.onega.jvm.std.struct.set.ISet;

class FileBytesAppender implements AutoCloseable {

  private static final Set<? extends OpenOption> FILE_APPEND_OPTIONS = ISet.of(
    StandardOpenOption.CREATE,
    StandardOpenOption.APPEND)
    .toSet();

  private final File file;

  private SeekableByteChannel fileChannel = null;

  public FileBytesAppender(final File file) {
    this.file = file;
  }

  public void append(final IBytes data) throws IOException {
    if (this.fileChannel == null) {
      final Path filePath = this.file.toPath();
      this.fileChannel = filePath.getFileSystem().provider().newByteChannel(filePath, FILE_APPEND_OPTIONS);
    }
    this.fileChannel.write(data.toByteBuffer());
  }

  @Override
  public void close() {
    IOUtils.closeResources(this.fileChannel);
  }

}