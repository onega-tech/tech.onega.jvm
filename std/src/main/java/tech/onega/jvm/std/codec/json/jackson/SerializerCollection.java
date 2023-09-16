package tech.onega.jvm.std.codec.json.jackson;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

final class SerializerCollection extends JsonSerializer<Iterable<?>> {

  @Override
  public void serialize(final Iterable<?> value, final JsonGenerator gen, final SerializerProvider provider)
    throws IOException {
    gen.writeStartArray();
    for (final Object v : value) {
      gen.writeObject(v);
    }
    gen.writeEndArray();
  }

}
