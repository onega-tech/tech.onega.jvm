package tech.onega.jvm.std.struct.hash.table;

import java.util.function.Consumer;
import tech.onega.jvm.std.struct.hash.KV;
import tech.onega.jvm.std.struct.iterator.IteratorLimitOffset;

final public class HashTableCursor<K, V> {

  private final Consumer<KV<K, V>> removeConsumer;

  private KV<K, V> kv;

  private final IteratorLimitOffset<KV<K, V>> iterator;

  private final HashTableIterator<K, V> tableIterator;

  HashTableCursor(final HashTableIterator<K, V> tableIterator, final int limit, final int offset,
    final Consumer<KV<K, V>> removeConsumer) {
    this.tableIterator = tableIterator;
    this.iterator = new IteratorLimitOffset<>(tableIterator, limit, offset);
    this.removeConsumer = removeConsumer;
    this.kv = null;
  }

  public KV<K, V> kv() {
    return kv;
  }

  public boolean next() {
    if (iterator.hasNext()) {
      kv = iterator.next();
      return true;
    }
    return false;
  }

  public void remove() {
    tableIterator.versionValidator.run();
    removeConsumer.accept(kv);
  }

}