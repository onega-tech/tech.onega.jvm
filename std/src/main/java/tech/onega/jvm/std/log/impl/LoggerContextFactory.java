package tech.onega.jvm.std.log.impl;

import java.util.concurrent.atomic.AtomicLong;
import tech.onega.jvm.std.annotation.Immutable;
import tech.onega.jvm.std.codec.json.JsonCodec;
import tech.onega.jvm.std.lang.Exec;
import tech.onega.jvm.std.log.LogAppender;
import tech.onega.jvm.std.log.LogLevel;
import tech.onega.jvm.std.log.LoggerContext;
import tech.onega.jvm.std.log.appenders.console.ConsoleAppender;
import tech.onega.jvm.std.struct.hash.KV;
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.struct.map.IMap;
import tech.onega.jvm.std.struct.map.IMultiMap;
import tech.onega.jvm.std.struct.map.MMap;
import tech.onega.jvm.std.struct.set.MSet;

@Immutable
final public class LoggerContextFactory implements AutoCloseable {

  private final static AtomicLong VERSION_COUNTER = new AtomicLong(0);

  public static LoggerContextFactory createDefault(final ClassLoader classLoader) {
    final LogAppender consoleAppender = ConsoleAppender.createDefault();
    final IList<LogAppender> appenders = IList.of(consoleAppender);
    final IMap<String, IMultiMap<LogLevel, LogAppender>> loggersLevelAppenders = IMap.of(KV.of("", IMultiMap.of(
      KV.of(LogLevel.DEBUG, consoleAppender),
      KV.of(LogLevel.INFO, consoleAppender),
      KV.of(LogLevel.ERROR, consoleAppender))));
    return new LoggerContextFactory(appenders, loggersLevelAppenders);
  }

  private final long version;

  private final IList<LogAppender> appenders;

  private final IMap<String, IMultiMap<LogLevel, LogAppender>> loggersLevelAppenders;

  public LoggerContextFactory(
    final IList<LogAppender> appenders,
    final IMap<String, IMultiMap<LogLevel, LogAppender>> loggersLevelAppenders) {
    version = VERSION_COUNTER.incrementAndGet();
    this.appenders = appenders;
    this.loggersLevelAppenders = loggersLevelAppenders;
  }

  @Override
  public void close() {
    for (final LogAppender appender : appenders) {
      Exec.quietly(appender::close);
    }
  }

  public LoggerContext createContext(final String loggerName) {
    final MMap<LogLevel, MSet<LogAppender>> levelAppenders = MMap.create(loggersLevelAppenders.size());
    for (final KV<String, IMultiMap<LogLevel, LogAppender>> loggerToAppenders : loggersLevelAppenders) {
      if (loggerName.startsWith(loggerToAppenders.key())) {
        for (final KV<LogLevel, LogAppender> levelAndAppender : loggerToAppenders.value()) {
          MSet<LogAppender> appenders = levelAppenders.get(levelAndAppender.key());
          if (appenders == null) {
            appenders = MSet.create(8);
            levelAppenders.add(levelAndAppender.key(), appenders);
          }
          if (levelAndAppender.value() == null) {
            appenders.clear();
          }
          else {
            appenders.add(levelAndAppender.value());
          }
        }
      }
    }
    return new LoggerContext(
      loggerName,
      version,
      levelAppenders.get(LogLevel.DEBUG).toIList(),
      levelAppenders.get(LogLevel.INFO).toIList(),
      levelAppenders.get(LogLevel.ERROR).toIList());
  }

  public IList<LogAppender> getAppenders() {
    return appenders;
  }

  public long getVersion() {
    return version;
  }

  @Override
  public String toString() {
    return JsonCodec.toString(
      IMap.of(
        KV.of("version", version),
        KV.of("appenders", appenders),
        KV.of("loggersToAppenders", loggersLevelAppenders)),
      true);
  }

}
