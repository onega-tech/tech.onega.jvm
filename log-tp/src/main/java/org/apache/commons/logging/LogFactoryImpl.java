package org.apache.commons.logging;

final class LogFactoryImpl extends LogFactory {

  @Override
  public Object getAttribute(final String name) {
    return null;
  }

  @Override
  public String[] getAttributeNames() {
    return new String[0];
  }

  @SuppressWarnings("rawtypes")
  @Override
  public Log getInstance(final Class clazz) throws LogConfigurationException {
    return new LogImpl(tech.onega.jvm.std.log.Loggers.find(clazz));
  }

  @Override
  public Log getInstance(final String name) throws LogConfigurationException {
    return new LogImpl(tech.onega.jvm.std.log.Loggers.find(name));
  }

  @Override
  public void release() {
  }

  @Override
  public void removeAttribute(final String name) {
  }

  @Override
  public void setAttribute(final String name, final Object value) {
  }

}
