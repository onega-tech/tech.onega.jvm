package org.slf4j;

import java.util.Map;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.MDCAdapterBasic;

public class MDC {

  public static class MDCCloseable implements AutoCloseable {

    private final String key;

    private MDCCloseable(final String key) {
      this.key = key;
    }

    @Override
    public void close() {
      MDC.remove(this.key);
    }

  }

  private final static MDCAdapter MDC_ADAPTER = new MDCAdapterBasic();

  public static void clear() {
    MDC_ADAPTER.clear();
  }

  public static String get(final String key) throws IllegalArgumentException {
    if (key == null) {
      throw new IllegalArgumentException("key parameter cannot be null");
    }
    if (MDC_ADAPTER == null) {
      throw new IllegalStateException("MDCAdapter cannot be null");
    }
    return MDC_ADAPTER.get(key);
  }

  public static Map<String, String> getCopyOfContextMap() {
    if (MDC_ADAPTER == null) {
      throw new IllegalStateException("MDCAdapter cannot be null");
    }
    return MDC_ADAPTER.getCopyOfContextMap();
  }

  public static MDCAdapter getMDCAdapter() {
    return MDC_ADAPTER;
  }

  public static void put(final String key, final String val) throws IllegalArgumentException {
    if (key == null) {
      throw new IllegalArgumentException("key parameter cannot be null");
    }
    if (MDC_ADAPTER == null) {
      throw new IllegalStateException("MDCAdapter cannot be null");
    }
    MDC_ADAPTER.put(key, val);
  }

  public static MDCCloseable putCloseable(final String key, final String val) throws IllegalArgumentException {
    put(key, val);
    return new MDCCloseable(key);
  }

  public static void remove(final String key) throws IllegalArgumentException {
    if (key == null) {
      throw new IllegalArgumentException("key parameter cannot be null");
    }
    if (MDC_ADAPTER == null) {
      throw new IllegalStateException("MDCAdapter cannot be null");
    }
    MDC_ADAPTER.remove(key);
  }

  public static void setContextMap(final Map<String, String> contextMap) {
    if (MDC_ADAPTER == null) {
      throw new IllegalStateException("MDCAdapter cannot be null");
    }
    MDC_ADAPTER.setContextMap(contextMap);
  }

  private MDC() {
  }

}
