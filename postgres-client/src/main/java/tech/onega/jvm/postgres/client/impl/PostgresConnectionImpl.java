package tech.onega.jvm.postgres.client.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;
import tech.onega.jvm.postgres.client.domain.PostgresArray;
import tech.onega.jvm.postgres.client.domain.PostgresConnection;
import tech.onega.jvm.postgres.client.domain.PostgresModelMapper;
import tech.onega.jvm.postgres.client.domain.PostgresObject;
import tech.onega.jvm.postgres.client.domain.PostgresRecord;
import tech.onega.jvm.postgres.client.domain.PostgresResultMapper;
import tech.onega.jvm.std.annotation.NotThreadSafe;
import tech.onega.jvm.std.annotation.Nullable;
import tech.onega.jvm.std.log.Logger;
import tech.onega.jvm.std.log.Loggers;
import tech.onega.jvm.std.validate.Check;

@NotThreadSafe
class PostgresConnectionImpl implements PostgresConnection {

  private static final Logger LOGGER = Loggers.find(PostgresConnectionImpl.class);

  private static void applyStatementParams(final PreparedStatement statement, final Object... params) throws SQLException {
    for (var i = 0; i < params.length; i++) {
      final var param = params[i];
      if (param instanceof final PostgresArray jdbcArray) {
        final var sqlArray = statement.getConnection().createArrayOf(jdbcArray.type(), jdbcArray.values());
        statement.setArray(i + 1, sqlArray);
      }
      else if (param instanceof final PostgresObject jdbcObject) {
        statement.setObject(i + 1, jdbcObject.value(), jdbcObject.type());
      }
      else {
        statement.setObject(i + 1, param);
      }
    }
  }

  private final Connection connection;

  public PostgresConnectionImpl(final Connection connection) {
    this.connection = connection;
  }

  @Override
  public int[] batch(final String query, final Collection<Object[]> multiParams) {
    LOGGER.debug("BATCH - {}", query);
    try (var statement = this.connection.prepareStatement(query.trim())) {
      for (final var params : multiParams) {
        applyStatementParams(statement, params);
        statement.addBatch();
      }
      return statement.executeBatch();
    }
    catch (final Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @Override
  public int change(final String query, final Object... params) {
    LOGGER.debug("UPDATE - {}", query);
    try (var statement = this.connection.prepareStatement(query.trim())) {
      applyStatementParams(statement, params);
      return statement.executeUpdate();
    }
    catch (final Exception e) {
      LOGGER.error(query);
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @Override
  public boolean exec(final String query, final Object... params) {
    LOGGER.debug("EXEC - {}", query);
    try (var statement = this.connection.prepareStatement(query.trim())) {
      applyStatementParams(statement, params);
      return statement.execute();
    }
    catch (final Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @Override
  public <R> Stream<R> find(final PostgresResultMapper<R> resultMapper, final String sql, final Object... params) {
    return this.query(sql, params).stream().map(r -> {
      try {
        return resultMapper.fromRecord(r);
      }
      catch (final Exception e) {
        throw new RuntimeException(e);
      }
    });
  }

  @Override
  public <R> Optional<R> findFirst(final PostgresResultMapper<R> resultMapper, final String sql, final Object... params) {
    return this.find(resultMapper, sql, params).findFirst();
  }

  @Override
  public <M> boolean modelContains(final PostgresModelMapper<M> mapper, final Object... idParams) {
    final var idFields = mapper.idFields();
    final var sql = new StringBuilder();
    sql
      .append("SELECT 1 FROM ")
      .append(mapper.tableName());
    //where
    {
      sql.append(" WHERE ");
      for (var i = 0; i < idFields.size(); i++) {
        final var field = idFields.get(i);
        sql.append('"');
        sql.append(field);
        sql.append('"');
        sql.append("=?");
        if (i + 1 < idFields.size()) {
          sql.append(" AND ");
        }
      }
    }
    sql.append(" LIMIT 1");
    return !this.query(sql.toString(), idParams).isEmpty();
  }

  @Override
  public boolean modelDelete(final PostgresModelMapper<?> mapper, final Object... idParams) {
    try {
      final var idFields = mapper.idFields();
      final var sql = new StringBuilder();
      sql
        .append("DELETE FROM ")
        .append(mapper.tableName());
      //where
      {
        sql.append(" WHERE ");
        for (var i = 0; i < idFields.size(); i++) {
          final var field = idFields.get(i);
          sql.append('"');
          sql.append(field);
          sql.append('"');
          sql.append("=?");
          if (i + 1 < idFields.size()) {
            sql.append(" AND ");
          }
        }
      }
      return this.change(sql.toString().trim(), idParams) == 1;
    }
    catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public <M> Stream<M> modelFind(final PostgresModelMapper<M> mapper, @Nullable final String whereClause, final Object... whereParams) {
    final var selectFields = mapper.allFields();
    final var sql = new StringBuilder();
    sql
      .append("SELECT ");
    {//fields
      for (var i = 0; i < selectFields.size(); i++) {
        final var field = selectFields.get(i);
        sql.append('"');
        sql.append(field);
        sql.append('"');
        if (i + 1 < selectFields.size()) {
          sql.append(',');
        }
      }
    }
    sql
      .append(" FROM ")
      .append(mapper.tableName());
    if (whereClause != null) {
      sql.append(" WHERE ").append(whereClause);
    }
    return this.find(mapper, sql.toString(), whereParams);
  }

  @Override
  public <M> Optional<M> modelFindById(final PostgresModelMapper<M> mapper, final Object... idParams) {
    final var selectFields = mapper.allFields();
    final var idFields = mapper.idFields();
    final var sql = new StringBuilder();
    sql
      .append("SELECT ");
    {//fields
      for (var i = 0; i < selectFields.size(); i++) {
        final var field = selectFields.get(i);
        sql.append('"');
        sql.append(field);
        sql.append('"');
        if (i + 1 < selectFields.size()) {
          sql.append(',');
        }
      }
    }
    sql
      .append(" FROM ")
      .append(mapper.tableName());
    //where
    {
      sql.append(" WHERE ");
      for (var i = 0; i < idFields.size(); i++) {
        final var field = idFields.get(i);
        sql.append('"');
        sql.append(field);
        sql.append('"');
        sql.append("=?");
        if (i + 1 < idFields.size()) {
          sql.append(" AND ");
        }
      }
    }
    sql.append(" LIMIT 1");
    return this.findFirst(mapper, sql.toString(), idParams);
  }

  @Override
  public <M> boolean modelInsert(final PostgresModelMapper<M> mapper, final M model) {
    Check.valid(model);
    try {
      final var record = mapper.toRecord(model);
      final var insertFields = mapper.allFields();
      final var sqlParams = new ArrayList<>(insertFields.size());
      final var sql = new StringBuilder();
      sql
        .append("INSERT INTO ")
        .append(mapper.tableName());
      //fields
      {
        sql.append('(');
        for (var i = 0; i < insertFields.size(); i++) {
          final var field = insertFields.get(i);
          sql.append('"');
          sql.append(field);
          sql.append('"');
          sqlParams.add(record.getObject(field).orElse(null));
          if (i + 1 < insertFields.size()) {
            sql.append(',');
          }
        }
        sql.append(')');
      }
      //values
      {
        sql.append(" VALUES (");
        for (var i = 0; i < insertFields.size(); i++) {
          sql.append('?');
          if (i + 1 < insertFields.size()) {
            sql.append(',');
          }
        }
        sql.append(')');
      }
      return this.change(sql.toString().trim(), sqlParams.toArray()) == 1;
    }
    catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public <M> boolean modelUpdate(final PostgresModelMapper<M> mapper, final M model) {
    Check.valid(model);
    try {
      final var record = mapper.toRecord(model);
      final var updateFields = mapper.mutableFields();
      final var idFields = mapper.idFields();
      final var sqlParams = new ArrayList<>(updateFields.size() + idFields.size());
      final var sql = new StringBuilder();
      sql
        .append("UPDATE ")
        .append(mapper.tableName());
      //set
      {
        sql.append(" SET ");
        for (var i = 0; i < updateFields.size(); i++) {
          final var field = updateFields.get(i);
          sql.append('"');
          sql.append(field);
          sql.append('"');
          sql.append("=?");
          sqlParams.add(record.getObject(field).orElse(null));
          if (i + 1 < updateFields.size()) {
            sql.append(',');
          }
        }
      }
      //where
      {
        sql.append(" WHERE ");
        for (var i = 0; i < idFields.size(); i++) {
          final var field = idFields.get(i);
          sql.append('"');
          sql.append(field);
          sql.append('"');
          sql.append("=?");
          sqlParams.add(record.getObject(field).orElse(null));
          if (i + 1 < idFields.size()) {
            sql.append(" AND ");
          }
        }
      }
      return this.change(sql.toString().trim(), sqlParams.toArray()) == 1;
    }
    catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  @NotNull
  public List<? extends PostgresRecord> query(final String query, final Object... params) {
    LOGGER.debug("SELECT - {}", query);
    final var result = new ArrayList<PostgresRecordImpl>();
    try (var statement = this.connection.prepareStatement(query.trim())) {
      applyStatementParams(statement, params);
      try (var resultSet = statement.executeQuery()) {
        final var metaData = resultSet.getMetaData();
        while (resultSet.next()) {
          final var record = new PostgresRecordImpl();
          for (var columnIndex = 1; columnIndex <= metaData.getColumnCount(); columnIndex++) {
            final var columnName = metaData.getColumnName(columnIndex);
            record.set(columnName, resultSet.getObject(columnIndex));
          }
          result.add(record);
        }
      }
    }
    catch (final Exception e) {
      LOGGER.error(query);
      throw new RuntimeException(e.getMessage(), e);
    }
    return result;
  }

}
