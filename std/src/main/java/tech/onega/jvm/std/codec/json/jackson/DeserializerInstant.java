package tech.onega.jvm.std.codec.json.jackson;

import java.io.IOException;
import java.time.Instant;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

final class DeserializerInstant extends JsonDeserializer<Instant> {

  @Override
  public Instant deserialize(final JsonParser parser, final DeserializationContext context)
    throws IOException, JsonProcessingException {
    final long epochMilli = parser.getValueAsLong();
    return Instant.ofEpochMilli(epochMilli);
  }

}
