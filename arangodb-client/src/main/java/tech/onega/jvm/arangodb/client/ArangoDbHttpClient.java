package tech.onega.jvm.arangodb.client;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodySubscribers;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import tech.onega.jvm.arangodb.client.domain.ArangoDbClientConfig;
import tech.onega.jvm.std.annotation.Nullable;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.lang.Exec;
import tech.onega.jvm.std.struct.map.IMultiMap;
import tech.onega.jvm.std.struct.map.MMultiMap;
import tech.onega.jvm.std.validate.Check;

/**
 * @see https://github.com/arangodb/arangodb-java-driver/blob/main/http/src/main/java/com/arangodb/http/
 * @see https://www.baeldung.com/java-9-http-client
 * @see https://docs.arangodb.com/3.11/develop/http/databases/
 */
@ThreadSafe
class ArangoDbHttpClient implements AutoCloseable {

  private static class AuthenticatorImpl extends Authenticator {

    private final ArangoDbClientConfig config;

    AuthenticatorImpl(final ArangoDbClientConfig config) {
      this.config = config;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication(
        this.config.user(),
        this.config.password().toCharArray());
    }

  }

  record Request(
    @NotBlank String method,
    @NotBlank String uri,
    @NotNull IMultiMap<String, String> headers,
    @Nullable byte[] body) {
  }

  record Response(
    int statusCode,
    @NotNull IMultiMap<String, String> headers,
    @NotBlank String uri,
    @Nullable byte[] body) {
  }

  private static HttpRequest createHttpRequest(final ArangoDbHttpClient.Request request, final ArangoDbClientConfig config) {
    Check.valid(request, "Request is not valid");
    final var requestBuilder = HttpRequest.newBuilder();
    requestBuilder.uri(URI.create("%s:%s%s".formatted(config.host(), config.port(), request.uri())));
    requestBuilder.method(request.method(), request.body() == null ? HttpRequest.BodyPublishers.noBody() : BodyPublishers.ofByteArray(request.body()));
    requestBuilder.timeout(config.requestTimeout());
    for (final var kv : request.headers()) {
      requestBuilder.setHeader(kv.key(), kv.value());
    }
    return requestBuilder.build();
  }

  private static ArangoDbHttpClient.Response createResponse(final HttpResponse<byte[]> httpResponse) {
    //
    final var headersBuilder = MMultiMap.<String, String>create();
    for (final var kvs : httpResponse.headers().map().entrySet()) {
      for (final var v : kvs.getValue()) {
        headersBuilder.add(kvs.getKey(), v);
      }
    }
    return new Response(httpResponse.statusCode(), headersBuilder.destroy(), httpResponse.uri().toString(), httpResponse.body());
  }

  private final HttpClient httpClient;

  private final ArangoDbClientConfig config;

  private final ExecutorService executorService = Executors.newCachedThreadPool();//TODO to virtual threads

  ArangoDbHttpClient(final ArangoDbClientConfig config) {
    Check.valid(config, "Config is not valid");
    this.config = config;
    this.httpClient = HttpClient.newBuilder()
      .executor(this.executorService)
      .followRedirects(Redirect.ALWAYS)
      .version(Version.HTTP_1_1)
      .connectTimeout(config.connectionTimeout())
      .authenticator(new AuthenticatorImpl(config))
      .build();
  }

  @Override
  public void close() {
    Exec.quietly(this.executorService::shutdown);
  }

  CompletableFuture<ArangoDbHttpClient.Response> executeAsync(final ArangoDbHttpClient.Request request) {
    //
    final var httpRequest = createHttpRequest(request, this.config);
    return this.httpClient
      .sendAsync(httpRequest, responseInfo -> BodySubscribers.ofByteArray())
      .thenApply(ArangoDbHttpClient::createResponse);
  }

}
