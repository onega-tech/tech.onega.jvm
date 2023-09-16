package tech.onega.jvm.jdbc.domain;

public record JdbcArray(
  String type,
  Object[] values) {
}
