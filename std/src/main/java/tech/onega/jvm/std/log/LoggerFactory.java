package tech.onega.jvm.std.log;

import tech.onega.jvm.std.annotation.Nullable;
import tech.onega.jvm.std.struct.map.IMap;

public interface LoggerFactory extends AutoCloseable {

  @Override
  void close();

  void configure(IMap<String, String> properties);

  void configureYaml(String yaml);

  @Nullable
  LoggerContext createContext(String name);

  Logger createLogger(Class<?> type);

  Logger createLogger(String name);

  long getVersion();

}
