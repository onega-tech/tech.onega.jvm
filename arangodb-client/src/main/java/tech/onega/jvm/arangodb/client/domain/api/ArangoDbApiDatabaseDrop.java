package tech.onega.jvm.arangodb.client.domain.api;

import java.util.concurrent.CompletableFuture;
import javax.validation.constraints.NotBlank;
import tech.onega.jvm.arangodb.client.domain.ArangoDbTransport;
import tech.onega.jvm.std.annotation.Nullable;
import tech.onega.jvm.std.validate.Check;

public class ArangoDbApiDatabaseDrop implements ArangoDbApiMethod<ArangoDbApiDatabaseDrop.Request, ArangoDbApiDatabaseDrop.Response> {

  public record Request(
    /**
     * The name of the database
     */
    @NotBlank String databaseName) {
  }

  public record Response(
    /**
     * 201 - is returned if the database was created successfully.
     * 400 - is returned if the request is malformed.
     * 403 - is returned if the request was not executed in the _system database.
     * 409 - is returned if the database could not be found.
     */
    int code,
    boolean error,
    @Nullable Integer errorNum,
    @Nullable String errorMessage,
    boolean result) {
  }

  @Override
  public CompletableFuture<ArangoDbApiDatabaseDrop.Response> execute(final ArangoDbTransport transport, final ArangoDbApiDatabaseDrop.Request request) {
    Check.valid(request, "Request is not valid");
    final var transportRequest = new ArangoDbTransport.Request<>(
      "DELETE",
      "/_api/database/" + request.databaseName(),
      ArangoDbApiDatabaseDrop.Response.class,
      null,
      request);
    return transport.execute(transportRequest).thenApply(r -> r.body());
  }

}
