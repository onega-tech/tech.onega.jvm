package tech.onega.jvm.jdbc.domain;

import tech.onega.jvm.std.annotation.NotThreadSafe;

@NotThreadSafe
public interface JdbcMigration extends Comparable<JdbcMigration> {

  @Override
  default int compareTo(final JdbcMigration o) {
    return Long.compare(this.version(), o.version());
  }

  void execute(JdbcConnection connection);

  String name();

  long version();

}
