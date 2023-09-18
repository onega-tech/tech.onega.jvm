package tech.onega.jvm.postgres.client.domain;

public record PostgresObject(
  int type,
  Object value) {
}
