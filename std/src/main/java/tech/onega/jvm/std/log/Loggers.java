package tech.onega.jvm.std.log;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.codec.properties.PropertiesCodec;
import tech.onega.jvm.std.io.FileUtils;
import tech.onega.jvm.std.io.IOUtils;
import tech.onega.jvm.std.log.impl.LoggerFactoryImpl;
import tech.onega.jvm.std.struct.map.IMap;

@ThreadSafe
final public class Loggers {

  private static final AtomicReference<LoggerFactory> LOGGER_FACTORY_REF = new AtomicReference<>(
    new LoggerFactoryImpl(Loggers.class.getClassLoader()));

  public static void configure(final File configFile) {
    final var fileName = configFile.getName();
    if (fileName.endsWith(".yaml") || fileName.endsWith(".yml")) {
      final var yaml = FileUtils.loadFile(configFile).toString(StandardCharsets.UTF_8);
      getFactory().configureYaml(yaml);
    }
    else {
      getFactory().configure(PropertiesCodec.encode(configFile));
    }
  }

  public static void configure(final IMap<String, String> properties) {
    getFactory().configure(properties);
  }

  public static void configure(final String configFile) {
    configure(new File(configFile));
  }

  public static void configureFromResource(String resourceName) {
    var classLoader = Loggers.class.getClassLoader();
    final var bytes = IOUtils.inputStreamToIBytes(classLoader.getResourceAsStream(resourceName));
    var str = bytes.toString(StandardCharsets.UTF_8);
    var factory = getFactory();
    if (resourceName.endsWith(".yaml") || resourceName.endsWith(".yml")) {
      factory.configureYaml(str);
    }
    else {
      var props = PropertiesCodec.encode(str);
      factory.configure(props);
    }
  }

  public static Logger find(final Class<?> type) {
    return LOGGER_FACTORY_REF.get().createLogger(type);
  }

  public static Logger find(final String name) {
    return LOGGER_FACTORY_REF.get().createLogger(name);
  }

  public static LoggerFactory getFactory() {
    return LOGGER_FACTORY_REF.get();
  }

  public static LoggerFactory replaceFactory(final LoggerFactory newFactory) {
    return LOGGER_FACTORY_REF.getAndSet(newFactory);
  }

}
