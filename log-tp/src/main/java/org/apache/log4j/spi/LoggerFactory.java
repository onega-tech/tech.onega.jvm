package org.apache.log4j.spi;

import org.apache.log4j.Logger;

@Deprecated
public interface LoggerFactory {

  public Logger makeNewLoggerInstance(String name);

}
