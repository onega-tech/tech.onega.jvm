package org.slf4j.spi;

import java.util.Map;

public interface MDCAdapter {

  void clear();

  String get(String key);

  Map<String, String> getCopyOfContextMap();

  void put(String key, String val);

  void remove(String key);

  void setContextMap(Map<String, String> contextMap);

}