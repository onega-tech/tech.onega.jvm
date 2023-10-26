package tech.onega.jvm.arangodb.client;

import java.time.Duration;
import org.testng.annotations.Test;
import tech.onega.jvm.arangodb.client.domain.ArangoDbClient;
import tech.onega.jvm.arangodb.client.domain.api.ArangoDbApiCollectionList;
import tech.onega.jvm.arangodb.client.domain.api.ArangoDbApiDatabaseCreate;
import tech.onega.jvm.arangodb.client.domain.api.ArangoDbApiDatabaseGet;
import tech.onega.jvm.arangodb.client.impl.ArangoDbClientImpl;
import tech.onega.jvm.std.io.Console;
import tech.onega.jvm.std.struct.list.IList;

public class ArangoDbClientImplTest { // extends ArangoDbClientTestAbstract

  private static ArangoDbClient createArangoDbClient() {
    /*/
    final var config = new ArangoDbClientImpl.Config(
       ARANGO_DB_TEST_CONTAINER.getHost(),
       ARANGO_DB_TEST_CONTAINER.getPort(),
       ARANGO_DB_TEST_CONTAINER.getUser(),
       ARANGO_DB_TEST_CONTAINER.getPassword(),
       Duration.ofSeconds(10),
       false,
       true);
       /*/
    final var config = new ArangoDbClientImpl.Config(
      "127.0.0.1",
      8529,
      "root",
      "arangodb",
      Duration.ofSeconds(10),
      false,
      false,
      true,
      true);
    return ArangoDbClientImpl.createDefaultClient(config);
  }

  @Test
  void testRnd() throws Exception {
    try (var client = createArangoDbClient()) {
      {
        final var response = client.databaseCreate(new ArangoDbApiDatabaseCreate.Request(
          "test",
          null,
          IList.of(new ArangoDbApiDatabaseCreate.Request.User("root", "", true, null))))
          .get();
        //_system
        Console.errJson(response);
      }
      {
        final var response = client.databaseList().get();
        Console.errJson(response);
      }
      {
        final var response = client.databaseGet(new ArangoDbApiDatabaseGet.Request("test")).get();
        Console.errJson(response);
      }
      {
        final var response = client.collectionList(new ArangoDbApiCollectionList.Request("test", true)).get();
        Console.errJson(response);
      }
    }
  }

}
