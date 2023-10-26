package tech.onega.jvm.arangodb.client.domain.api;

import java.util.concurrent.CompletableFuture;
import javax.validation.constraints.NotBlank;
import tech.onega.jvm.arangodb.client.domain.ArangoDbTransport;
import tech.onega.jvm.std.annotation.Nullable;
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.validate.Check;

public class ArangoDbApiCollectionList implements ArangoDbApiMethod<ArangoDbApiCollectionList.Request, ArangoDbApiCollectionList.Response> {

  public record Request(
    @NotBlank String databaseName,
    /**
     * Whether or not system collections should be excluded from the result.
     */
    boolean excludeSystem) {
  }

  public record Response(
    /**
     * 200 - is returned if the list of database was compiled successfully.
     */
    int code,
    boolean error,
    @Nullable Integer errorNum,
    @Nullable String errorMessage,
    @Nullable IList<Response.Result> result) {

    public record Result(
      /**
       * The identifier of the collection.
       */
      @NotBlank String id,
      /**
       * The name of the collection.
       */
      @NotBlank String name,
      /**
       * The status of the collection as number.
       *   3: loaded
       *   5: deleted
       * Every other status indicates a corrupted collection.
       */
      int status,
      /**
       * The type of the collection as number.
       *   2: document collection (normal case)
       *   3: edge collection
       */
      int type,
      /**
       * If true then the collection is a system collection.
       */
      boolean isSystem,
      String globallyUniqueId) {
    }

  }

  @Override
  public CompletableFuture<ArangoDbApiCollectionList.Response> execute(final ArangoDbTransport transport, final ArangoDbApiCollectionList.Request request) {
    Check.valid(request, "Request is not valid");
    final var transportRequest = new ArangoDbTransport.Request<>(
      "GET",
      "/_db/%s/_api/collection?excludeSystem=%s".formatted(
        request.databaseName(),
        request.excludeSystem() ? "true" : "false"),
      ArangoDbApiCollectionList.Response.class,
      null,
      null);
    return transport.execute(transportRequest).thenApply(r -> r.body());
  }

}
