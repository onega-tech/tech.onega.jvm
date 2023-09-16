package tech.onega.jvm.jdbc.domain;

import java.util.Collection;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.lang.Lambda;

@ThreadSafe
public interface JdbcClient extends AutoCloseable {

  @Override
  void close();

  <M> JdbcModelMapper<M> createModelMapper(
    String tableName,
    String[] immutableFields,
    String[] mutableFields,
    String[] idFields,
    Lambda.Consumer2<JdbcRecord, M, Exception> toRecord,
    Lambda.Function<JdbcRecord, M, Exception> fromRecord);

  void exec(Lambda.Consumer<JdbcConnection, Exception> lambda);

  void execTransact(Lambda.Consumer<JdbcConnection, Exception> lambda);

  boolean isConnected();

  void migrate();

  void purgeData(Collection<String> tables);

  <R> R query(final Lambda.Function<JdbcConnection, R, Exception> lambda);

  <R> R queryTransact(Lambda.Function<JdbcConnection, R, Exception> lambda);

}
