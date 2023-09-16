package tech.onega.jvm.std.codec.json.jackson;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collector;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import tech.onega.jvm.std.struct.hash.KV;
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.struct.map.IMap;

final class DeserializerMultiMap<V> extends JsonDeserializer<V> {

  private final JavaType keyType;

  private final JavaType valueType;

  private final Collector<Object, Object, V> collector;

  @SuppressWarnings("unchecked")
  public DeserializerMultiMap(final Collector<?, ?, ? extends V> collector, final JavaType type) {
    this.keyType = type.containedTypeCount() < 1 ? null : type.containedType(0);
    this.valueType = type.containedTypeCount() < 2 ? null : type.containedType(1);
    this.collector = (Collector<Object, Object, V>) collector;
  }

  @Override
  public V deserialize(final JsonParser parser, final DeserializationContext context)
    throws IOException, JsonProcessingException {
    final CollectionLikeType collectionLikeType = valueType == null
      ? null
      : CollectionLikeType.construct(
        IList.class,
        TypeBindings.create(IList.class, valueType),
        TypeFactory.unknownType(),
        null,
        valueType);
    final JsonDeserializer<?> valueDeserializer = valueType == null ? null
      : context.findContextualValueDeserializer(collectionLikeType, null);
    final KeyDeserializer keyDeserializer = keyType == null ? null : context.findKeyDeserializer(keyType, null);
    final Object tmp = collector.supplier().get();
    final BiConsumer<Object, Object> accumulator = collector.accumulator();
    JsonToken token = null;
    Object key = null;
    while ((token = parser.nextToken()) != JsonToken.END_OBJECT) {
      if (token == JsonToken.FIELD_NAME) {
        if (keyDeserializer != null) {
          key = keyDeserializer.deserializeKey(parser.getCurrentName(), context);
        }
        else {
          key = parser.getCurrentName();
        }
      }
      token = parser.nextToken();
      if (token == JsonToken.VALUE_NULL) {
        accumulator.accept(tmp, KV.of(key, IList.empty()));
      }
      else if (valueDeserializer == null) {
        for (Object value : (Collection<?>) parser.readValueAs(Object.class)) {
          if (value instanceof Collection) {
            value = IList.copy(((Collection<?>) value));
          }
          else if (value instanceof Map) {
            value = IMap.of((Map<?, ?>) value);
          }
          accumulator.accept(tmp, KV.of(key, value));
        }
      }
      else {
        for (final Object value : (Iterable<?>) valueDeserializer.deserialize(parser, context)) {
          accumulator.accept(tmp, KV.of(key, value));
        }
      }
    }
    return collector.finisher().apply(tmp);
  }

}
