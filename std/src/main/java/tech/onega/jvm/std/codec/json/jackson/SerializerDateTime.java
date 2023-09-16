package tech.onega.jvm.std.codec.json.jackson;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import tech.onega.jvm.std.struct.date.DateTime;

final class SerializerDateTime extends JsonSerializer<DateTime> {

  @Override
  public void serialize(final DateTime date, final JsonGenerator gen, final SerializerProvider provider)
    throws IOException {
    gen.writeNumber(date.toUTCTimestampMilli());
  }

}
