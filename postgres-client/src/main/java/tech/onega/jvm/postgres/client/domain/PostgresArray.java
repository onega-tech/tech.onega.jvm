package tech.onega.jvm.postgres.client.domain;

public record PostgresArray(
  String type,
  Object[] values) {
}
