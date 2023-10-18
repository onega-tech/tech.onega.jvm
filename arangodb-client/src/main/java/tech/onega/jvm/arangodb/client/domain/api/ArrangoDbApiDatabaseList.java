package tech.onega.jvm.arangodb.client.domain.api;

import java.util.concurrent.CompletableFuture;
import tech.onega.jvm.arangodb.client.domain.ArangoDbTransport;
import tech.onega.jvm.std.annotation.Nullable;
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.validate.Check;

public class ArrangoDbApiDatabaseList implements ArrangoDbApiMethod<ArrangoDbApiDatabaseList.Request, ArrangoDbApiDatabaseList.Response> {

  public record Request() {
  }

  public record Response(
    int code,
    boolean error,
    @Nullable Integer errorNum,
    @Nullable String errorMessage,
    @Nullable IList<String> result) {
  }

  @Override
  public CompletableFuture<ArrangoDbApiDatabaseList.Response> execute(final ArangoDbTransport transport, final ArrangoDbApiDatabaseList.Request request) {
    final var transportRequest = new ArangoDbTransport.Request<ArrangoDbApiDatabaseList.Response>(
      "GET",
      "/_api/database",
      ArrangoDbApiDatabaseList.Response.class,
      null,
      null);
    return transport.execute(transportRequest).thenApply(r -> {
      Check.equals(r.statusCode(), 200, "Wrong status Code");
      return r.body();
    });
  }

}
