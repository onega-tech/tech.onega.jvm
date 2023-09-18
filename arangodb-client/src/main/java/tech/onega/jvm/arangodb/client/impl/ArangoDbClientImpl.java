package tech.onega.jvm.arangodb.client.impl;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import tech.onega.jvm.arangodb.client.domain.ArangoDbClient;
import tech.onega.jvm.arangodb.client.domain.ArangoDbHttpTransport;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.codec.base.Base64Codec;
import tech.onega.jvm.std.lang.Exec;
import tech.onega.jvm.std.struct.hash.KV;
import tech.onega.jvm.std.struct.map.IMultiMap;
import tech.onega.jvm.std.validate.Check;

@ThreadSafe
public class ArangoDbClientImpl implements ArangoDbClient {

  public record Config(
    @NotBlank String host,
    @Min(1) int port,
    @NotBlank String user,
    @NotBlank String password,
    @NotNull Duration requestTimeout,
    boolean https) {
  }

  public static ArangoDbClient createDefaultClient(final Config config) {
    Check.valid(config, "Config is not valid");
    final var transportConfig = new ArangoHttpTransportJavaNet.Config(
      true,
      false,
      Executors::newCachedThreadPool);
    final var httpTransport = new ArangoHttpTransportJavaNet(transportConfig);
    return new ArangoDbClientImpl(config, httpTransport);
  }

  private final Config config;

  private final ArangoDbHttpTransport httpTransport;

  private ArangoDbClientImpl(final Config config, final ArangoDbHttpTransport httpTransport) {
    this.config = config;
    this.httpTransport = httpTransport;
  }

  @Override
  public void close() {
    Exec.quietly(this.httpTransport::close);
  }

  private ArangoDbHttpTransport.Request createHttpTransportRequestGet(final String endpoint) {
    final var authToken = Base64Codec.encodeAsString("%s:%s".formatted(this.config.user(), this.config.password()).getBytes(StandardCharsets.UTF_8), true);
    final var headers = IMultiMap.<String, String>of(
      KV.of("accept", "application/json"),
      KV.of("authorization", "Basic " + authToken));
    final var uri = "%s%s:%s%s".formatted(
      (this.config.https() ? "https://" : "http"),
      this.config.host(),
      this.config.port(),
      endpoint);
    return new ArangoDbHttpTransport.Request(
      "GET",
      uri,
      headers,
      this.config.requestTimeout(),
      null);
  }

  public CompletableFuture<ArangoDbHttpTransport.Response> listAllDatabasesAsync() {
    final var request = this.createHttpTransportRequestGet("/_api/database");
    return this.httpTransport.execute(request);
  }

}
