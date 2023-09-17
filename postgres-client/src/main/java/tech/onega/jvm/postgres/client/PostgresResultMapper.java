package tech.onega.jvm.postgres.client;

@FunctionalInterface
public interface PostgresResultMapper<R> {

  R fromRecord(PostgresRecord record) throws Exception;

}