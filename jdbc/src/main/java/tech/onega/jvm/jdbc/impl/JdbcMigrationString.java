package tech.onega.jvm.jdbc.impl;

import tech.onega.jvm.jdbc.domain.JdbcConnection;
import tech.onega.jvm.jdbc.domain.JdbcMigration;
import tech.onega.jvm.std.annotation.NotThreadSafe;

@NotThreadSafe
public class JdbcMigrationString implements JdbcMigration {

  private final long version;

  private final String sql;

  private final String name;

  public JdbcMigrationString(final long version, final String name, final String sql) {
    this.version = version;
    this.sql = sql;
    this.name = name;
  }

  @Override
  public void execute(final JdbcConnection connection) {
    try {
      connection.exec(this.sql());
    }
    catch (final Exception e) {
      throw new RuntimeException(
        "Can't execute migration with version:%s, name:%s, sql:%s".formatted(this.version(), this.name(), this.sql()),
        e);
    }
  }

  @Override
  public String name() {
    return this.name;
  }

  public String sql() {
    return this.sql;
  }

  @Override
  public long version() {
    return this.version;
  }

}
