package tech.onega.jvm.std.struct.hash.table;

import java.io.Serializable;
import tech.onega.jvm.std.struct.hash.KV;

final class HashTableNode<K, V> implements Serializable {

  private static final long serialVersionUID = 1L;

  public final int keyHash;

  public KV<K, V> kv;

  public HashTableNode<K, V> next;

  public HashTableNode<K, V> before;

  public HashTableNode<K, V> after;

  public HashTableNode(final int keyHash, final KV<K, V> kv) {
    this.kv = kv;
    this.keyHash = keyHash;
  }

}