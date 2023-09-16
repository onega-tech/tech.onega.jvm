package tech.onega.jvm.jdbc.domain;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;
import tech.onega.jvm.std.annotation.NotThreadSafe;
import tech.onega.jvm.std.annotation.Nullable;

@NotThreadSafe
public interface JdbcConnection {

  int[] batch(String query, Collection<Object[]> multiParams);

  int change(String query, Object... params);

  boolean exec(String query, Object... params);

  <R> Stream<R> find(JdbcResultMapper<R> resultMapper, String sql, Object... params);

  <R> Optional<R> findFirst(JdbcResultMapper<R> resultMapper, String sql, Object... params);

  <M> boolean modelContains(JdbcModelMapper<M> mapper, Object... idParams);

  boolean modelDelete(JdbcModelMapper<?> mapper, Object... idParams);

  <M> Stream<M> modelFind(JdbcModelMapper<M> mapper, @Nullable String whereClause, Object... whereParams);

  default <M> Stream<M> modelFindAll(final JdbcModelMapper<M> mapper) {
    return this.modelFind(mapper, null);
  }

  <M> Optional<M> modelFindById(JdbcModelMapper<M> mapper, Object... idParams);

  <M> boolean modelInsert(JdbcModelMapper<M> mapper, M model);

  <M> boolean modelUpdate(JdbcModelMapper<M> mapper, M model);

  @NotNull
  List<? extends JdbcRecord> query(String query, Object... params);

}
