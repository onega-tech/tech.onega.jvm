package org.slf4j.impl;

import org.slf4j.spi.MDCAdapter;

public class StaticMDCBinder {

  public static final StaticMDCBinder SINGLETON = new StaticMDCBinder();

  public static final StaticMDCBinder getSingleton() {
    return SINGLETON;
  }

  private StaticMDCBinder() {
    throw new UnsupportedOperationException("This code should never make it into the jar");
  }

  public MDCAdapter getMDCA() {
    throw new UnsupportedOperationException("This code should never make it into the jar");
  }

  public String getMDCAdapterClassStr() {
    throw new UnsupportedOperationException("This code should never make it into the jar");
  }

}