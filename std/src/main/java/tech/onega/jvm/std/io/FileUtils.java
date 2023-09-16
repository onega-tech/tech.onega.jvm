package tech.onega.jvm.std.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;
import tech.onega.jvm.std.struct.bytes.IBytes;
import tech.onega.jvm.std.struct.set.ISet;
import tech.onega.jvm.std.struct.set.MSet;
import tech.onega.jvm.std.validate.Check;

final public class FileUtils {

  public static long checksum(final File file, final Checksum checksum) {
    return checksum(file, checksum, IOUtils.DEFAULT_BUFFER_SIZE);
  }

  public static long checksum(final File file, final Checksum checksum, final int bufferSize) {
    if (file.isDirectory()) {
      throw new IllegalArgumentException("Checksums can't be computed on directories");
    }
    try (var in = new CheckedInputStream(new FileInputStream(file), checksum)) {
      IOUtils.copyStreams(in, OutputStream.nullOutputStream(), bufferSize);
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
    return checksum.getValue();
  }

  public static long checksumCRC32(final File file) {
    return checksum(file, new CRC32());
  }

  public static void clearDir(final File file) throws RuntimeException {
    clearDir(file.getPath());
  }

  public static void clearDir(final String dir) throws RuntimeException {
    try {
      final var path = Paths.get(dir);
      if (!java.nio.file.Files.isDirectory(path) || !java.nio.file.Files.exists(path)) {
        return;
      }
      java.nio.file.Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

        @Override
        public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
          if (!dir.equals(path)) {
            java.nio.file.Files.delete(dir);
          }
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
          java.nio.file.Files.delete(file);
          return FileVisitResult.CONTINUE;
        }

      });
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void deleteDir(final File file) throws RuntimeException {
    clearDir(file);
    deleteFile(file);
  }

  public static void deleteFile(final File file) {
    if (file.exists()) {
      file.delete();
    }
  }

  public static String digestDir(final File dir, final MessageDigest digest, final int bufferSize) {
    Check.isTrue(dir.exists(), "Dir not exist %s", dir);
    try {
      final var dirLength = dir.getCanonicalPath().length();
      for (final var file : FileUtils.listDir(dir, true)) {
        final var fileName = file.getAbsolutePath().substring(dirLength);
        digest.update(fileName.getBytes());
        if (file.isFile()) {
          digestFile(file, digest, bufferSize);
        }
      }
      return IBytes.wrap(digest.digest()).toHEX();
    }
    catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void digestFile(final File file, final MessageDigest digest, final int bufferSize) {
    checksum(file, new Checksum() {

      @Override
      public long getValue() {
        return 0;
      }

      @Override
      public void reset() {
      }

      @Override
      public void update(final byte[] b, final int off, final int len) {
        digest.update(b, off, len);
      }

      @Override
      public void update(final int b) {
        digest.update((byte) b);
      }

    }, bufferSize);
  }

  public static ISet<File> listDir(final File dir) {
    return listDir(dir, false);
  }

  public static ISet<File> listDir(final File dir, final boolean recursively) {
    if (!recursively) {
      return ISet.of(dir.listFiles());
    }
    else {
      final var result = MSet.<File>of();
      listDirRecursively(result, dir);
      return result.destroy();
    }
  }

  private static void listDirRecursively(final MSet<File> result, final File dir) {
    result.add(dir);
    for (final var file : dir.listFiles()) {
      if (file.isDirectory()) {
        listDirRecursively(result, file);
      }
      else {
        result.add(file);
      }
    }
  }

  public static IBytes loadFile(final File file) {
    try {
      return IBytes.read(new FileInputStream(file));
    }
    catch (final RuntimeException e) {
      throw e;
    }
    catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static IBytes loadFile(final String path) {
    return loadFile(new File(path));
  }

  public static void mkdirs(final File file) {
    if (!file.exists()) {
      file.mkdirs();
    }
  }

  public static void saveFile(final File file, final IBytes bytes) {
    try (var out = java.nio.file.Files.newOutputStream(
      file.toPath(),
      StandardOpenOption.CREATE,
      StandardOpenOption.WRITE,
      StandardOpenOption.TRUNCATE_EXISTING)) {
      bytes.asReader().writeToStream(out);
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void saveFile(final String file, final IBytes bytes) {
    saveFile(new File(file), bytes);
  }

}
