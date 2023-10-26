package tech.onega.jvm.arangodb.client.domain.api;

import java.util.concurrent.CompletableFuture;
import tech.onega.jvm.arangodb.client.domain.ArangoDbTransport;
import tech.onega.jvm.std.annotation.ThreadSafe;

@ThreadSafe
/**
 *  @see https://docs.arangodb.com/3.11/develop/http/general-request-handling/
 */
public interface ArangoDbApiMethod<V, R> {

  CompletableFuture<R> execute(ArangoDbTransport transport, V request);

}
