package tech.onega.jvm.arangodb.client.impl;

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
import javax.validation.constraints.NotNull;
import tech.onega.jvm.arangodb.client.domain.ArangoDbHttpTransport;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.lang.Exec;
import tech.onega.jvm.std.lang.Lambda.Supplier;
import tech.onega.jvm.std.struct.map.MMultiMap;
import tech.onega.jvm.std.validate.Check;

@ThreadSafe
public class ArangoHttpTransportJavaNet implements ArangoDbHttpTransport {

  public record Config(
    boolean followRedirects,
    boolean http2,
    @NotNull Supplier<ExecutorService, RuntimeException> executorServiceFactory) {
  }

  private static HttpClient createHttpClient(final Config config, final ExecutorService executorService) {
    return HttpClient.newBuilder()
      .executor(executorService)
      .followRedirects(config.followRedirects() ? Redirect.ALWAYS : Redirect.NEVER)
      .version(config.http2() ? Version.HTTP_2 : Version.HTTP_1_1)
      .build();
  }

  private static HttpRequest createHttpRequest(final ArangoDbHttpTransport.Request transportRequest) {
    Check.valid(transportRequest, "Request is not valid");
    final var requestBuilder = HttpRequest.newBuilder();
    requestBuilder.uri(URI.create(transportRequest.uri()));
    requestBuilder.method(transportRequest.method(), transportRequest.body() == null ? HttpRequest.BodyPublishers.noBody() : BodyPublishers.ofByteArray(transportRequest.body()));
    requestBuilder.timeout(transportRequest.timeout());
    for (final var kv : transportRequest.headers()) {
      requestBuilder.setHeader(kv.key(), kv.value());
    }
    return requestBuilder.build();
  }

  private static ArangoDbHttpTransport.Response createTransportResponse(final HttpResponse<byte[]> httpResponse) {
    //
    final var headersBuilder = MMultiMap.<String, String>create();
    for (final var kvs : httpResponse.headers().map().entrySet()) {
      for (final var v : kvs.getValue()) {
        headersBuilder.add(kvs.getKey(), v);
      }
    }
    return new Response(httpResponse.statusCode(), headersBuilder.destroy(), httpResponse.uri().toString(), httpResponse.body());
  }

  private final Config config;

  private final ExecutorService executorService;

  private final HttpClient httpClient;

  public ArangoHttpTransportJavaNet(final Config config) {
    Check.valid(config, "Config is not valid");
    this.config = config;
    this.executorService = config.executorServiceFactory.invoke();
    this.httpClient = createHttpClient(config, this.executorService);
  }

  @Override
  public void close() {
    Exec.quietly(this.executorService::shutdown);
  }

  @Override
  public CompletableFuture<ArangoDbHttpTransport.Response> execute(final ArangoDbHttpTransport.Request transportRequest) {
    final var httpRequest = createHttpRequest(transportRequest);
    return this.httpClient
      .sendAsync(httpRequest, responseInfo -> BodySubscribers.ofByteArray())
      .thenApply(ArangoHttpTransportJavaNet::createTransportResponse);
  }

  public Config getConfig() {
    return this.config;
  }

}
