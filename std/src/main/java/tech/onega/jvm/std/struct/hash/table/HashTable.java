package tech.onega.jvm.std.struct.hash.table;

import java.io.Serializable;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;
import tech.onega.jvm.std.annotation.Copy;
import tech.onega.jvm.std.annotation.NotThreadSafe;
import tech.onega.jvm.std.annotation.Self;
import tech.onega.jvm.std.lang.Equals;
import tech.onega.jvm.std.lang.Lambda;
import tech.onega.jvm.std.lang.Ref;
import tech.onega.jvm.std.math.MathUtils;
import tech.onega.jvm.std.struct.hash.Hash;
import tech.onega.jvm.std.struct.hash.HashFunction;
import tech.onega.jvm.std.struct.hash.HashFunctionBasic;
import tech.onega.jvm.std.struct.hash.KV;
import tech.onega.jvm.std.struct.iterable.IterableUtils;
import tech.onega.jvm.std.struct.stream.StreamUtils;

@NotThreadSafe
final public class HashTable<K, V> implements Iterable<KV<K, V>>, Serializable {

  private static final long serialVersionUID = 1L;

  public static final int DEFAULT_CAPACITY_INITIAL = MathUtils.pow2(4);

  public static final int DEFAULT_SIZE_MAX = MathUtils.pow2(30);

  public static final int DEFAULT_CAPACITY_MAX = MathUtils.pow2(30);

  public static final float DEFAULT_COLLISIONS_MAX = 1f;

  private static final HashFunction<Object> DEFAULT_HASH_FUNCTION_REF = new HashFunctionBasic<>();

  private static float calcCollisions(final int size, final int capacity) {
    return capacity <= 0 ? 0f : (float) size / (float) capacity;
  }

  private static int capacityGrow(final int capacity, final int maxCapacity) {
    final int nextCapacity = MathUtils.pow2(MathUtils.log2(capacity) + 1);
    return Math.min(nextCapacity, maxCapacity);
  }

  public static <K, V> Collector<KV<K, V>, ?, HashTable<K, V>> collector(final HashTable<K, V> table) {
    return StreamUtils.selfCollector(() -> table, (a, v) -> a.replace(v));
  }

  @SuppressWarnings("unchecked")
  public static <K> HashFunction<K> DEFAULT_HASH_FUNCTION() {
    return (HashFunction<K>) DEFAULT_HASH_FUNCTION_REF;
  }

  private static int indexOf(final int hash, final int capacity) {
    return (capacity - 1) & hash;
  }

  public static <K, V> HashTable<K, V> lifo(final int initialCapacity, final int maxCapacity) {
    return new HashTable<>(initialCapacity, DEFAULT_CAPACITY_MAX, DEFAULT_COLLISIONS_MAX, DEFAULT_HASH_FUNCTION(),
      maxCapacity, false);
  }

  @SafeVarargs
  public static <K, V> HashTable<K, V> of(final KV<K, V>... kvs) {
    final HashTable<K, V> table = new HashTable<>(kvs.length);
    for (final KV<K, V> kv : kvs) {
      table.replace(kv);
    }
    return table;
  }

  private static <K, V> HashTableNode<K, V>[] rebalance(final HashTableNode<K, V>[] oldTable, final int newLength) {
    if (oldTable.length == newLength) {
      return oldTable;
    }
    @SuppressWarnings("unchecked")
    final HashTableNode<K, V>[] newTable = new HashTableNode[newLength];
    if (newTable.length == 0) {
      return newTable;
    }
    int index = 0;
    HashTableNode<K, V> newNode = null;
    HashTableNode<K, V> exist = null;
    HashTableNode<K, V> node = null;
    for (int i = 0; i < oldTable.length; i++) {
      node = oldTable[i];
      if (node == null) {
        continue;
      }
      oldTable[i] = null;
      while (node != null) {
        newNode = node;
        node = node.next;
        {//move
          newNode.next = null;
          index = indexOf(newNode.keyHash, newTable.length);
          exist = newTable[index];
          if (exist == null) {
            newTable[index] = newNode;
          }
          else {
            while (exist.next != null) {
              exist = exist.next;
            }
            exist.next = newNode;
          }
        }
      }
    }
    return newTable;
  }

  private HashTableNode<K, V> first;

  private HashTableNode<K, V> last;

  private HashTableNode<K, V>[] table;

  private int size;

  private int sizeMax;

  private int capacity;

  private int capacityMax;

  private float collisions;

  private float collisionsMax;

  private int version;

  private final HashFunction<K> hashFunction;

  private boolean lru;

  public HashTable() {
    this(DEFAULT_CAPACITY_INITIAL, DEFAULT_CAPACITY_MAX, DEFAULT_COLLISIONS_MAX, DEFAULT_HASH_FUNCTION(),
      DEFAULT_SIZE_MAX, true);
  }

  public HashTable(final int capacityInitial) {
    this(capacityInitial, DEFAULT_CAPACITY_MAX, DEFAULT_COLLISIONS_MAX, DEFAULT_HASH_FUNCTION(), DEFAULT_SIZE_MAX,
      true);
  }

  public HashTable(final int capacityInitial, final int capacityMax) {
    this(capacityInitial, capacityMax, DEFAULT_COLLISIONS_MAX, DEFAULT_HASH_FUNCTION(), DEFAULT_SIZE_MAX, true);
  }

  public HashTable(final int capacityInitial, final int capacityMax, final float collisionsMax) {
    this(capacityInitial, capacityMax, collisionsMax, DEFAULT_HASH_FUNCTION(), DEFAULT_SIZE_MAX, true);
  }

  @SuppressWarnings("unchecked")
  public HashTable(final int capacityInitial, final int capacityMax, final float collisionsMax,
    final HashFunction<K> hashFunction, final int sizeMax, final boolean lru) {
    if (capacityInitial < 0) {
      throw new IllegalArgumentException("Illegal initial capacity: " + capacityInitial);
    }
    if (collisionsMax <= 0 || Float.isNaN(collisionsMax)) {
      throw new IllegalArgumentException("Illegal collisions max: " + collisionsMax);
    }
    this.lru = lru;
    this.hashFunction = hashFunction;
    this.capacity = capacityInitial;
    this.capacityMax = capacityMax;
    this.collisions = 0;
    this.collisionsMax = collisionsMax;
    this.size = 0;
    this.sizeMax = sizeMax;
    this.version = 0;
    table = new HashTableNode[capacity];
  }

  private KV<K, V> add(final int keyHash, final int index, final KV<K, V> kv, final HashTableNode<K, V> prev) {
    final HashTableNode<K, V> node = new HashTableNode<>(keyHash, kv);
    if (prev == null) {
      table[index] = node;
    }
    else {
      prev.next = node;
    }
    //link
    if (first == null) {
      first = node;
    }
    if (last == null) {
      last = node;
    }
    else if (last != node && node.after == null && node.before == null) {
      last.after = node;
      node.before = last;
      last = node;
    }
    size++;
    if (size > sizeMax) { //if full
      trimTo(sizeMax);
    }
    else {
      grow();
    }
    return null;
  }

  public int capacity() {
    return capacity;
  }

  public void capacity(final int newCapacity) {
    this.capacity = Math.min(newCapacity, capacityMax);
    this.capacityMax = Math.max(capacity, capacityMax);
    collisions = calcCollisions(size, capacity);
    collisionsMax = Math.max(collisions, collisionsMax);
    table = rebalance(table, capacity);
  }

  public int capacityMax() {
    return capacityMax;
  }

  public void capacityMax(final int newCapacityMax) {
    this.capacityMax = newCapacityMax;
    this.capacity = Math.min(this.capacity, capacityMax);
    collisions = calcCollisions(size, capacity);
    collisionsMax = Math.max(collisions, collisionsMax);
    table = rebalance(table, capacity);
  }

  public void clear() {
    if (size > 0) {
      first = null;
      last = null;
      size = 0;
      version++;
      Arrays.fill(table, null);
    }
  }

  public float collisions() {
    return collisions;
  }

  public void collisions(final float newCollisions) {
    this.collisions = Math.min(newCollisions, collisionsMax);
    this.collisionsMax = Math.max(collisions, collisionsMax);
    this.capacity = collisions == 0f ? 0 : (int) Math.floor(size / collisions);
    this.capacityMax = Math.max(capacity, capacityMax);
    table = rebalance(table, capacity);
  }

  public float collisionsMax() {
    return collisionsMax;
  }

  public void collisionsMax(final float newCollisionsMax) {
    this.collisionsMax = newCollisionsMax;
    this.collisions = Math.min(collisions, collisionsMax);
    this.capacity = collisions == 0f ? 0 : (int) Math.floor(size / collisions);
    this.capacityMax = Math.max(capacity, capacityMax);
    table = rebalance(table, capacity);
  }

  public boolean contains(final K key) {
    return get(key) != null;
  }

  @Self
  public HashTableImmutable<K, V> destroy() {
    trim();
    final HashTableImmutable<K, V> result = HashTableImmutable.of(size, table, first, hashFunction);
    this.size = 0;
    this.table = null;
    this.capacity = 0;
    this.collisions = 0;
    this.first = null;
    this.last = null;
    return result;
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

  private void grow() {
    version++;
    collisions = calcCollisions(size, capacity);
    while (capacity == 0 || collisions > collisionsMax) {
      capacity = capacityGrow(capacity, capacityMax);
      collisions = calcCollisions(size, capacity);
    }
    if (capacity >= table.length) {
      table = rebalance(table, capacity);
    }
  }

  @Override
  public int hashCode() {
    return Hash.iterable(this);
  }

  public HashFunction<K> hashFunction() {
    return hashFunction;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public boolean isFull() {
    return size >= sizeMax;
  }

  @Override
  public Iterator<KV<K, V>> iterator() {
    final int expectedVersion = version;
    return new HashTableIterator<>(first, () -> {
      if (expectedVersion != version) {
        throw new ConcurrentModificationException();
      }
    });
  }

  public KV<K, V> last() {
    return last == null ? null : last.kv;
  }

  public boolean lru() {
    return lru;
  }

  public void lru(final boolean lru) {
    this.lru = lru;
  }

  public KV<K, V> remove(final K key) {
    if (table.length == 0 || size == 0) {
      return null;
    }
    final int hash = hashFunction.hash(key);
    final int index = indexOf(hash, table.length);
    HashTableNode<K, V> prev = null;
    HashTableNode<K, V> node = table[index];
    while (node != null) {
      if (hashFunction.equals(node.kv.key, key)) {
        if (prev == null) {
          table[index] = node.next;
        }
        else {
          prev.next = node.next;
        }
        node.next = null;
        {//ordered
          if (node.before == null && node.after != null) {
            node.after.before = null;
          }
          else if (node.before != null && node.after == null) {
            node.before.after = null;
          }
          else if (node.before != null && node.after != null) {
            node.before.after = node.after;
            node.after.before = node.before;
          }
          if (first == node) {
            first = node.after;
          }
          if (last == node) {
            last = node.before;
          }
          node.before = null;
          node.after = null;
        }
        //done
        size--;
        version++;
        collisions = calcCollisions(size, capacity);
        return node.kv;
      }
      prev = node;
      node = node.next;
    }
    return null;
  }

  public KV<K, V> replace(final KV<K, V> kv) {
    grow();
    final int keyHash = hashFunction.hash(kv.key);
    final int index = indexOf(keyHash, table.length);
    HashTableNode<K, V> prev = null;
    HashTableNode<K, V> node = table[index];
    while (node != null) {
      if (hashFunction.equals(node.kv.key, kv.key)) {
        return update(node, kv);
      }
      prev = node;
      node = node.next;
    }
    return add(keyHash, index, kv, prev);
  }

  public int size() {
    return size;
  }

  public void size(final int newSize) {
    trimTo(newSize);
  }

  public int sizeMax() {
    return sizeMax;
  }

  public void sizeMax(final int newSizeMax) {
    trimTo(newSizeMax);
    this.sizeMax = newSizeMax;
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

  @Copy
  public HashTableImmutable<K, V> toImmutable() {
    final HashTable<K, V> result = new HashTable<>(size, size, collisionsMax, hashFunction, size, lru);
    for (final KV<K, V> kv : this) {
      result.replace(kv);
    }
    return result.destroy();
  }

  public <R> HashTableImmutable<K, R> toImmutable(final Function<V, R> mapper) {
    final HashTable<K, R> table = new HashTable<>(size, size, collisionsMax, hashFunction, size, lru);
    for (final KV<K, V> kv : this) {
      table.replace(KV.of(kv.key, mapper.apply(kv.value)));
    }
    return table.destroy();
  }

  @Override
  public String toString() {
    return IterableUtils.toString(this);
  }

  public void trim() {
    capacity(size());
  }

  private void trimTo(final int newSize) {
    while (size > newSize) {
      remove(lru ? first.kv.key : last.kv.key);
    }
  }

  private KV<K, V> update(final HashTableNode<K, V> node, final KV<K, V> kv) {
    final KV<K, V> oldKv = node.kv;
    node.kv = kv;
    return oldKv;
  }

  public int version() {
    return version;
  }

  public <E extends Throwable> void walk(final Lambda.Consumer<HashTableCursor<K, V>, E> context) throws E {
    walk(context, size, 0);
  }

  public <E extends Throwable> void walk(final Lambda.Consumer<HashTableCursor<K, V>, E> context, final int limit,
    final int offset) throws E {
    final Ref<Integer> expectedVersionRef = Ref.of(version);
    final HashTableIterator<K, V> iterator = new HashTableIterator<>(first, () -> {
      if (expectedVersionRef.get() != version) {
        throw new ConcurrentModificationException();
      }
    });
    final HashTableCursor<K, V> cursor = new HashTableCursor<>(iterator, limit, offset, kv -> {
      remove(kv.key);
      expectedVersionRef.set(version);
    });
    while (cursor.next()) {
      context.invoke(cursor);
    }
  }

}
