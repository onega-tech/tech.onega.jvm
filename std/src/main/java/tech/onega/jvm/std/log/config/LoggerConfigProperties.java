package tech.onega.jvm.std.log.config;

import java.util.stream.Stream;
import tech.onega.jvm.std.log.impl.LoggerContextFactory;
import tech.onega.jvm.std.struct.hash.KV;
import tech.onega.jvm.std.struct.map.IMap;
import tech.onega.jvm.std.struct.map.IMultiMap;

final public class LoggerConfigProperties {

  private static IMap<String, IMap<String, String>> createAppendersOptions(final IMap<String, String> properties) {
    return properties.stream()
      .filter(kv -> kv.key().startsWith("appender."))
      .map(kv -> {
        final var names = kv.key().split("\\.");
        final var appenderName = names[1];
        final var optionsName = names[2];
        return KV.of(appenderName, KV.of(optionsName, kv.value()));
      })
      .collect(IMultiMap.collector(properties.size()))
      .keyMultiValues()
      .stream()
      .map(kv -> KV.of(kv.key(), kv.value().stream().collect(IMap.collector(kv.value().size()))))
      .collect(IMap.collector(properties.size()));
  }

  public static LoggerContextFactory createContextFactory(final IMap<String, String> properties,
    final ClassLoader classLoader) {
    final var appendersOptions = createAppendersOptions(properties);
    final var appenders = LoggerConfigUtils.createAppenders(appendersOptions, classLoader);
    final var loggersOptions = createLoggersOptions(properties);
    final var loggersLevelAppenders = LoggerConfigUtils.createLoggersLevelAppenders(loggersOptions, appenders);
    return new LoggerContextFactory(appenders.values(), loggersLevelAppenders);
  }

  private static IMap<String, IMultiMap<String, String>> createLoggersOptions(final IMap<String, String> properties) {
    return properties.stream()
      .filter(kv -> kv.key().startsWith("logger"))
      .sorted((kvA, kvB) -> kvA.key().compareTo(kvB.key()))
      .flatMap(kv -> {
        final var loggerName = kv.key().equals("logger") ? "" : kv.key().substring("logger".length() + 1);
        final var levelsToAppender = LoggerConfigUtils.parseLoggerConfig(kv.value());
        return Stream.of(KV.of(loggerName, levelsToAppender));
      })
      .collect(IMap.collector(properties.size()));
  }

}
