package tech.onega.jvm.arangodb.client.domain;

import tech.onega.jvm.std.annotation.ThreadSafe;

@ThreadSafe
public interface ArangoDbClient extends AutoCloseable {

  @Override
  void close();

}
