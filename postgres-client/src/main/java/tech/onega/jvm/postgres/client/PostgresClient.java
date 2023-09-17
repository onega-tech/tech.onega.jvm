package tech.onega.jvm.postgres.client;

import java.util.Collection;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.lang.Lambda;

@ThreadSafe
public interface PostgresClient extends AutoCloseable {

  @Override
  void close();

  <M> PostgresModelMapper<M> createModelMapper(
    String tableName,
    String[] immutableFields,
    String[] mutableFields,
    String[] idFields,
    Lambda.Consumer2<PostgresRecord, M, Exception> toRecord,
    Lambda.Function<PostgresRecord, M, Exception> fromRecord);

  void exec(Lambda.Consumer<PostgresConnection, Exception> lambda);

  void execTransact(Lambda.Consumer<PostgresConnection, Exception> lambda);

  boolean isConnected();

  void migrate();

  void purgeData(Collection<String> tables);

  <R> R query(final Lambda.Function<PostgresConnection, R, Exception> lambda);

  <R> R queryTransact(Lambda.Function<PostgresConnection, R, Exception> lambda);

}
