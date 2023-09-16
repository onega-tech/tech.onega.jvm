package tech.onega.jvm.std.codec.json.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;

final public class OnegaStdJacksonModule extends Module {

  private final static String MODULE_NAME = "OnegaStdModule";

  private final static Version VERSION = new Version(1, 0, 0, null, "jxx", "std");

  private final static com.fasterxml.jackson.databind.ser.Serializers SERIALIZERS = new Serializers();

  private final static com.fasterxml.jackson.databind.deser.Deserializers DESERIALIZERS = new Deserializers();

  @Override
  public String getModuleName() {
    return MODULE_NAME;
  }

  @Override
  public void setupModule(final SetupContext context) {
    context.addDeserializers(DESERIALIZERS);
    context.addSerializers(SERIALIZERS);
  }

  @Override
  public Version version() {
    return VERSION;
  }

}
