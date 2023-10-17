package org.slf4j.impl;

import org.slf4j.IMarkerFactory;
import org.slf4j.spi.MarkerFactoryBinder;

public class StaticMarkerBinder implements MarkerFactoryBinder {

  public static final StaticMarkerBinder SINGLETON = new StaticMarkerBinder();

  public static StaticMarkerBinder getSingleton() {
    return SINGLETON;
  }

  private StaticMarkerBinder() {
    throw new UnsupportedOperationException("This code should never make it into the jar");
  }

  public IMarkerFactory getMarkerFactory() {
    throw new UnsupportedOperationException("This code should never make it into the jar");
  }

  public String getMarkerFactoryClassStr() {
    throw new UnsupportedOperationException("This code should never make it into the jar");
  }

}