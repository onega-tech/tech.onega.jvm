package tech.onega.jvm.std.log;

import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.struct.bytes.IBytes;

@ThreadSafe
public interface LogFormatter {

  IBytes format(Iterable<LogMessage> messages, int size) throws Exception;

}
