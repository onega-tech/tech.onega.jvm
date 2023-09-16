package org.apache.log4j.spi;

@Deprecated
public abstract class Filter implements OptionHandler {

  public static final int DENY = -1;

  public static final int NEUTRAL = 0;

  public static final int ACCEPT = 1;

  @Deprecated
  public Filter next;

  @Override
  public void activateOptions() {
  }

  abstract public int decide(LoggingEvent event);

  public Filter getNext() {
    return next;
  }

  public void setNext(final Filter next) {
    this.next = next;
  }

}
