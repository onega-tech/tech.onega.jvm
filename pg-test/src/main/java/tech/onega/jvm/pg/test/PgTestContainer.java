package tech.onega.jvm.pg.test;

import org.testcontainers.containers.GenericContainer;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.lang.Exec;

@ThreadSafe
final public class PgTestContainer implements AutoCloseable {

  public static PgTestContainer create() {
    return new PgTestContainer("postgres:16-alpine");
  }

  public static PgTestContainer create(final String dockerImage) {
    return new PgTestContainer(dockerImage);
  }

  private static GenericContainer<?> createContainer(final String dockerImage, final int containerPort, final String password) {
    final var container = new GenericContainer<>(dockerImage);
    container.withEnv("MALLOC_ARENA_MAX", "1");
    container.withEnv("POSTGRES_PASSWORD", password);
    container.withExposedPorts(containerPort);
    container.start();
    return container;
  }

  private final GenericContainer<?> container;

  private final String host;

  private final int port;

  private final String password = "postgres";

  private final String user = "postgres";

  private final String database = "postgres";

  private PgTestContainer(final String dockerImage) {
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
