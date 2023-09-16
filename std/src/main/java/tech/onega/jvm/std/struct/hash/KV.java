package tech.onega.jvm.std.struct.hash;

import java.io.Serializable;
import java.util.Map;
import tech.onega.jvm.std.annotation.Immutable;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.lang.Equals;

@Immutable
@ThreadSafe
final public class KV<K, V> implements Serializable {

  private static final long serialVersionUID = 1L;

  public static <K, V> KV<K, V> of(final java.util.Map.Entry<K, V> entry) {
    return new KV<>(entry);
  }

  public static <K, V> KV<K, V> of(final K k, final V v) {
    return new KV<>(k, v);
  }

  public final K key;

  public final V value;

  public final int hashCode;

  private KV(final K key, final V value) {
    this.key = key;
    this.value = value;
    this.hashCode = Hash.codes(key, value);
  }

  private KV(final Map.Entry<K, V> entry) {
    this(entry.getKey(), entry.getValue());
  }

  @Override
  public boolean equals(final Object obj) {
    return Equals.check(this, obj, f -> new Object[] { f.key, f.value });
  }

  @Override
  public int hashCode() {
    return hashCode;
  }

  public K key() {
    return key;
  }

  @Override
  public String toString() {
    return String.format("%s:%s", key(), value());
  }

  public V value() {
    return value;
  }

}
