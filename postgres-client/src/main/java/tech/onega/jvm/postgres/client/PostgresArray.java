package tech.onega.jvm.postgres.client;

public record PostgresArray(
  String type,
  Object[] values) {
}
