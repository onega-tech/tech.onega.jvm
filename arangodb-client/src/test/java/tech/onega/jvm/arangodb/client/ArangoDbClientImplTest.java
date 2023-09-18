package tech.onega.jvm.arangodb.client;

import java.time.Duration;
import org.testng.annotations.Test;
import tech.onega.jvm.arangodb.client.domain.ArangoDbClientConfig;
import tech.onega.jvm.arangodb.client.impl.ArangoDbClientImpl;
import tech.onega.jvm.std.io.Console;

public class ArangoDbClientImplTest {

  @Test
  void testRnd() throws Exception {
    final var config = new ArangoDbClientConfig(
      "http://localhost",
      8529,
      "root",
      "arangodb",
      Duration.ofSeconds(10),
      Duration.ofSeconds(10));
    try (var client = new ArangoDbClientImpl(config)) {
      final var response = client.listAllDatabasesAsync().get();
      Console.err(response.statusCode());
      Console.err(response.headers());
      Console.err(response.body() == null ? null : new String(response.body()));
      //Authorization: Basic dXNlcjpwYXNz
    }
  }

}
