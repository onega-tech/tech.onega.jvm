package org.apache.commons.logging;

public abstract class LogFactory {

  public static final String DIAGNOSTICS_DEST_PROPERTY = "org.apache.commons.logging.diagnostics.dest";

  public static final String FACTORY_DEFAULT = "org.apache.commons.logging.impl.LogFactoryImpl";

  public static final String FACTORY_PROPERTIES = "commons-logging.properties";

  public static final String FACTORY_PROPERTY = "org.apache.commons.logging.LogFactory";

  public static final String HASHTABLE_IMPLEMENTATION_PROPERTY = "org.apache.commons.logging.LogFactory.HashtableImpl";

  public static final String PRIORITY_KEY = "priority";

  public static final String TCCL_KEY = "use_tccl";

  private static final LogFactoryImpl IMPL = new LogFactoryImpl();

  public static LogFactory getFactory() throws LogConfigurationException {
    return IMPL;
  }

  @SuppressWarnings("rawtypes")
  public static Log getLog(final Class clazz) throws LogConfigurationException {
    return getFactory().getInstance(clazz);
  }

  public static Log getLog(final String name) throws LogConfigurationException {
    return getFactory().getInstance(name);
  }

  public static String objectId(final Object o) {
    return o == null ? "null" : (o.getClass().getName() + "@" + System.identityHashCode(o));
  }

  public static void release(final ClassLoader classLoader) {
  }

  public static void releaseAll() {
  }

  public abstract Object getAttribute(String name);

  public abstract String[] getAttributeNames();

  @SuppressWarnings("rawtypes")
  public abstract Log getInstance(Class clazz) throws LogConfigurationException;

  public abstract Log getInstance(String name) throws LogConfigurationException;

  public abstract void release();

  public abstract void removeAttribute(String name);

  public abstract void setAttribute(String name, Object value);

}
