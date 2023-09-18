package tech.onega.jvm.arangodb.client;

import java.time.Duration;
import org.testng.annotations.Test;
import tech.onega.jvm.arangodb.client.impl.ArangoDbClientImpl;
import tech.onega.jvm.std.io.Console;

public class ArangoDbClientImplTest {

  @Test
  void testRnd() throws Exception {
    final var config = new ArangoDbClientImpl.Config(
      "127.0.0.1",
      8529,
      "root",
      "arangodb",
      Duration.ofSeconds(10),
      false,
      true);
    try (var client = ArangoDbClientImpl.createDefaultClient(config)) {
      for (var i = 0; i < 10; i++) {
        final var response = client.listAllDatabasesAsync().get();
        Console.err(response.statusCode());
        Console.err(response.headers());
        Console.err(response.body() == null ? null : new String(response.body()));
      }
    }
  }

}
