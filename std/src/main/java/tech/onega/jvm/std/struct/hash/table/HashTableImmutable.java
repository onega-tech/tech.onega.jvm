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
    return this.get(key) != null;
  }

  @Override
  public boolean equals(final Object obj) {
    return Equals.check(this, obj, f -> new Object[] { f.size, f.iterator() });
  }

  public KV<K, V> first() {
    return this.first == null ? null : this.first.kv;
  }

  public KV<K, V> get(final K key) {
    if (this.size == 0 || this.table.length == 0) {
      return null;
    }
    final int hash = this.hashFunction.hash(key);
    final int index = indexOf(hash, this.table.length);
    HashTableNode<K, V> node = this.table[index];
    while (node != null) {
      if (node.kv.key() == key || this.hashFunction.equals(node.kv.key(), key)) {
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
    return this.hashFunction;
  }

  public boolean isEmpty() {
    return this.size == 0;
  }

  @Override
  public Iterator<KV<K, V>> iterator() {
    return new HashTableIterator<>(this.first, null);
  }

  public int size() {
    return this.size;
  }

  public Stream<KV<K, V>> stream() {
    return StreamUtils.createStream(this.iterator());
  }

  public KV<K, V>[] toArray() {
    @SuppressWarnings("unchecked")
    final KV<K, V>[] kvs = new KV[this.size()];
    int pos = 0;
    for (final KV<K, V> kv : this) {
      kvs[pos++] = kv;
    }
    return kvs;
  }

}
