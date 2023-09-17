package tech.onega.jvm.postgres.client;

public record PostgresObject(
  int type,
  Object value) {
}
