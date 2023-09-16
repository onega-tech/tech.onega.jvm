package tech.onega.jvm.std.lang;

import java.util.Comparator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import tech.onega.jvm.std.codec.json.jackson.JacksonMapper;

final public class ObjectUtils {

  private static final JacksonMapper MAPPER = new JacksonMapper(() -> new ObjectMapper());

  public static <T> T clone(final T value) {
    return MAPPER.clone(value);
  }

  public static <T> T convert(final Object value, final Class<T> targetType) {
    return MAPPER.convert(value, targetType);
  }

  public static <T> T convert(final Object value, final Class<T> collectionType, final Class<?> valueType) {
    return MAPPER.convert(value, collectionType, valueType);
  }

  public static <T> T convert(final Object value, final Class<T> mapType, final Class<?> keyType,
    final Class<?> valueType) {
    return MAPPER.convert(value, mapType, keyType, valueType);
  }

  public static <T> T convert(final Object value, final TypeReference<T> targetType) {
    return MAPPER.convert(value, targetType);
  }

  public static <T> T max(final Comparator<T> comparator, final T a, final T b) {
    return comparator.compare(a, b) >= 0 ? a : b;
  }

  public static <T> T min(final Comparator<T> comparator, final T a, final T b) {
    return comparator.compare(a, b) >= 0 ? b : a;
  }

}
