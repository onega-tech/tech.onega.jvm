package tech.onega.jvm.jdbc.domain;

@FunctionalInterface
public interface JdbcResultMapper<R> {

  R fromRecord(JdbcRecord record) throws Exception;

}