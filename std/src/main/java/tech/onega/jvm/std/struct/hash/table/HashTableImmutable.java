package tech.onega.jvm.std.struct.hash.table;

import java.io.Serializable;
import java.util.Iterator;
import java.util.stream.Stream;
import tech.onega.jvm.std.annotation.Unsafe;
import tech.onega.jvm.std.lang.Equals;
import tech.onega.jvm.std.struct.hash.Hash;
import tech.onega.jvm.std.struct.hash.HashFunction;
import tech.onega.jvm.std.struct.hash.KV;
import tech.onega.jvm.std.struct.stream.StreamUtils;

final public class HashTableImmutable<K, V> implements Iterable<KV<K, V>>, Serializable {

  private static final long serialVersionUID = 1L;

  private static HashTableImmutable<Object, Object> EMPTY_REF = new HashTableImmutable<>(0, null, null, null);

  @SuppressWarnings("unchecked")
  public static <K, V> HashTableImmutable<K, V> empty() {
    return (HashTableImmutable<K, V>) EMPTY_REF;
  }

  private static int indexOf(final int hash, final int capacity) {
    return (capacity - 1) & hash;
  }

  @Unsafe
  public static <K, V> HashTableImmutable<K, V> of(
    final int size,
    final HashTableNode<K, V>[] table,
    final HashTableNode<K, V> first, final HashFunction<K> hashFunction) {
    //
    return size == 0 ? empty() : new HashTableImmutable<>(size, table, first, hashFunction);
  }

  private final HashTableNode<K, V> first;

  private final HashTableNode<K, V>[] table;

  private final int size;

  private final HashFunction<K> hashFunction;

  private final int hashCode;

  private HashTableImmutable(
    final int size,
    final HashTableNode<K, V>[] table,
    final HashTableNode<K, V> first,
    final HashFunction<K> hashFunction) {
    //
    this.table = table;
    this.size = size;
    this.first = first;
    this.hashFunction = hashFunction;
    this.hashCode = Hash.iterable(this);
  }

  public boolean contains(final K key) {
    return get(key) != null;
  }

  @Override
  public boolean equals(final Object obj) {
    return Equals.check(this, obj, f -> new Object[] { f.size, f.iterator() });
  }

  public KV<K, V> first() {
    return first == null ? null : first.kv;
  }

  public KV<K, V> get(final K key) {
    if (size == 0 || table.length == 0) {
      return null;
    }
    final int hash = hashFunction.hash(key);
    final int index = indexOf(hash, table.length);
    HashTableNode<K, V> node = table[index];
    while (node != null) {
      if (node.kv.key == key || hashFunction.equals(node.kv.key, key)) {
        return node == null ? null : node.kv;
      }
      node = node.next;
    }
    return null;
  }

  @Override
  public int hashCode() {
    return this.hashCode;
  }

  public HashFunction<K> hashFunction() {
    return hashFunction;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  @Override
  public Iterator<KV<K, V>> iterator() {
    return new HashTableIterator<>(first, null);
  }

  public int size() {
    return size;
  }

  public Stream<KV<K, V>> stream() {
    return StreamUtils.createStream(iterator());
  }

  public KV<K, V>[] toArray() {
    @SuppressWarnings("unchecked")
    final KV<K, V>[] kvs = new KV[size()];
    int pos = 0;
    for (final KV<K, V> kv : this) {
      kvs[pos++] = kv;
    }
    return kvs;
  }

}
