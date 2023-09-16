package org.apache.log4j;

import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

@Deprecated
public interface Appender {

  void addFilter(Filter newFilter);

  public void clearFilters();

  public void close();

  public void doAppend(LoggingEvent event);

  public ErrorHandler getErrorHandler();

  public Filter getFilter();

  public Layout getLayout();

  public String getName();

  public boolean requiresLayout();

  public void setErrorHandler(ErrorHandler errorHandler);

  public void setLayout(Layout layout);

  public void setName(String name);

}
