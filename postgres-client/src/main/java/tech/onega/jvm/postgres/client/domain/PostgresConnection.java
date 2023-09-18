package tech.onega.jvm.postgres.client.domain;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;
import tech.onega.jvm.std.annotation.NotThreadSafe;
import tech.onega.jvm.std.annotation.Nullable;

@NotThreadSafe
public interface PostgresConnection {

  int[] batch(String query, Collection<Object[]> multiParams);

  int change(String query, Object... params);

  boolean exec(String query, Object... params);

  <R> Stream<R> find(PostgresResultMapper<R> resultMapper, String sql, Object... params);

  <R> Optional<R> findFirst(PostgresResultMapper<R> resultMapper, String sql, Object... params);

  <M> boolean modelContains(PostgresModelMapper<M> mapper, Object... idParams);

  boolean modelDelete(PostgresModelMapper<?> mapper, Object... idParams);

  <M> Stream<M> modelFind(PostgresModelMapper<M> mapper, @Nullable String whereClause, Object... whereParams);

  default <M> Stream<M> modelFindAll(final PostgresModelMapper<M> mapper) {
    return this.modelFind(mapper, null);
  }

  <M> Optional<M> modelFindById(PostgresModelMapper<M> mapper, Object... idParams);

  <M> boolean modelInsert(PostgresModelMapper<M> mapper, M model);

  <M> boolean modelUpdate(PostgresModelMapper<M> mapper, M model);

  @NotNull
  List<? extends PostgresRecord> query(String query, Object... params);

}
