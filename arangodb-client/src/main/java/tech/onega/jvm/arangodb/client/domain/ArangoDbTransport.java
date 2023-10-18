package tech.onega.jvm.arangodb.client.domain;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import tech.onega.jvm.std.annotation.Nullable;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.struct.map.IMultiMap;

@ThreadSafe
public interface ArangoDbTransport extends AutoCloseable {

  final static class Request<R> {

    private final @NotBlank String method;

    private final @NotBlank String endpoint;

    private final @NotNull Class<R> responseType;

    private final @Nullable Duration timeout;

    private final @Nullable Object body;

    public Request(
      @NotBlank final String method,
      @NotBlank final String endpoint,
      @NotNull final Class<R> responseType,
      @NotNull final Duration timeout,
      @Nullable final Object body) {
      //
      this.method = method;
      this.endpoint = endpoint;
      this.responseType = responseType;
      this.timeout = timeout;
      this.body = body;
    }

    @Nullable
    public Object body() {
      return this.body;
    }

    @NotBlank
    public String endpoint() {
      return this.endpoint;
    }

    @NotBlank
    public String method() {
      return this.method;
    }

    @NotNull
    public Class<R> responseType() {
      return this.responseType;
    }

    @Nullable
    public Duration timeout() {
      return this.timeout;
    }

  }

  final static class Response<R> {

    private final @NotNull Request<R> request;

    private final int statusCode;

    private final @NotNull IMultiMap<String, String> headers;

    private final @Nullable R body;

    public Response(
      @NotNull final Request<R> request,
      final int statusCode,
      @NotNull final IMultiMap<String, String> headers,
      @Nullable final R body) {
      //
      this.request = request;
      this.statusCode = statusCode;
      this.headers = headers;
      this.body = body;
    }

    @Nullable
    public R body() {
      return this.body;
    }

    @NotNull
    public IMultiMap<String, String> headers() {
      return this.headers;
    }

    @NotNull
    public Request<R> request() {
      return this.request;
    }

    public int statusCode() {
      return this.statusCode;
    }

  }

  @Override
  void close();

  <R> CompletableFuture<ArangoDbTransport.Response<R>> execute(ArangoDbTransport.Request<R> transportRequest);

}
