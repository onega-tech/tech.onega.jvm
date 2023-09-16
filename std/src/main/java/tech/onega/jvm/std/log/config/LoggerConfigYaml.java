package tech.onega.jvm.std.log.config;

import com.fasterxml.jackson.databind.node.ObjectNode;
import tech.onega.jvm.std.codec.yaml.YamlCodec;
import tech.onega.jvm.std.log.impl.LoggerContextFactory;
import tech.onega.jvm.std.struct.bytes.IBytes;
import tech.onega.jvm.std.struct.hash.KV;
import tech.onega.jvm.std.struct.map.IMap;
import tech.onega.jvm.std.struct.map.IMultiMap;
import tech.onega.jvm.std.struct.stream.StreamUtils;

final public class LoggerConfigYaml {

  private static IMap<String, IMap<String, String>> createAppendersOptions(final ObjectNode appendersNode) {
    return StreamUtils
      .createStream(appendersNode.fields())
      .map(appenderKV -> {
        final var appenderName = appenderKV.getKey();
        final var optionsNode = (ObjectNode) appenderKV.getValue();
        final var optionsMap = StreamUtils
          .createStream(optionsNode.fields())
          .map(optsKV -> KV.of(optsKV.getKey(), optsKV.getValue().asText())).collect(IMap.collector());
        return KV.of(appenderName, optionsMap);
      })
      .collect(IMap.collector());
  }

  public static LoggerContextFactory createContextFactory(final IBytes yaml, final ClassLoader classLoader) {
    final var config = YamlCodec.parse(yaml, ObjectNode.class);
    final var appendersNode = (ObjectNode) config.get("appenders");
    final var appendersOptions = createAppendersOptions(appendersNode);
    final var appenders = LoggerConfigUtils.createAppenders(appendersOptions, classLoader);
    final var loggersNode = (ObjectNode) config.get("loggers");
    final var loggersOptions = createLoggersOptions(loggersNode);
    final var loggersLevelAppenders = LoggerConfigUtils.createLoggersLevelAppenders(loggersOptions, appenders);
    return new LoggerContextFactory(appenders.values(), loggersLevelAppenders);
  }

  public static LoggerContextFactory createContextFactory(final String yaml, final ClassLoader classLoader) {
    return createContextFactory(IBytes.of(yaml), classLoader);
  }

  private static IMap<String, IMultiMap<String, String>> createLoggersOptions(final ObjectNode loggersNode) {
    return StreamUtils
      .createStream(loggersNode.fields())
      .map(kv -> {
        final var loggerName = kv.getKey().equals("default") ? "" : kv.getKey();
        final var loggerConfig = LoggerConfigUtils.parseLoggerConfig(kv.getValue().asText());
        return KV.of(loggerName, loggerConfig);
      })
      .collect(IMap.collector());
  }

}
