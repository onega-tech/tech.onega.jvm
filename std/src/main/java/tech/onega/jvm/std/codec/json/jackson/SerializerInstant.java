package tech.onega.jvm.std.codec.json.jackson;

import java.io.IOException;
import java.time.Instant;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

final class SerializerInstant extends JsonSerializer<Instant> {

  @Override
  public void serialize(final Instant instant, final JsonGenerator gen, final SerializerProvider provider)
    throws IOException {
    gen.writeNumber(instant.toEpochMilli());
  }

}
