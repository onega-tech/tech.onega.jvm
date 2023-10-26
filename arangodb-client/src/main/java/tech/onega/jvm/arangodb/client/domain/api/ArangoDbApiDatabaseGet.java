package tech.onega.jvm.arangodb.client.domain.api;

import java.util.concurrent.CompletableFuture;
import javax.validation.constraints.NotBlank;
import tech.onega.jvm.arangodb.client.domain.ArangoDbTransport;
import tech.onega.jvm.std.annotation.Nullable;
import tech.onega.jvm.std.validate.Check;

public class ArangoDbApiDatabaseGet implements ArangoDbApiMethod<ArangoDbApiDatabaseGet.Request, ArangoDbApiDatabaseGet.Response> {

  public record Request(
    /**
     * the name of database
     */
    @NotBlank String name) {
  }

  public record Response(
    /**
     * 200 - is returned if the information was retrieved successfully.
     * 400 - is returned if the request is invalid.
     * 404 - is returned if the database could not be found.
     */
    int code,
    boolean error,
    @Nullable Integer errorNum,
    @Nullable String errorMessage,
    Response.Result result) {

    public record Result(
      /**
       * the id of the current database
       */
      @NotBlank String id,
      /**
       * the name of the current database
       */
      @NotBlank String name,
      /**
       * whether or not the current database is the _system database
       */
      boolean isSystem,
      /**
       * the filesystem path of the current database
       */
      String path,
      /**
       * the default sharding method for collections created in this database
       */
      String sharding,
      /**
       * the default replication factor for collections in this database
       */
      int replicationFactor,
      /**
       * the default write concern for collections in this database
       */
      int writeConcern) {
    }

  }

  @Override
  public CompletableFuture<ArangoDbApiDatabaseGet.Response> execute(final ArangoDbTransport transport, final ArangoDbApiDatabaseGet.Request request) {
    Check.valid(request, "Request is not valid");
    final var transportRequest = new ArangoDbTransport.Request<>(
      "GET",
      "/_db/%s/_api/database/current".formatted(request.name()),
      ArangoDbApiDatabaseGet.Response.class,
      null,
      request);
    return transport.execute(transportRequest).thenApply(r -> r.body());
  }

}
