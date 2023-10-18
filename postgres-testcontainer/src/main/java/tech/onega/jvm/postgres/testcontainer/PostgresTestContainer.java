package tech.onega.jvm.postgres.testcontainer;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.OutputFrame;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.lang.Exec;
import tech.onega.jvm.std.log.Logger;
import tech.onega.jvm.std.log.Loggers;

@ThreadSafe
final public class PostgresTestContainer implements AutoCloseable {

  private final static Logger LOGGER = Loggers.find(PostgresTestContainer.class);

  public static PostgresTestContainer create() {
    return new PostgresTestContainer("postgres:16-alpine");
  }

  public static PostgresTestContainer create(final String dockerImage) {
    return new PostgresTestContainer(dockerImage);
  }

  private static GenericContainer<?> createContainer(final String dockerImage, final int containerPort, final String password) {
    final var container = new GenericContainer<>(dockerImage);
    container.withEnv("MALLOC_ARENA_MAX", "1");
    container.withEnv("POSTGRES_PASSWORD", password);
    container.withLogConsumer(PostgresTestContainer::log);
    container.withExposedPorts(containerPort);
    container.start();
    return container;
  }

  private static void log(final OutputFrame outputFrame) {
    LOGGER.info(outputFrame.getUtf8StringWithoutLineEnding());
  }

  private final GenericContainer<?> container;

  private final String host;

  private final int port;

  private final String password = "postgres";

  private final String user = "postgres";

  private final String database = "postgres";

  private PostgresTestContainer(final String dockerImage) {
    final var containerPort = 5432;
    this.container = createContainer(dockerImage, containerPort, this.password);
    this.host = this.container.getHost();
    this.port = this.container.getMappedPort(containerPort);
  }

  @Override
  public void close() {
    Exec.quietly(this.container::close);
  }

  public String getDatabase() {
    return this.database;
  }

  public String getHost() {
    return this.host;
  }

  public String getPassword() {
    return this.password;
  }

  public int getPort() {
    return this.port;
  }

  public String getUser() {
    return this.user;
  }

}
