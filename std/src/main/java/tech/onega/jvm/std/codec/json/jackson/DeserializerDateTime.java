package tech.onega.jvm.std.codec.json.jackson;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import tech.onega.jvm.std.struct.date.DateTime;

final class DeserializerDateTime extends JsonDeserializer<DateTime> {

  @Override
  public DateTime deserialize(final JsonParser parser, final DeserializationContext context)
    throws IOException, JsonProcessingException {
    final long epochMillis = parser.getValueAsLong();
    return DateTime.ofTimestampMillis(epochMillis);
  }

}
