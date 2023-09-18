package tech.onega.jvm.arangodb.client.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import tech.onega.jvm.std.annotation.Nullable;

public record ArangoDbResponse(
  @JsonProperty("code") int code,
  @JsonProperty("error") boolean error,
  @JsonProperty("errorNum") @Nullable Integer errorNum,
  @JsonProperty("errorMessage") @Nullable String errorMessage,
  @JsonProperty("result") @Nullable Object result) {
}
