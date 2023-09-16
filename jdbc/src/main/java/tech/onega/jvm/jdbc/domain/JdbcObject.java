package tech.onega.jvm.jdbc.domain;

public record JdbcObject(
  int type,
  Object value) {
}
