package tech.onega.jvm.arangodb.client;

import java.util.concurrent.CompletableFuture;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.struct.hash.KV;
import tech.onega.jvm.std.struct.map.IMultiMap;
import tech.onega.jvm.std.validate.Check;

@ThreadSafe
public class ArangoDbClientImpl implements ArangoDbClient {

  private final ArangoDbHttpClient httpClient;

  public ArangoDbClientImpl(final ArangoDbClientConfig config) {
    Check.valid(config, "Config is not valid");
    this.httpClient = new ArangoDbHttpClient(config);
  }

  @Override
  public void close() {
    this.httpClient.close();
  }

  public CompletableFuture<ArangoDbHttpClient.Response> listAllDatabasesAsync() {
    final var request = new ArangoDbHttpClient.Request(
      "GET",
      "/_api/database",
      IMultiMap.<String, String>of(KV.of("accept", "application/json")),
      null);
    return this.httpClient.executeAsync(request);
  }

}
