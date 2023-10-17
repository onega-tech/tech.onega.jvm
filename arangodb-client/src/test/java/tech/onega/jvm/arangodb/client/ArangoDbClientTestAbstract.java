package tech.onega.jvm.arangodb.client;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import tech.onega.jvm.arangodb.testcontainer.ArangodbTestContainer;
import tech.onega.jvm.std.log.Loggers;

abstract public class ArangoDbClientTestAbstract {

  static {
    Loggers.configureFromResource("log.yaml");
  }

  protected static final ArangodbTestContainer ARANGO_DB_TEST_CONTAINER = ArangodbTestContainer.create();

  @AfterSuite
  protected void afterSuite() {
    ARANGO_DB_TEST_CONTAINER.close();
  }

  @BeforeClass
  protected void beforeClass() {
  }

  @BeforeMethod
  protected void beforeMethod() {
  }

  @BeforeSuite
  protected void beforeSuite() {
  }

}
