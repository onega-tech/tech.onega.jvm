package tech.onega.jvm.arangodb.client.domain.api;

import java.util.concurrent.CompletableFuture;
import tech.onega.jvm.arangodb.client.domain.ArangoDbTransport;
import tech.onega.jvm.std.annotation.ThreadSafe;

@ThreadSafe
public interface ArrangoDbApiMethod<V, R> {

  CompletableFuture<R> execute(ArangoDbTransport transport, V request);

}
