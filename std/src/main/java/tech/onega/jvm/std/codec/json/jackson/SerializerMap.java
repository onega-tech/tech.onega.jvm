package tech.onega.jvm.std.codec.json.jackson;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import tech.onega.jvm.std.struct.hash.KV;
import tech.onega.jvm.std.struct.vector.Vector;

final class SerializerMap extends JsonSerializer<Vector<KV<?, ?>>> {

  @Override
  public void serialize(final Vector<KV<?, ?>> value, final JsonGenerator gen, final SerializerProvider serializers)
    throws IOException {
    gen.writeStartObject();
    for (final KV<?, ?> kv : value) {
      if (kv.value() == null) {
        continue;
      }
      gen.writeObjectField(String.valueOf(kv.key()), kv.value());
    }
    gen.writeEndObject();
  }

}
