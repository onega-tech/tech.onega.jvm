package org.slf4j.impl;

import org.slf4j.ILoggerFactory;

public class StaticLoggerBinder {

  private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();

  public static final StaticLoggerBinder getSingleton() {
    return SINGLETON;
  }

  String REQUESTED_API_VERSION = "1.6.99"; // !final

  private StaticLoggerBinder() {
    throw new UnsupportedOperationException("This code should have never made it into slf4j-api.jar");
  }

  public ILoggerFactory getLoggerFactory() {
    throw new UnsupportedOperationException("This code should never make it into slf4j-api.jar");
  }

  public String getLoggerFactoryClassStr() {
    throw new UnsupportedOperationException("This code should never make it into slf4j-api.jar");
  }

}