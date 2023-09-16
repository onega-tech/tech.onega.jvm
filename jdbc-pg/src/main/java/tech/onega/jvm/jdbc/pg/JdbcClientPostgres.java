package tech.onega.jvm.jdbc.pg;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.metrics.micrometer.MicrometerMetricsTrackerFactory;
import io.micrometer.core.instrument.MeterRegistry;
import tech.onega.jvm.jdbc.domain.JdbcClient;
import tech.onega.jvm.jdbc.domain.JdbcConnection;
import tech.onega.jvm.jdbc.domain.JdbcModelMapper;
import tech.onega.jvm.jdbc.domain.JdbcRecord;
import tech.onega.jvm.jdbc.impl.JdbcMigrationClasspath;
import tech.onega.jvm.std.annotation.Nullable;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.lang.Lambda;
import tech.onega.jvm.std.log.Logger;
import tech.onega.jvm.std.log.Loggers;
import tech.onega.jvm.std.validate.Check;

@ThreadSafe
public class JdbcClientPostgres implements JdbcClient {

  public record Config(
    @NotBlank String host,
    @Positive int port,
    @NotBlank String user,
    @NotBlank String password,
    @NotBlank String database,
    @Positive int maximumPoolSize,
    @Nullable String migrationTable,
    @Nullable Set<String> migrationResources) {
  }

  private static final Logger LOGGER = Loggers.find(JdbcClientPostgres.class);

  /**
   * @see https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby
   */
  private static HikariDataSource createHikariDataSource(final Config config, @Nullable final MeterRegistry meterRegistry) {
    final var hikariConfig = new HikariConfig();
    hikariConfig.setJdbcUrl("jdbc:postgresql://%s:%s/%s".formatted(config.host(), config.port(), config.database()));
    hikariConfig.setUsername(config.user());
    hikariConfig.setPassword(config.password());
    //
    hikariConfig.addDataSourceProperty("cachePrepStmts", true);
    hikariConfig.addDataSourceProperty("prepStmtCacheSize", 250);
    hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
    //
    hikariConfig.setMinimumIdle(1);
    hikariConfig.setLeakDetectionThreshold(Duration.ofMinutes(5).toMillis());
    hikariConfig.setAutoCommit(true);
    hikariConfig.setConnectionTimeout(Duration.ofSeconds(30).toMillis());
    hikariConfig.setValidationTimeout(Duration.ofSeconds(5).toMillis());
    hikariConfig.setIdleTimeout(Duration.ofMinutes(1).toMillis());
    hikariConfig.setKeepaliveTime(Duration.ofSeconds(60).toMillis());
    hikariConfig.setMaxLifetime(Duration.ofMinutes(60).toMillis());
    hikariConfig.setReadOnly(false);
    hikariConfig.setConnectionTestQuery("SELECT 1");
    hikariConfig.setPoolName("%s|%s|%s|%s".formatted(JdbcClientPostgres.class.getName(), config.host(), config.port(), config.database()));
    hikariConfig.setMaximumPoolSize(config.maximumPoolSize());
    hikariConfig.setTransactionIsolation(String.valueOf(Connection.TRANSACTION_READ_COMMITTED));
    if (meterRegistry != null) {
      hikariConfig.setMetricsTrackerFactory(new MicrometerMetricsTrackerFactory(meterRegistry));
    }
    hikariConfig.setAllowPoolSuspension(true);
    hikariConfig.setRegisterMbeans(true);
    return new HikariDataSource(hikariConfig);
  }

  private final Config config;

  private final HikariDataSource dataSource;

  public JdbcClientPostgres(final Config config) {
    this(config, null);
  }

  public JdbcClientPostgres(final Config config, @Nullable final MeterRegistry meterRegistry) {
    Check.valid(config, "Config is not valid");
    this.config = config;
    this.dataSource = createHikariDataSource(config, meterRegistry);
    //
  }

  @Override
  public void close() {
    this.dataSource.close();
  }

  @Override
  public <M> JdbcModelMapper<M> createModelMapper(
    final String tableName,
    final String[] immutableFields,
    final String[] mutableFields,
    final String[] idFields,
    final Lambda.Consumer2<JdbcRecord, M, Exception> toRecord,
    final Lambda.Function<JdbcRecord, M, Exception> fromRecord) {
    //
    return new JdbcModelMapper<M>(
      JdbcRecordPostgres::new,
      tableName,
      immutableFields,
      mutableFields,
      idFields,
      toRecord,
      fromRecord);
  }

  @Override
  public void exec(final Lambda.Consumer<JdbcConnection, Exception> lambda) {
    try (var connection = this.dataSource.getConnection()) {
      connection.setAutoCommit(true);
      lambda.invoke(new JdbcConnectionPostgres(connection));
    }
    catch (final Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @Override
  public void execTransact(final Lambda.Consumer<JdbcConnection, Exception> lambda) {
    try (var connection = this.dataSource.getConnection()) {
      LOGGER.debug("BEGIN");
      connection.setAutoCommit(false);
      try {
        lambda.invoke(new JdbcConnectionPostgres(connection));
        LOGGER.debug("COMMIT");
        connection.commit();
      }
      catch (final Exception e) {
        LOGGER.debug("ROLLBACK");
        connection.rollback();
        throw e;
      }
    }
    catch (final Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  public Config getConfig() {
    return this.config;
  }

  @Override
  public boolean isConnected() {
    try (var connection = this.dataSource.getConnection()) {
      return connection.isValid(1);
    }
    catch (final SQLException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @Override
  public void migrate() {
    Check.notBlank(this.config.migrationTable(), "Migration table can't be blank");
    Check.notNull(this.config.migrationResources(), "Migrations can't be null");
    //
    LOGGER.info("Start migrate process");
    //create table
    this.exec(connection -> connection.exec("""
          CREATE TABLE IF NOT EXISTS %s (
            version BIGINT NOT NULL PRIMARY KEY,
            create_date TIMESTAMP(3) NOT NULL DEFAULT NOW()
          )
        """.formatted(this.config.migrationTable())));
    //find last migration versions
    final long lastMigratedVersion = this.query(connection -> {
      return connection
        .query("SELECT COALESCE((SELECT MAX(version) FROM %s), 0) as v".formatted(this.config.migrationTable()))
        .get(0)
        .getLong("v")
        .get();
    });
    LOGGER.info("Current migartion version is: {}", lastMigratedVersion);
    //create migrations obj
    final var migrations = this.config.migrationResources.stream().map(JdbcMigrationClasspath::new).toList();
    //find new migrations
    final var newMigrations = migrations.stream()
      .filter(v -> lastMigratedVersion == 0 ? true : v.version() > lastMigratedVersion)
      .sorted()
      .collect(Collectors.toList());
    //apply new migrations
    for (final var migration : newMigrations) {
      LOGGER.info("Start migration. Version: {}. Name: {}", migration.version(), migration.name());
      this.execTransact(connection -> {
        migration.execute(connection);
        connection.change("INSERT INTO %s(version,create_date) VALUES(?, NOW())".formatted(this.config.migrationTable()), migration.version());
        LOGGER.info("Complete migration. Version: {}", migration.version());
      });
    }
    //done
    LOGGER.info("Migrate process is completed");
  }

  @Override
  public void purgeData(final Collection<String> tables) {
    LOGGER.info("Purge tables: {}, ", tables);
    this.execTransact(connection -> {
      final StringBuilder sql;
      {
        sql = new StringBuilder();
        for (final String table : tables) {
          sql.append(String.format("\n ALTER TABLE %s DISABLE TRIGGER ALL;", table));
        }
        for (final String table : tables) {
          sql.append(String.format("\n DELETE FROM %s;", table));
        }
        for (final String table : tables) {
          sql.append(String.format("\n ALTER TABLE %s ENABLE TRIGGER ALL;", table));
        }
      }
      connection.exec(sql.toString());
    });
  }

  @Override
  public <R> R query(final Lambda.Function<JdbcConnection, R, Exception> lambda) {
    try (var connection = this.dataSource.getConnection()) {
      connection.setAutoCommit(true);
      return lambda.invoke(new JdbcConnectionPostgres(connection));
    }
    catch (final Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @Override
  public <R> R queryTransact(final Lambda.Function<JdbcConnection, R, Exception> lambda) {
    try (var connection = this.dataSource.getConnection()) {
      LOGGER.debug("BEGIN");
      connection.setAutoCommit(false);
      try {
        final var r = lambda.invoke(new JdbcConnectionPostgres(connection));
        LOGGER.debug("COMMIT");
        connection.commit();
        return r;
      }
      catch (final Exception e) {
        LOGGER.debug("ROLLBACK");
        connection.rollback();
        throw e;
      }
    }
    catch (final Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

}
