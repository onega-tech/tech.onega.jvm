package tech.onega.jvm.arangodb.client.domain;

import java.time.Duration;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record ArangoDbClientConfig(
  @NotBlank String host,
  @Min(1) int port,
  @NotBlank String user,
  @NotBlank String password,
  @NotNull Duration connectionTimeout,
  @NotNull Duration requestTimeout) {
}
