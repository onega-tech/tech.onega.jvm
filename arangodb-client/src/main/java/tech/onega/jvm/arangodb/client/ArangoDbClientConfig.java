package tech.onega.jvm.arangodb.client;

import java.time.Duration;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record ArangoDbClientConfig(
  @NotBlank String host,
  @Min(1) int port,
  @NotBlank String user,
  @NotBlank String password,
  @NotNull Duration connectionTimeout, // = Duration.ofSeconds(60);
  @NotNull Duration requestTimeout // = Duration.ofSeconds(60);
) {
}
