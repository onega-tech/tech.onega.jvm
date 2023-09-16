package tech.onega.jvm.std.codec.json.jackson;

import java.time.Instant;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.MapLikeType;
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

final class Deserializers extends com.fasterxml.jackson.databind.deser.Deserializers.Base {

  private static final int DEFAULT_CAPACITY = 32;

  private final static JsonDeserializer<DateTime> DATE_TIME_DESERIALIZER = new DeserializerDateTime();

  private final static JsonDeserializer<Instant> INSTANT_DESERIALIZER = new DeserializerInstant();

  @Override
  public JsonDeserializer<?> findBeanDeserializer(
    final JavaType type,
    final DeserializationConfig config,
    final BeanDescription beanDesc) throws JsonMappingException {
    final Class<?> raw = type.getRawClass();
    if (ReflectionUtils.isExtendedFrom(raw, IList.class)) {
      return new DeserializerCollection<IList<?>>(IList.collector(DEFAULT_CAPACITY), type);
    }
    else if (ReflectionUtils.isExtendedFrom(raw, MList.class)) {
      return new DeserializerCollection<MList<?>>(MList.collector(DEFAULT_CAPACITY), type);
    }
    else if (ReflectionUtils.isExtendedFrom(raw, ISet.class)) {
      return new DeserializerCollection<ISet<?>>(ISet.collector(DEFAULT_CAPACITY), type);
    }
    else if (ReflectionUtils.isExtendedFrom(raw, MSet.class)) {
      return new DeserializerCollection<MSet<?>>(MSet.collector(DEFAULT_CAPACITY), type);
    }
    else if (ReflectionUtils.isExtendedFrom(raw, IMap.class)) {
      return new DeserializerMap<IMap<?, ?>>(IMap.collector(DEFAULT_CAPACITY), type);
    }
    else if (ReflectionUtils.isExtendedFrom(raw, MMap.class)) {
      return new DeserializerMap<MMap<?, ?>>(MMap.collector(DEFAULT_CAPACITY), type);
    }
    else if (ReflectionUtils.isExtendedFrom(raw, IMultiMap.class)) {
      return new DeserializerMultiMap<IMultiMap<?, ?>>(IMultiMap.collector(DEFAULT_CAPACITY), type);
    }
    else if (ReflectionUtils.isExtendedFrom(raw, MMultiMap.class)) {
      return new DeserializerMultiMap<MMultiMap<?, ?>>(MMultiMap.collector(DEFAULT_CAPACITY), type);
    }
    else if (ReflectionUtils.isExtendedFrom(raw, DateTime.class)) {
      return DATE_TIME_DESERIALIZER;
    }
    else if (ReflectionUtils.isExtendedFrom(raw, Instant.class)) {
      return INSTANT_DESERIALIZER;
    }
    return null;
  }

  @Override
  public JsonDeserializer<?> findCollectionLikeDeserializer(
    final CollectionLikeType type,
    final DeserializationConfig config,
    final BeanDescription beanDesc,
    final TypeDeserializer elementTypeDeserializer,
    final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
    final Class<?> raw = type.getRawClass();
    if (ReflectionUtils.isExtendedFrom(raw, IList.class)) {
      return new DeserializerCollection<IList<?>>(IList.collector(DEFAULT_CAPACITY), type);
    }
    else if (ReflectionUtils.isExtendedFrom(raw, MList.class)) {
      return new DeserializerCollection<MList<?>>(MList.collector(DEFAULT_CAPACITY), type);
    }
    else if (ReflectionUtils.isExtendedFrom(raw, ISet.class)) {
      return new DeserializerCollection<ISet<?>>(ISet.collector(DEFAULT_CAPACITY), type);
    }
    else if (ReflectionUtils.isExtendedFrom(raw, MSet.class)) {
      return new DeserializerCollection<MSet<?>>(MSet.collector(DEFAULT_CAPACITY), type);
    }
    return null;
  }

  @Override
  public JsonDeserializer<?> findMapLikeDeserializer(
    final MapLikeType type,
    final DeserializationConfig config,
    final BeanDescription beanDesc,
    final KeyDeserializer keyDeserializer,
    final TypeDeserializer elementTypeDeserializer,
    final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
    final Class<?> raw = type.getRawClass();
    if (ReflectionUtils.isExtendedFrom(raw, IMap.class)) {
      return new DeserializerMap<IMap<?, ?>>(IMap.collector(DEFAULT_CAPACITY), type);
    }
    else if (ReflectionUtils.isExtendedFrom(raw, MMap.class)) {
      return new DeserializerMap<MMap<?, ?>>(MMap.collector(DEFAULT_CAPACITY), type);
    }
    else if (ReflectionUtils.isExtendedFrom(raw, IMultiMap.class)) {
      return new DeserializerMultiMap<IMultiMap<?, ?>>(IMultiMap.collector(DEFAULT_CAPACITY), type);
    }
    else if (ReflectionUtils.isExtendedFrom(raw, MMultiMap.class)) {
      return new DeserializerMultiMap<MMultiMap<?, ?>>(MMultiMap.collector(DEFAULT_CAPACITY), type);
    }
    return null;
  }

}
