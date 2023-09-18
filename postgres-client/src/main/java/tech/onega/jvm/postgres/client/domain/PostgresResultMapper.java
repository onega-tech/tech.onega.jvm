package tech.onega.jvm.postgres.client.domain;

@FunctionalInterface
public interface PostgresResultMapper<R> {

  R fromRecord(PostgresRecord record) throws Exception;

}