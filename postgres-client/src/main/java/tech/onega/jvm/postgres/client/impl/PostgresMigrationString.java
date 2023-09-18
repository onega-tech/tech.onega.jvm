package tech.onega.jvm.postgres.client.impl;

import tech.onega.jvm.postgres.client.domain.PostgresConnection;
import tech.onega.jvm.postgres.client.domain.PostgresMigration;
import tech.onega.jvm.std.annotation.NotThreadSafe;

@NotThreadSafe
public class PostgresMigrationString implements PostgresMigration {

  private final long version;

  private final String sql;

  private final String name;

  public PostgresMigrationString(final long version, final String name, final String sql) {
    this.version = version;
    this.sql = sql;
    this.name = name;
  }

  @Override
  public void execute(final PostgresConnection connection) {
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
