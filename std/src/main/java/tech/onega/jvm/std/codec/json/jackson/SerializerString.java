package tech.onega.jvm.std.codec.json.jackson;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

final public class SerializerString extends StdSerializer<Object> {

  private static final long serialVersionUID = 1L;

  public SerializerString() {
    this(null);
  }

  public SerializerString(final Class<Object> type) {
    super(type);
  }

  @Override
  public void serialize(final Object value, final JsonGenerator gen, final SerializerProvider provider)
    throws IOException {
    gen.writeString(String.valueOf(value));
  }

}