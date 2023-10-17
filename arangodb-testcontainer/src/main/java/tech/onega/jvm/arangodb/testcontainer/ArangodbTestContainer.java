package tech.onega.jvm.arangodb.testcontainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.wait.strategy.Wait;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.lang.Exec;

@ThreadSafe
final public class ArangodbTestContainer implements AutoCloseable {

  private static final Logger LOGGER = LoggerFactory.getLogger(ArangodbTestContainer.class);

  public static ArangodbTestContainer create() {
    return new ArangodbTestContainer("arangodb:3.11.4");
  }

  public static ArangodbTestContainer create(final String dockerImage) {
    return new ArangodbTestContainer(dockerImage);
  }

  private static GenericContainer<?> createContainer(final String dockerImage, final int containerPort, final String password) {
    final var container = new GenericContainer<>(dockerImage);
    container.withEnv("MALLOC_ARENA_MAX", "1");
    container.withEnv("ARANGO_ROOT_PASSWORD", password);
    container.withEnv("ARANGODB_OVERRIDE_DETECTED_TOTAL_MEMORY", "1G");
    container.withEnv("ARANGODB_OVERRIDE_DETECTED_NUMBER_OF_CORES", "1");
    container.withEnv("ARANGO_STORAGE_ENGINE", "rocksdb");
    container.withExposedPorts(containerPort);
    container.withLogConsumer(ArangodbTestContainer::log);
    container.waitingFor(Wait.forLogMessage(".*is ready for business. Have fun!.*", 1));
    container.start();
    return container;
  }

  private static void log(final OutputFrame outputFrame) {
    LOGGER.info(outputFrame.getUtf8StringWithoutLineEnding());
  }

  private final GenericContainer<?> container;

  private final String host;

  private final int port;

  private final String password = "arangodb";

  private final String user = "root";

  private ArangodbTestContainer(final String dockerImage) {
    final var containerPort = 8529;
    this.container = createContainer(dockerImage, containerPort, this.password);
    this.host = this.container.getHost();
    this.port = this.container.getMappedPort(containerPort);
  }

  @Override
  public void close() {
    Exec.quietly(this.container::close);
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
