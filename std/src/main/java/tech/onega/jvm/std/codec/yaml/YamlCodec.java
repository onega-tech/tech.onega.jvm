package tech.onega.jvm.std.codec.yaml;

import java.io.File;
import java.nio.charset.Charset;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import tech.onega.jvm.std.codec.json.jackson.JacksonMapper;
import tech.onega.jvm.std.io.writer.IOWriterBytes;
import tech.onega.jvm.std.struct.bytes.IBytes;

final public class YamlCodec {

  private static final JacksonMapper MAPPER = new JacksonMapper(() -> new ObjectMapper(new YAMLFactory()));

  public static ObjectMapper getMapper() {
    return MAPPER.getMapper();
  }

  public static <T> T parse(final byte[] json, final Class<T> type) throws RuntimeException {
    return MAPPER.parse(json, type);
  }

  public static <T> T parse(final File file, final Class<T> type) throws RuntimeException {
    return MAPPER.parse(file, type);
  }

  public static <T> T parse(final IBytes json, final Class<T> type) throws RuntimeException {
    return MAPPER.parse(json, type);
  }

  public static <T> T parse(final IBytes json, final Class<T> collectionType, final Class<?> valueType)
    throws RuntimeException {
    return MAPPER.parse(json, collectionType, valueType);
  }

  public static <T> T parse(final IBytes json, final Class<T> mapType, final Class<?> keyType, final Class<?> valueType)
    throws RuntimeException {
    return MAPPER.parse(json, mapType, keyType, valueType);
  }

  public static <T> T parse(final String json, final Class<T> type) throws RuntimeException {
    return MAPPER.parse(json, type);
  }

  public static <T> T parse(final String json, final Class<T> type, final Charset charset) throws RuntimeException {
    return MAPPER.parse(json, type, charset);
  }

  public static <T> T parse(final String json, final Class<T> collectionType, final Class<?> valueType)
    throws RuntimeException {
    return MAPPER.parse(json, collectionType, valueType);
  }

  public static <T> T parse(final String json, final Class<T> collectionType, final Class<?> valueType,
    final Charset charset) throws RuntimeException {
    return MAPPER.parse(json, collectionType, valueType, charset);
  }

  public static <T> T parse(final String json, final Class<T> mapType, final Class<?> keyType, final Class<?> valueType)
    throws RuntimeException {
    return MAPPER.parse(json, mapType, keyType, valueType);
  }

  public static <T> T parse(final String json, final Class<T> mapType, final Class<?> keyType, final Class<?> valueType,
    final Charset charset) throws RuntimeException {
    return MAPPER.parse(json, mapType, keyType, valueType, charset);
  }

  public static byte[] toByteArray(final Object value) {
    return MAPPER.toByteArray(value);
  }

  public static byte[] toByteArray(final Object value, final boolean pretty) {
    return MAPPER.toByteArray(value, pretty);
  }

  public static byte[] toByteArray(final Object value, final boolean pretty, final int initialCapacity) {
    return MAPPER.toByteArray(value, pretty, initialCapacity);
  }

  public static IBytes toBytes(final Object value) {
    return MAPPER.toBytes(value);
  }

  public static IBytes toBytes(final Object value, final boolean pretty) {
    return MAPPER.toBytes(value, pretty);
  }

  public static IBytes toBytes(final Object value, final boolean pretty, final int initialCapacity) {
    return MAPPER.toBytes(value, pretty, initialCapacity);
  }

  public static void toFile(final File file, final Object value) {
    MAPPER.toFile(file, value);
  }

  public static void toFile(final File file, final Object value, final boolean pretty) {
    MAPPER.toFile(file, value, pretty);
  }

  public static void toFile(final String file, final Object value) {
    MAPPER.toFile(file, value);
  }

  public static void toFile(final String file, final Object value, final boolean pretty) {
    MAPPER.toFile(file, value, pretty);
  }

  public static String toString(final Object value) {
    return MAPPER.toString(value);
  }

  public static String toString(final Object value, final boolean pretty) {
    return MAPPER.toString(value, pretty);
  }

  public static String toString(final Object value, final boolean pretty, final Charset charset) {
    return MAPPER.toString(value, pretty, charset);
  }

  public static String toString(final Object value, final Charset charset) {
    return MAPPER.toString(value, charset);
  }

  public static void writeTo(final Object value, final boolean pretty, final IOWriterBytes writer) {
    MAPPER.writeTo(value, pretty, writer);
  }

}