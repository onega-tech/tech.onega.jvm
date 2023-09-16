package tech.onega.jvm.std.log.impl;

import java.util.concurrent.atomic.AtomicReference;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.log.Logger;
import tech.onega.jvm.std.log.LoggerContext;
import tech.onega.jvm.std.log.LoggerFactory;
import tech.onega.jvm.std.log.config.LoggerConfigProperties;
import tech.onega.jvm.std.log.config.LoggerConfigYaml;
import tech.onega.jvm.std.log.jul.JulHandler;
import tech.onega.jvm.std.struct.map.IMap;

@ThreadSafe
final public class LoggerFactoryImpl implements LoggerFactory {

  private final ClassLoader classLoader;

  private final AtomicReference<LoggerContextFactory> contextFactoryRef;

  public LoggerFactoryImpl(final ClassLoader classLoader) {
    this.classLoader = classLoader;
    contextFactoryRef = new AtomicReference<>(LoggerContextFactory.createDefault(classLoader));
    JulHandler.install();
    Runtime.getRuntime().addShutdownHook(new Thread(this::close));
  }

  @Override
  public void close() {
    final var contextFactory = contextFactoryRef.getAndSet(null);
    if (contextFactory != null) {
      contextFactory.close();
    }
  }

  @Override
  public void configure(final IMap<String, String> properties) {
    final var newContextFactory = LoggerConfigProperties.createContextFactory(properties, classLoader);
    configure(newContextFactory);
  }

  public void configure(final LoggerContextFactory newContextFactory) {
    final var oldContextFactory = contextFactoryRef.getAndSet(newContextFactory);
    if (oldContextFactory != null) {
      oldContextFactory.close();
    }
  }

  @Override
  public void configureYaml(final String yaml) {
    final var newContextFactory = LoggerConfigYaml.createContextFactory(yaml, classLoader);
    configure(newContextFactory);
  }

  @Override
  public LoggerContext createContext(final String name) {
    final var contextFactory = contextFactoryRef.get();
    return contextFactory == null ? null : contextFactory.createContext(name);
  }

  @Override
  public Logger createLogger(final Class<?> type) {
    return createLogger(type.getName());
  }

  @Override
  public Logger createLogger(final String name) {
    return new LoggerImpl(this, name);
  }

  @Override
  public long getVersion() {
    final var contextFactory = contextFactoryRef.get();
    return contextFactory == null ? -1 : contextFactory.getVersion();
  }

}
