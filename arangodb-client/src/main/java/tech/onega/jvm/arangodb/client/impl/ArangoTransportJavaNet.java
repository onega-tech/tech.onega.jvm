package tech.onega.jvm.arangodb.client.impl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodySubscribers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.arangodb.jackson.dataformat.velocypack.VPackFactory;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import tech.onega.jvm.arangodb.client.domain.ArangoDbTransport;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.codec.base.Base64Codec;
import tech.onega.jvm.std.codec.json.jackson.OnegaStdJacksonModule;
import tech.onega.jvm.std.lang.Exec;
import tech.onega.jvm.std.lang.Lambda.Supplier;
import tech.onega.jvm.std.log.Logger;
import tech.onega.jvm.std.log.Loggers;
import tech.onega.jvm.std.struct.hash.KV;
import tech.onega.jvm.std.struct.map.IMap;
import tech.onega.jvm.std.struct.map.IMultiMap;
import tech.onega.jvm.std.struct.map.MMultiMap;
import tech.onega.jvm.std.validate.Check;

@ThreadSafe
public class ArangoTransportJavaNet implements ArangoDbTransport {

  public record Config(
    @NotBlank String host,
    @Min(1) int port,
    @NotBlank String user,
    @NotBlank String password,
    @NotNull Duration requestTimeout,
    boolean velocityPack,
    boolean https,
    boolean followRedirects,
    boolean http2,
    @NotNull Supplier<ExecutorService, RuntimeException> executorServiceFactory) {
  }

  private final static Logger LOGGER = Loggers.find(ArangoTransportJavaNet.class);

  private static HttpClient createHttpClient(final Config config, final ExecutorService executorService) {
    return HttpClient.newBuilder()
      .executor(executorService)
      .followRedirects(config.followRedirects() ? Redirect.ALWAYS : Redirect.NEVER)
      .version(config.http2() ? Version.HTTP_2 : Version.HTTP_1_1)
      .build();
  }

  private static IMultiMap<String, String> createHttpHeaders(final HttpHeaders httpHeaders) {
    //
    final var headersBuilder = MMultiMap.<String, String>create();
    for (final var kvs : httpHeaders.map().entrySet()) {
      for (final var v : kvs.getValue()) {
        headersBuilder.add(kvs.getKey(), v);
      }
    }
    return headersBuilder.destroy();
  }

  private static String createHttpServerURI(final Config config) {
    return "%s%s:%s".formatted(
      (config.https() ? "https://" : "http://"),
      config.host(),
      config.port());
  }

  private static ObjectMapper createObjectMapper(final Config config) {
    final var objectMapper = config.velocityPack() ? new ObjectMapper(new VPackFactory()) : new ObjectMapper();
    objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    objectMapper.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, true);
    objectMapper.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, false);
    objectMapper.setSerializationInclusion(Include.ALWAYS);
    objectMapper.setDefaultPropertyInclusion(Include.ALWAYS);
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.registerModule(new Jdk8Module());
    objectMapper.registerModule(new OnegaStdJacksonModule());
    objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
      .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
      .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
      .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
      .withCreatorVisibility(JsonAutoDetect.Visibility.ANY));
    objectMapper.setDefaultPrettyPrinter(new DefaultPrettyPrinter()
      .withArrayIndenter(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE));
    return objectMapper;
  }

  private static IMap<String, String> createRequestHttpHeaders(final Config config) {
    //
    final var authToken = Base64Codec.encodeAsString("%s:%s".formatted(config.user(), config.password()).getBytes(StandardCharsets.UTF_8), true);
    return IMap.of(
      KV.of("authorization", "Basic " + authToken),
      KV.of("accept", config.velocityPack() ? "application/x-velocypack" : "application/json; charset=utf-8"));
  }

  private final Config config;

  private final ExecutorService executorService;

  private final HttpClient httpClient;

  private final ObjectMapper objectMapper;

  private final String httpServerURI;

  private final IMap<String, String> requestHttpHeaders;

  public ArangoTransportJavaNet(final Config config) {
    Check.valid(config, "Config is not valid");
    this.config = config;
    this.executorService = config.executorServiceFactory.invoke();
    this.httpClient = createHttpClient(config, this.executorService);
    this.objectMapper = createObjectMapper(config);
    this.httpServerURI = createHttpServerURI(config);
    this.requestHttpHeaders = createRequestHttpHeaders(config);
  }

  @Override
  public void close() {
    Exec.quietly(this.executorService::shutdown);
  }

  private HttpRequest createHttpRequest(final ArangoDbTransport.Request<?> request) {
    try {
      final byte[] requestBody = request.body() == null ? null : this.objectMapper.writeValueAsBytes(request.body());
      final var requestBuilder = HttpRequest.newBuilder();
      requestBuilder.uri(URI.create(this.httpServerURI + request.endpoint()));
      requestBuilder.method(request.method(), requestBody == null ? HttpRequest.BodyPublishers.noBody() : BodyPublishers.ofByteArray(requestBody));
      requestBuilder.timeout(request.timeout() == null ? this.config.requestTimeout() : request.timeout());
      this.requestHttpHeaders.forEach(kv -> requestBuilder.setHeader(kv.key(), kv.value()));
      return requestBuilder.build();
    }
    catch (final Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  private <R> ArangoDbTransport.Response<R> createResponse(final ArangoDbTransport.Request<R> request, final HttpRequest httpRequest, final HttpResponse<byte[]> httpResponse) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("\n  HttpRequest: {}, \n  HtttResponse: {}", httpRequest, httpResponse);
    }
    //TODO так что у нас какие идеи
    //TODO ответ аранго или нет
    //TODO если ответ аранго надо понять ошибка или нет- там может быть только статус код
    //TODO а ещё нам важен content-код
    //TODO - пока только позитивный кейс, тоесть только код
    try {
      final R body = httpResponse.body() == null ? null : this.objectMapper.readValue(httpResponse.body(), request.responseType());
      return new ArangoDbTransport.Response<>(request, httpResponse.statusCode(), createHttpHeaders(httpRequest.headers()), body);
    }
    catch (final Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @Override
  public <R> CompletableFuture<Response<R>> execute(final ArangoDbTransport.Request<R> request) {
    final var httpRequest = this.createHttpRequest(request);
    return this.httpClient
      .sendAsync(httpRequest, responseInfo -> BodySubscribers.ofByteArray())
      .thenApply(httpResponse -> this.createResponse(request, httpRequest, httpResponse));
  }

  public Config getConfig() {
    return this.config;
  }

}
