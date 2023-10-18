package tech.onega.jvm.arangodb.client.domain;

import java.util.concurrent.CompletableFuture;
import tech.onega.jvm.arangodb.client.domain.api.ArrangoDbApiDatabaseList;
import tech.onega.jvm.std.annotation.ThreadSafe;

@ThreadSafe
public interface ArangoDbClient extends AutoCloseable {

  @Override
  void close();

  CompletableFuture<ArrangoDbApiDatabaseList.Response> listAllDatabasesAsync();

}
