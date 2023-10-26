package tech.onega.jvm.arangodb.client.domain.api;

import java.util.concurrent.CompletableFuture;
import tech.onega.jvm.arangodb.client.domain.ArangoDbTransport;
import tech.onega.jvm.std.annotation.Nullable;
import tech.onega.jvm.std.struct.set.ISet;
import tech.onega.jvm.std.validate.Check;

public class ArangoDbApiDatabaseList implements ArangoDbApiMethod<ArangoDbApiDatabaseList.Request, ArangoDbApiDatabaseList.Response> {

  public record Request() {
  }

  public record Response(
    /**
     * 200 - is returned if the list of database was compiled successfully.
     * 400 - is returned if the request is invalid.
     * 403 - is returned if the request was not executed in the _system database.
     */
    int code,
    boolean error,
    @Nullable Integer errorNum,
    @Nullable String errorMessage,
    @Nullable ISet<String> result) {
  }

  @Override
  public CompletableFuture<ArangoDbApiDatabaseList.Response> execute(final ArangoDbTransport transport, final ArangoDbApiDatabaseList.Request request) {
    Check.valid(request, "Request is not valid");
    final var transportRequest = new ArangoDbTransport.Request<>(
      "GET",
      "/_api/database",
      ArangoDbApiDatabaseList.Response.class,
      null,
      null);
    return transport.execute(transportRequest).thenApply(r -> r.body());
  }

}
