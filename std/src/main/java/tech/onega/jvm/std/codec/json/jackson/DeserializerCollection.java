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
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.struct.map.IMap;

final class DeserializerCollection<V> extends JsonDeserializer<V> {

  private final JavaType valueType;

  private final Collector<Object, Object, V> collector;

  @SuppressWarnings("unchecked")
  public DeserializerCollection(final Collector<?, ?, ? extends V> collector, final JavaType type) {
    this.valueType = type.containedTypeCount() < 1 ? null : type.containedType(0);
    this.collector = (Collector<Object, Object, V>) collector;
  }

  @Override
  public V deserialize(final JsonParser parser, final DeserializationContext context)
    throws IOException, JsonProcessingException {
    final JsonDeserializer<?> valueDeserializer = valueType == null ? null
      : context.findContextualValueDeserializer(valueType, null);
    final Object tmp = collector.supplier().get();
    final BiConsumer<Object, Object> accumulator = collector.accumulator();
    JsonToken token = null;
    Object value = null;
    while ((token = parser.nextToken()) != JsonToken.END_ARRAY) {
      if (token == JsonToken.VALUE_NULL) {
        value = null;
      }
      else if (valueDeserializer != null) {
        value = valueDeserializer.deserialize(parser, context);
      }
      else {
        value = parser.readValueAs(Object.class);
        if (value instanceof Collection) {
          value = IList.copy(((Collection<?>) value));
        }
        else if (value instanceof Map) {
          value = IMap.of((Map<?, ?>) value);
        }
      }
      accumulator.accept(tmp, value);
    }
    return collector.finisher().apply(tmp);
  }

}
