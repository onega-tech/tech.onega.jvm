package tech.onega.jvm.postgres.client.impl;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import tech.onega.jvm.postgres.client.domain.PostgresRecord;
import tech.onega.jvm.std.annotation.NotThreadSafe;
import tech.onega.jvm.std.annotation.Nullable;

@NotThreadSafe
class PostgresRecordImpl implements PostgresRecord {

  private final LinkedHashMap<String, Object> data;

  public PostgresRecordImpl() {
    this.data = new LinkedHashMap<>();
  }

  public PostgresRecordImpl(final Map<String, Object> data) {
    this.data = new LinkedHashMap<>(data);
  }

  @Override
  public Set<String> fields() {
    return this.data.keySet();
  }

  @Override
  public Optional<Boolean> getBoolean(final String key) {
    return this.getObject(key).map(v -> (boolean) v);
  }

  @Override
  public Optional<Byte> getByte(final String key) {
    return this.getObject(key).map(v -> (byte) v);
  }

  @Override
  public Optional<byte[]> getByteArray(final String key) {
    return this.getObject(key).map(v -> (byte[]) v);
  }

  @Override
  public Optional<Double> getDouble(final String key) {
    return this.getObject(key).map(v -> (double) v);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public <E extends Enum> Optional<E> getEnum(final String key, final Class<E> enumType) {
    return this.getObject(key).map(v -> (E) Enum.valueOf(enumType, (String) v));
  }

  @Override
  public Optional<Float> getFloat(final String key) {
    return this.getObject(key).map(v -> (float) v);
  }

  @Override
  public Optional<Instant> getInstant(final String key) {
    return this.getObject(key)
      .map(v -> {
        if (v instanceof final java.sql.Timestamp vTimestamp) {
          return vTimestamp.toInstant();
        }
        return (Instant) v;
      });
  }

  @Override
  public Optional<Integer> getInteger(final String key) {
    return this.getObject(key).map(v -> (int) v);
  }

  @Override
  public Optional<Long> getLong(final String key) {
    return this.getObject(key).map(v -> (long) v);
  }

  @Override
  public Optional<Object> getObject(final String key) {
    final var v = this.data.get(key);
    return v == null ? Optional.empty() : Optional.of(this.data.get(key));
  }

  @Override
  public Optional<Short> getShort(final String key) {
    return this.getObject(key).map(v -> (short) v);
  }

  @Override
  public Optional<String> getString(final String key) {
    return this.getObject(key).map(v -> (String) v);
  }

  @Override
  public Optional<UUID> getUUID(final String key) {
    return this.getObject(key).map(v -> (UUID) v);
  }

  @Override
  public PostgresRecordImpl set(final String key, @Nullable final Object value) {
    if (value instanceof final Instant valueInstant) {
      this.data.put(key, java.sql.Timestamp.from(valueInstant));
    }
    else {
      this.data.put(key, value);
    }
    return this;
  }

  @Override
  public int size() {
    return this.data.size();
  }

  @Override
  public Map<String, Object> toMap() {
    return new LinkedHashMap<String, Object>(this.data);
  }

}