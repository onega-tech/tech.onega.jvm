package tech.onega.jvm.std.log;

import tech.onega.jvm.std.annotation.ThreadSafe;

@ThreadSafe
public interface LogAppender extends AutoCloseable {

  void append(LogMessage message) throws Exception;

  @Override
  void close();

}
