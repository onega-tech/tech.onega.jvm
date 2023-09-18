package tech.onega.jvm.arangodb.client.domain;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import tech.onega.jvm.std.annotation.Nullable;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.struct.map.IMultiMap;

@ThreadSafe
public interface ArangoDbHttpTransport extends AutoCloseable {

  record Request(
    @NotBlank String method,
    @NotBlank String uri,
    @NotNull IMultiMap<String, String> headers,
    Duration timeout,
    @Nullable byte[] body) {
  }

  record Response(
    int statusCode,
    @NotNull IMultiMap<String, String> headers,
    @NotBlank String uri,
    @Nullable byte[] body) {
  }

  @Override
  void close();

  CompletableFuture<ArangoDbHttpTransport.Response> execute(ArangoDbHttpTransport.Request request);

}
