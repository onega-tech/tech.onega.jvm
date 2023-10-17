package tech.onega.jvm.arangodb.client;

import java.time.Duration;
import org.testng.annotations.Test;
import tech.onega.jvm.arangodb.client.impl.ArangoDbClientImpl;
import tech.onega.jvm.std.io.Console;

public class ArangoDbClientImplTest extends ArangoDbClientTestAbstract {

  @Test
  void testRnd() throws Exception {
    final var config = new ArangoDbClientImpl.Config(
      ARANGO_DB_TEST_CONTAINER.getHost(),
      ARANGO_DB_TEST_CONTAINER.getPort(),
      ARANGO_DB_TEST_CONTAINER.getUser(),
      ARANGO_DB_TEST_CONTAINER.getPassword(),
      Duration.ofSeconds(10),
      false,
      true);
    try (var client = ArangoDbClientImpl.createDefaultClient(config)) {
      for (var i = 0; i < 10; i++) {
        final var response = client.listAllDatabasesAsync().get();
        Console.errJson(response);
      }
    }
  }

}
