package tech.onega.jvm.std.codec.json.jackson;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import tech.onega.jvm.std.io.FileUtils;
import tech.onega.jvm.std.io.writer.IOWriterBytes;
import tech.onega.jvm.std.struct.bytes.IBytes;

final public class JacksonMapper {

  private static class PrettyPrinter extends DefaultPrettyPrinter {

    private static final long serialVersionUID = 1L;

    public PrettyPrinter() {
      super(DEFAULT_ROOT_VALUE_SEPARATOR);
      this._objectIndenter = new DefaultIndenter("  ", "\n");
    }

  }

  private static ObjectMapper createMapper(final Supplier<ObjectMapper> mapperFactory) {
    final var prettyPrinter = new PrettyPrinter()
      .withArrayIndenter(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
    final var mapper = mapperFactory.get();
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    mapper.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, true);
    mapper.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
    mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
    mapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
    mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
    mapper.configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, false);
    mapper.setSerializationInclusion(Include.ALWAYS);
    mapper.setDefaultPropertyInclusion(Include.ALWAYS);
    mapper.registerModule(new JavaTimeModule());
    mapper.registerModule(new Jdk8Module());
    mapper.registerModule(new OnegaStdJacksonModule());
    mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
      .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
      .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
      .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
      .withCreatorVisibility(JsonAutoDetect.Visibility.ANY));
    mapper.setDefaultPrettyPrinter(prettyPrinter);
    return mapper;
  }

  public final ObjectWriter writerPretty;

  public final ObjectWriter writer;

  public final ObjectMapper mapper;

  public JacksonMapper(final Supplier<ObjectMapper> mapperFactory) {
    final var prettyPrinter = new PrettyPrinter()
      .withArrayIndenter(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
    this.mapper = createMapper(mapperFactory);
    this.writerPretty = this.mapper.writer().with(prettyPrinter);
    this.writer = this.mapper.writer();
  }

  @SuppressWarnings("unchecked")
  public <T> T clone(final T value) {
    return value == null ? null : this.mapper.convertValue(value, (Class<T>) value.getClass());
  }

  public <T> T convert(final Object value, final Class<T> targetType) {
    return this.mapper.convertValue(value, targetType);
  }

  public <T> T convert(final Object value, final Class<T> collectionType, final Class<?> valueType) {
    final JavaType javaType = this.mapper.getTypeFactory().constructCollectionLikeType(collectionType, valueType);
    return this.mapper.convertValue(value, javaType);
  }

  public <T> T convert(final Object value, final Class<T> mapType, final Class<?> keyType, final Class<?> valueType) {
    final JavaType javaType = this.mapper.getTypeFactory().constructMapLikeType(mapType, keyType, valueType);
    return this.mapper.convertValue(value, javaType);
  }

  public <T> T convert(final Object value, final TypeReference<T> targetType) {
    return this.mapper.convertValue(value, targetType);
  }

  public ObjectMapper createObjectMapper() {
    return this.mapper.copy();
  }

  public ObjectMapper getMapper() {
    return this.mapper;
  }

  public <T> T parse(final byte[] json, final Class<T> type) throws RuntimeException {
    try {
      return this.mapper.readValue(json, type);
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public <T> T parse(final File file, final Class<T> type) throws RuntimeException {
    return this.parse(FileUtils.loadFile(file), type);
  }

  public <T> T parse(final IBytes json, final Class<T> type) throws RuntimeException {
    try {
      return this.mapper.readValue(json.asReader().asInputStream(), type);
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public <T> T parse(final IBytes json, final Class<T> collectionType, final Class<?> valueType)
    throws RuntimeException {
    try {
      final JavaType javaType = this.mapper.getTypeFactory().constructCollectionLikeType(collectionType, valueType);
      return this.mapper.readValue(json.asReader().asInputStream(), javaType);
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public <T> T parse(final IBytes json, final Class<T> mapType, final Class<?> keyType, final Class<?> valueType)
    throws RuntimeException {
    try {
      final JavaType javaType = this.mapper.getTypeFactory().constructMapLikeType(mapType, keyType, valueType);
      return this.mapper.readValue(json.asReader().asInputStream(), javaType);
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public <T> T parse(final String json, final Class<T> type) throws RuntimeException {
    return this.parse(IBytes.of(json), type);
  }

  public <T> T parse(final String json, final Class<T> type, final Charset charset) throws RuntimeException {
    return this.parse(IBytes.of(json, charset), type);
  }

  public <T> T parse(final String json, final Class<T> collectionType, final Class<?> valueType)
    throws RuntimeException {
    return this.parse(IBytes.of(json), collectionType, valueType);
  }

  public <T> T parse(final String json, final Class<T> collectionType, final Class<?> valueType, final Charset charset)
    throws RuntimeException {
    return this.parse(IBytes.of(json, charset), collectionType, valueType);
  }

  public <T> T parse(final String json, final Class<T> mapType, final Class<?> keyType, final Class<?> valueType)
    throws RuntimeException {
    return this.parse(IBytes.of(json), mapType, keyType, valueType);
  }

  public <T> T parse(final String json, final Class<T> mapType, final Class<?> keyType, final Class<?> valueType,
    final Charset charset) throws RuntimeException {
    return this.parse(IBytes.of(json, charset), mapType, keyType, valueType);
  }

  public byte[] toByteArray(final Object value) {
    return this.toByteArray(value, false);
  }

  public byte[] toByteArray(final Object value, final boolean pretty) {
    return this.toByteArray(value, pretty, 256);
  }

  public byte[] toByteArray(final Object value, final boolean pretty, final int initialCapacity) {
    final IOWriterBytes writer = new IOWriterBytes(initialCapacity);
    this.writeTo(value, pretty, writer);
    return writer.toBytes();
  }

  public IBytes toBytes(final Object value) {
    return this.toBytes(value, false);
  }

  public IBytes toBytes(final Object value, final boolean pretty) {
    return this.toBytes(value, pretty, 256);
  }

  public IBytes toBytes(final Object value, final boolean pretty, final int initialCapacity) {
    final IOWriterBytes writer = new IOWriterBytes(initialCapacity);
    this.writeTo(value, pretty, writer);
    return writer.toIBytes();
  }

  public void toFile(final File file, final Object value) {
    this.toFile(file, value, false);
  }

  public void toFile(final File file, final Object value, final boolean pretty) {
    FileUtils.saveFile(file, this.toBytes(value, pretty));
  }

  public void toFile(final String file, final Object value) {
    this.toFile(new File(file), value, false);
  }

  public void toFile(final String file, final Object value, final boolean pretty) {
    FileUtils.saveFile(new File(file), this.toBytes(value, pretty));
  }

  public String toString(final Object value) {
    return this.toString(value, false);
  }

  public String toString(final Object value, final boolean pretty) {
    return this.toString(value, pretty, StandardCharsets.UTF_8);
  }

  public String toString(final Object value, final boolean pretty, final Charset charset) {
    return this.toBytes(value, pretty).toString(charset);
  }

  public String toString(final Object value, final Charset charset) {
    return this.toString(value, false, charset);
  }

  public void writeTo(final Object value, final boolean pretty, final IOWriterBytes writer) {
    try {
      if (pretty) {
        this.writerPretty.writeValue(writer.asOutputStream(), value);
      }
      else {
        this.writer.writeValue(writer.asOutputStream(), value);
      }
    }
    catch (final RuntimeException e) {
      throw e;
    }
    catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

}
