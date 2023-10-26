package tech.onega.jvm.arangodb.client.domain.api;

import java.util.concurrent.CompletableFuture;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import tech.onega.jvm.arangodb.client.domain.ArangoDbTransport;
import tech.onega.jvm.std.annotation.Nullable;
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.validate.Check;

public class ArangoDbApiDatabaseCreate implements ArangoDbApiMethod<ArangoDbApiDatabaseCreate.Request, ArangoDbApiDatabaseCreate.Response> {

  public record Request(
    /**
     * Has to contain a valid database name. The name must conform to the selected naming convention for databases.
     * If the name contains Unicode characters, the name must be NFC-normalized.
     * Non-normalized names will be rejected by arangod.
     */
    @NotBlank String name,
    /**
     * Optional object which can contain the following attributes:
     */
    @Nullable Request.Options options,
    /**
     * An array of user objects.
     * The users will be granted Administrate permissions for the new database.
     * Users that do not exist yet will be created.
     * If users is not specified or does not contain any users, the default user root will be used to ensure that
     * the new database will be accessible after it is created.
     * The root user is created with an empty password should it not exist.
     * Each user object can contain the following attributes:
     */
    @Nullable IList<Request.User> users) {

    public record Options(
      /**
       * Default replication factor for new collections created in this database.
       * Special values include “satellite”, which will replicate the collection to
       * every DB-Server (Enterprise Edition only), and 1, which disables replication. (cluster only)
       */
      int replicationFactor,
      /**
       * The sharding method to use for new collections in this database.
       * Valid values are: “”, “flexible”, or “single”.
       * The first two are equivalent. (cluster only)
       */
      String sharding,
      /**
       * Default write concern for new collections created in this database.
       * It determines how many copies of each shard are required to be in sync on the different DB-Servers.
       * If there are less than these many copies in the cluster, a shard refuses to write.
       * Writes to shards with enough up-to-date copies succeed at the same time, however.
       * The value of writeConcern cannot be greater than replicationFactor.
       * For SatelliteCollections, the writeConcern is automatically controlled to equal the number of DB-Servers and has a value of 0. (cluster only)
       */
      int writeConcern) {
    }

    public record User(
      /**
       * Login name of an existing user or one to be created.
       */
      @NotNull String username,
      /**
       * The user password as a string.
       * If not specified, it will default to an empty string.
       * The attribute is ignored for users that already exist.
       */
      @NotNull String passwd,
      /**
       * A flag indicating whether the user account should be activated or not.
       * If set to false, then the user won’t be able to log into the database.
       * The attribute is ignored for users that already exist.
       */
      boolean active,
      /**
       * A JSON object with extra user information.
       * It is used by the web interface to store graph viewer settings and saved queries.
       * Should not be set or modified by end users, as custom attributes will not be preserved.
       */
      @Nullable Object extra) {
    }

  }

  public record Response(
    /**
     * 201 - is returned if the database was created successfully.
     * 400 - is returned if the request parameters are invalid or if a database with the specified name already exists.
     * 403 - is returned if the request was not executed in the _system database.
     * 409 - is returned if a database with the specified name already exists.
     */
    int code,
    boolean error,
    @Nullable Integer errorNum,
    @Nullable String errorMessage,
    boolean result) {
  }

  @Override
  public CompletableFuture<ArangoDbApiDatabaseCreate.Response> execute(final ArangoDbTransport transport, final ArangoDbApiDatabaseCreate.Request request) {
    Check.valid(request, "Request is not valid");
    final var transportRequest = new ArangoDbTransport.Request<>(
      "POST",
      "/_api/database",
      ArangoDbApiDatabaseCreate.Response.class,
      null,
      request);
    return transport.execute(transportRequest).thenApply(r -> r.body());
  }

}
