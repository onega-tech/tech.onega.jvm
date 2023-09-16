package tech.onega.jvm.std.codec.json.jackson;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.time.Instant;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import tech.onega.jvm.std.reflection.ReflectionUtils;
import tech.onega.jvm.std.struct.date.DateTime;
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.struct.list.MList;
import tech.onega.jvm.std.struct.map.IMap;
import tech.onega.jvm.std.struct.map.IMultiMap;
import tech.onega.jvm.std.struct.map.MMap;
import tech.onega.jvm.std.struct.map.MMultiMap;
import tech.onega.jvm.std.struct.set.ISet;
import tech.onega.jvm.std.struct.set.MSet;

final class Serializers extends com.fasterxml.jackson.databind.ser.Serializers.Base {

  private static final JsonSerializer<?> SERIALIZER_COLLECTION = new SerializerCollection();

  private static final JsonSerializer<?> SERIALIZER_MAP = new SerializerMap();

  private static final JsonSerializer<?> SERIALIZER_IMULTI_MAP = new SerializerIMultiMap();

  private static final JsonSerializer<?> SERIALIZER_MMULTI_MAP = new SerializerMMultiMap();

  private static final JsonSerializer<?> SERIALIZER_DATE_TIME = new SerializerDateTime();

  private static final SerializerString SERIALIZER_STRING = new SerializerString();

  private static final JsonSerializer<?> SERIALIZER_INSTANT = new SerializerInstant();

  @Override
  public JsonSerializer<?> findSerializer(
    final SerializationConfig config,
    final JavaType type,
    final BeanDescription beanDesc) {
    final Class<?> raw = type.getRawClass();
    if (ReflectionUtils.isExtendedFrom(raw, IList.class)) {
      return SERIALIZER_COLLECTION;
    }
    else if (ReflectionUtils.isExtendedFrom(raw, MList.class)) {
      return SERIALIZER_COLLECTION;
    }
    else if (ReflectionUtils.isExtendedFrom(raw, ISet.class)) {
      return SERIALIZER_COLLECTION;
    }
    else if (ReflectionUtils.isExtendedFrom(raw, MSet.class)) {
      return SERIALIZER_COLLECTION;
    }
    else if (ReflectionUtils.isExtendedFrom(raw, IMap.class)) {
      return SERIALIZER_MAP;
    }
    else if (ReflectionUtils.isExtendedFrom(raw, MMap.class)) {
      return SERIALIZER_MAP;
    }
    else if (ReflectionUtils.isExtendedFrom(raw, IMultiMap.class)) {
      return SERIALIZER_IMULTI_MAP;
    }
    else if (ReflectionUtils.isExtendedFrom(raw, MMultiMap.class)) {
      return SERIALIZER_MMULTI_MAP;
    }
    else if (ReflectionUtils.isExtendedFrom(raw, DateTime.class)) {
      return SERIALIZER_DATE_TIME;
    }
    else if (ReflectionUtils.isExtendedFrom(raw, Instant.class)) {
      return SERIALIZER_INSTANT;
    }
    else if (ReflectionUtils.isExtendedFrom(raw, AnnotatedElement.class)) {
      return SERIALIZER_STRING;
    }
    else if (ReflectionUtils.isExtendedFrom(raw, Annotation.class)) {
      return SERIALIZER_STRING;
    }
    return null;
  }

}
