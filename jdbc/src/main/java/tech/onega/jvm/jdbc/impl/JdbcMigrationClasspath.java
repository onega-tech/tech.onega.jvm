package tech.onega.jvm.jdbc.impl;

import java.nio.charset.StandardCharsets;
import tech.onega.jvm.std.annotation.NotThreadSafe;

@NotThreadSafe
public class JdbcMigrationClasspath extends JdbcMigrationString {

  private static String loadSql(final String classpath) {
    try (var inputStream = ClassLoader.getSystemResourceAsStream(classpath)) {
      return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }
    catch (final Exception e) {
      throw new RuntimeException("Can't load migration for classPath: " + classpath, e);
    }
  }

  private static long parseVersion(final String classpath) {
    final var localPaths = classpath.split("\\/");
    final var localPath = localPaths[localPaths.length - 1];
    final var parts = localPath.contains("-")
      ? localPath.split("\\-")
      : localPath.split("\\.");
    return Long.parseLong(parts[0]);
  }

  public JdbcMigrationClasspath(final String classpath) {
    super(parseVersion(classpath), classpath, loadSql(classpath));
  }

}
