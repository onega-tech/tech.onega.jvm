package tech.onega.jvm.arangodb.client.domain;

import java.util.concurrent.CompletableFuture;
import tech.onega.jvm.arangodb.client.domain.api.ArangoDbApiCollectionList;
import tech.onega.jvm.arangodb.client.domain.api.ArangoDbApiDatabaseCreate;
import tech.onega.jvm.arangodb.client.domain.api.ArangoDbApiDatabaseDrop;
import tech.onega.jvm.arangodb.client.domain.api.ArangoDbApiDatabaseGet;
import tech.onega.jvm.arangodb.client.domain.api.ArangoDbApiDatabaseList;
import tech.onega.jvm.std.annotation.ThreadSafe;

@ThreadSafe
public interface ArangoDbClient extends AutoCloseable {

  @Override
  void close();

  CompletableFuture<ArangoDbApiCollectionList.Response> collectionList(ArangoDbApiCollectionList.Request request);

  CompletableFuture<ArangoDbApiDatabaseCreate.Response> databaseCreate(ArangoDbApiDatabaseCreate.Request request);

  CompletableFuture<ArangoDbApiDatabaseDrop.Response> databaseDrop(ArangoDbApiDatabaseDrop.Request request);

  CompletableFuture<ArangoDbApiDatabaseGet.Response> databaseGet(ArangoDbApiDatabaseGet.Request request);

  CompletableFuture<ArangoDbApiDatabaseList.Response> databaseList();

}
