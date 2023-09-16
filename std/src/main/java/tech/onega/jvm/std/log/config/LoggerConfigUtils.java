package tech.onega.jvm.std.log.config;

import tech.onega.jvm.std.log.LogAppender;
import tech.onega.jvm.std.log.LogLevel;
import tech.onega.jvm.std.struct.hash.KV;
import tech.onega.jvm.std.struct.map.IMap;
import tech.onega.jvm.std.struct.map.IMultiMap;
import tech.onega.jvm.std.struct.stream.StreamUtils;

final class LoggerConfigUtils {

  static IMap<String, LogAppender> createAppenders(final IMap<String, IMap<String, String>> appenderOptions,
    final ClassLoader classLoader) {
    return appenderOptions.stream()
      .filter(kv -> kv.value().get("enabled", "true").equalsIgnoreCase("true"))
      .map(kv -> {
        final String appenderClassName = kv.value().get("class");
        if (appenderClassName == null) {
          throw new RuntimeException(String.format("Appender class name is empty. Appender name: %s", kv.key()));
        }
        try {
          final var appenderClass = classLoader.loadClass(appenderClassName);
          final var method = appenderClass.getDeclaredMethod("create", String.class, IMap.class);
          final var appender = (LogAppender) method.invoke(null, kv.key(), kv.value());
          return KV.of(kv.key(), appender);
        }
        catch (final Exception e) {
          throw new RuntimeException(
            String.format("Can't create appender %s. Class name: %s", kv.key(), appenderClassName), e);
        }
      })
      .collect(IMap.collector(appenderOptions.size()));
  }

  static IMap<String, IMultiMap<LogLevel, LogAppender>> createLoggersLevelAppenders(
    final IMap<String, IMultiMap<String, String>> loggersOptions,
    final IMap<String, LogAppender> appenders) {
    return loggersOptions
      .stream()
      .map(loggerNameAndOptions -> {
        final var loggerName = loggerNameAndOptions.key();
        final var loggerOptions = loggerNameAndOptions.value();
        final var levelToAppenders = loggerOptions
          .stream()
          .map(loggerOption -> {
            final LogLevel level = LogLevel.valueOf(loggerOption.key());
            final LogAppender appender = appenders.get(loggerOption.value());
            return KV.of(level, appender);
          })
          .filter(kv -> kv.key() != null)
          .collect(IMultiMap.collector(loggersOptions.size() * 3));
        return KV.of(loggerName, levelToAppenders);
      })
      .collect(IMap.collector(loggersOptions.size()));
  }

  static IMultiMap<String, String> parseLoggerConfig(final String appenderConfig) {
    return StreamUtils.createStream(appenderConfig.trim().split("]"))
      .flatMap(cond -> {
        final var colonIndex = cond.indexOf(':');
        final var levelName = cond.substring(0, colonIndex).replace(',', ' ').trim().toUpperCase();
        return StreamUtils
          .createStream(cond.substring(colonIndex + 2).trim().split(","))
          .map(appenderName -> KV.of(levelName, appenderName));
      })
      .collect(IMultiMap.collector(16));
  }

}
