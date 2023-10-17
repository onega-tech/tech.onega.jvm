package org.slf4j.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MDCAdapterBasic implements MDCAdapter {

  private final InheritableThreadLocal<Map<String, String>> inheritableThreadLocal = new InheritableThreadLocal<Map<String, String>>() {

    @Override
    protected Map<String, String> childValue(final Map<String, String> parentValue) {
      if (parentValue == null) {
        return null;
      }
      return new HashMap<String, String>(parentValue);
    }

  };

  @Override
  public void clear() {
    final Map<String, String> map = this.inheritableThreadLocal.get();
    if (map != null) {
      map.clear();
      this.inheritableThreadLocal.remove();
    }
  }

  @Override
  public String get(final String key) {
    final Map<String, String> map = this.inheritableThreadLocal.get();
    if ((map != null) && (key != null)) {
      return map.get(key);
    }
    else {
      return null;
    }
  }

  @Override
  public Map<String, String> getCopyOfContextMap() {
    final Map<String, String> oldMap = this.inheritableThreadLocal.get();
    if (oldMap != null) {
      return new HashMap<String, String>(oldMap);
    }
    else {
      return null;
    }
  }

  public Set<String> getKeys() {
    final Map<String, String> map = this.inheritableThreadLocal.get();
    if (map != null) {
      return map.keySet();
    }
    else {
      return null;
    }
  }

  @Override
  public void put(final String key, final String val) {
    if (key == null) {
      throw new IllegalArgumentException("key cannot be null");
    }
    Map<String, String> map = this.inheritableThreadLocal.get();
    if (map == null) {
      map = new HashMap<String, String>();
      this.inheritableThreadLocal.set(map);
    }
    map.put(key, val);
  }

  @Override
  public void remove(final String key) {
    final Map<String, String> map = this.inheritableThreadLocal.get();
    if (map != null) {
      map.remove(key);
    }
  }

  @Override
  public void setContextMap(final Map<String, String> contextMap) {
    this.inheritableThreadLocal.set(new HashMap<String, String>(contextMap));
  }

}
