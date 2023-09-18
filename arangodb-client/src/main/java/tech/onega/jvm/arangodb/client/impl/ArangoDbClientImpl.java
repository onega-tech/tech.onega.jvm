package tech.onega.jvm.arangodb.client.impl;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import tech.onega.jvm.arangodb.client.domain.ArangoDbClient;
import tech.onega.jvm.arangodb.client.domain.ArangoDbHttpTransport;
import tech.onega.jvm.std.annotation.Nullable;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.codec.base.Base64Codec;
import tech.onega.jvm.std.codec.json.jackson.OnegaStdJacksonModule;
import tech.onega.jvm.std.lang.Exec;
import tech.onega.jvm.std.struct.hash.KV;
import tech.onega.jvm.std.struct.map.IMultiMap;
import tech.onega.jvm.std.validate.Check;

/**
 * @see https://docs.arangodb.com/3.11/develop/http/general-request-handling/
 * @see https://github.com/arangodb/arangodb-java-driver/tree/main/http
 */
@ThreadSafe
public class ArangoDbClientImpl implements ArangoDbClient {
  //private static final String CONTENT_TYPE_APPLICATION_JSON_UTF8 = "application/json; charset=utf-8";
  //private static final String CONTENT_TYPE_VPACK = "application/x-velocypack";

  public record Config(
    @NotBlank String host,
    @Min(1) int port,
    @NotBlank String user,
    @NotBlank String password,
    @NotNull Duration requestTimeout,
    boolean https,
    boolean http2) {
  }

  public static ArangoDbClient createDefaultClient(final Config config) {
    Check.valid(config, "Config is not valid");
    final var transportConfig = new ArangoHttpTransportJavaNet.Config(
      true,
      config.http2(),
      Executors::newCachedThreadPool);
    final var httpTransport = new ArangoHttpTransportJavaNet(transportConfig);
    final var objectMapper = createDefaultObjectMapper();
    return new ArangoDbClientImpl(config, httpTransport, objectMapper);
  }

  public static ObjectMapper createDefaultObjectMapper() {
    final var objectMapper = new ObjectMapper();
    objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    objectMapper.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, true);
    objectMapper.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, false);
    objectMapper.setSerializationInclusion(Include.NON_EMPTY);
    objectMapper.setDefaultPropertyInclusion(Include.NON_EMPTY);
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.registerModule(new OnegaStdJacksonModule());
    objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
      .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
      .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
      .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
      .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
    objectMapper.setDefaultPrettyPrinter(new DefaultPrettyPrinter()
      .withArrayIndenter(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE));
    return objectMapper;
  }

  private static IMultiMap<String, String> createHttpHeaders(final Config config) {
    final var authToken = Base64Codec.encodeAsString("%s:%s".formatted(config.user(), config.password()).getBytes(StandardCharsets.UTF_8), true);
    return IMultiMap.<String, String>of(
      KV.of("accept", "application/json"),
      //KV.of("accept", "application/x-velocypack"),
      KV.of("authorization", "Basic " + authToken));
  }

  private static String createHttpServerURI(final Config config) {
    return "%s%s:%s".formatted(
      (config.https() ? "https://" : "http://"),
      config.host(),
      config.port());
  }

  private final Config config;

  private final ArangoDbHttpTransport httpTransport;

  private final IMultiMap<String, String> httpHeaders;

  private final String httpServerURI;

  private final ObjectMapper objectMapper;

  public ArangoDbClientImpl(final Config config, final ArangoDbHttpTransport httpTransport, final ObjectMapper objectMapper) {
    this.config = config;
    this.httpTransport = httpTransport;
    this.httpHeaders = createHttpHeaders(config);
    this.httpServerURI = createHttpServerURI(config);
    this.objectMapper = objectMapper;
  }

  @Override
  public void close() {
    Exec.quietly(this.httpTransport::close);
  }

  private ArangoDbHttpTransport.Request createHttpTransportRequest(final String method, final String endpoint, @Nullable final byte[] body) {
    //
    return new ArangoDbHttpTransport.Request(
      method,
      this.httpServerURI + endpoint,
      this.httpHeaders,
      this.config.requestTimeout(),
      body);
  }

  @Override
  public CompletableFuture<ArangoDbHttpTransport.Response> listAllDatabasesAsync() {
    final var request = this.createHttpTransportRequest("GET", "/_api/database", null);
    return this.httpTransport.execute(request);
  }

}
