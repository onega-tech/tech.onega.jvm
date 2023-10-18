package tech.onega.jvm.arangodb.client.impl;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import tech.onega.jvm.arangodb.client.domain.ArangoDbClient;
import tech.onega.jvm.arangodb.client.domain.ArangoDbTransport;
import tech.onega.jvm.arangodb.client.domain.api.ArrangoDbApiDatabaseList;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.lang.Exec;
import tech.onega.jvm.std.validate.Check;

/**
 * @see https://docs.arangodb.com/3.11/develop/http/general-request-handling/
 * @see https://github.com/arangodb/arangodb-java-driver/tree/main/http
 */
@ThreadSafe
public class ArangoDbClientImpl implements ArangoDbClient {

  public record Config(
    @NotBlank String host,
    @Min(1) int port,
    @NotBlank String user,
    @NotBlank String password,
    @NotNull Duration requestTimeout,
    boolean velocityPack,
    boolean https,
    boolean followRedirects,
    boolean http2) {
  }

  public static ArangoDbClient createDefaultClient(final Config config) {
    Check.valid(config, "Config is not valid");
    final var transportConfig = new ArangoTransportJavaNet.Config(
      config.host(),
      config.port(),
      config.user(),
      config.password(),
      config.requestTimeout(),
      config.velocityPack(),
      config.https(),
      config.followRedirects(),
      config.http2(),
      Executors::newCachedThreadPool);
    final var httpTransport = new ArangoTransportJavaNet(transportConfig);
    return new ArangoDbClientImpl(config, httpTransport);
  }

  private final Config config;

  private final ArangoDbTransport transport;

  public ArangoDbClientImpl(final Config config, final ArangoDbTransport transport) {
    this.config = config;
    this.transport = transport;
  }

  @Override
  public void close() {
    Exec.quietly(this.transport::close);
  }

  public Config getConfig() {
    return this.config;
  }

  @Override
  public CompletableFuture<ArrangoDbApiDatabaseList.Response> listAllDatabasesAsync() {
    return new ArrangoDbApiDatabaseList().execute(this.transport, new ArrangoDbApiDatabaseList.Request());
  }

}
