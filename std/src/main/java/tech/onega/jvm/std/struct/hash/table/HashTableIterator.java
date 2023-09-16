package tech.onega.jvm.std.struct.hash.table;

import java.util.Iterator;
import java.util.NoSuchElementException;
import tech.onega.jvm.std.annotation.Nullable;
import tech.onega.jvm.std.struct.hash.KV;

final class HashTableIterator<K, V> implements Iterator<KV<K, V>> {

  private HashTableNode<K, V> next;

  public final Runnable versionValidator;

  public HashTableIterator(final HashTableNode<K, V> first, @Nullable final Runnable versionValidator) {
    this.next = first;
    this.versionValidator = versionValidator;
  }

  @Override
  public boolean hasNext() {
    if (versionValidator != null) {
      versionValidator.run();
    }
    return next != null;
  }

  @Override
  public KV<K, V> next() {
    final HashTableNode<K, V> node = next;
    if (node == null) {
      throw new NoSuchElementException();
    }
    next = node.after;
    return node == null ? null : node.kv;
  }

}