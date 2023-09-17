package tech.onega.jvm.arangodb.client;

import tech.onega.jvm.std.annotation.ThreadSafe;

@ThreadSafe
public interface ArangoDbClient extends AutoCloseable {

  @Override
  void close();

}
