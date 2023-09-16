package tech.onega.jvm.std.codec.zip;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import tech.onega.jvm.std.annotation.Nullable;
import tech.onega.jvm.std.lang.Lambda;

final public class ZipCodec {

  public static void compressDir(
    final File dir,
    final File zipFile,
    final int zipLevel,
    @Nullable final Lambda.Consumer<ZipEntry, Throwable> zipEntryConsumer) throws Exception {
    final var sourceDir = dir.toPath();
    try (final ZipOutputStream outputStream = new ZipOutputStream(
      new BufferedOutputStream(new FileOutputStream(zipFile)))) {
      outputStream.setMethod(ZipOutputStream.DEFLATED);
      outputStream.setLevel(zipLevel);
      Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {

        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attributes) throws IOException {
          final var targetFile = sourceDir.relativize(file);
          final var zipEntry = new ZipEntry(targetFile.toString());
          final var bytes = Files.readAllBytes(file);
          if (zipEntryConsumer != null) {
            try {
              zipEntryConsumer.invoke(zipEntry);
            }
            catch (final Throwable e) {
              throw new IOException(e);
            }
          }
          outputStream.putNextEntry(zipEntry);
          outputStream.write(bytes, 0, bytes.length);
          outputStream.closeEntry();
          return FileVisitResult.CONTINUE;
        }

      });
    }
  }

}
