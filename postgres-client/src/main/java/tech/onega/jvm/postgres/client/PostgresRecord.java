package tech.onega.jvm.postgres.client;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import tech.onega.jvm.std.annotation.NotThreadSafe;
import tech.onega.jvm.std.annotation.Nullable;

@NotThreadSafe
public interface PostgresRecord {

  Set<String> fields();

  Optional<Boolean> getBoolean(final String key);

  Optional<Byte> getByte(final String key);

  Optional<byte[]> getByteArray(final String key);

  Optional<Double> getDouble(final String key);

  @SuppressWarnings("rawtypes")
  <E extends Enum> Optional<E> getEnum(final String key, Class<E> enumType);

  Optional<Float> getFloat(final String key);

  Optional<Instant> getInstant(final String key);

  Optional<Integer> getInteger(final String key);

  Optional<Long> getLong(final String key);

  Optional<Object> getObject(final String key);

  Optional<Short> getShort(final String key);

  Optional<String> getString(final String key);

  Optional<UUID> getUUID(final String key);

  PostgresRecord set(final String key, @Nullable final Object value);

  int size();

  Map<String, Object> toMap();

}
