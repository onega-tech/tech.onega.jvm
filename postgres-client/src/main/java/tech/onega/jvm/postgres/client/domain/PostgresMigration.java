package tech.onega.jvm.postgres.client.domain;

import tech.onega.jvm.std.annotation.NotThreadSafe;

@NotThreadSafe
public interface PostgresMigration extends Comparable<PostgresMigration> {

  @Override
  default int compareTo(final PostgresMigration o) {
    return Long.compare(this.version(), o.version());
  }

  void execute(PostgresConnection connection);

  String name();

  long version();

}
