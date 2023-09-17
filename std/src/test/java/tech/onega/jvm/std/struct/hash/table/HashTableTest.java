package tech.onega.jvm.std.struct.hash.table;

import java.util.ConcurrentModificationException;
import org.testng.annotations.Test;
import tech.onega.jvm.std.lang.RandUtils;
import tech.onega.jvm.std.lang.Ref;
import tech.onega.jvm.std.struct.hash.KV;
import tech.onega.jvm.std.validate.Check;

public class HashTableTest {

  @Test
  public void testCapacity() {
    final var table = new HashTable<Integer, Object>(0);
    table.collisions(1.0f);
    Check.isTrue(table.isEmpty());
    Check.isTrue(table.capacity() == 0);
    for (var i = 0; i < 10_000; i++) {
      table.replace(KV.of(i, i));
    }
    Check.isTrue(table.size() == 10_000);
    Check.isTrue(table.capacity() == 16384);
    Check.isTrue(table.collisions() == 10_000f / 16384f);
    //newCollisions = 1f
    table.capacity(100);
    Check.isTrue(table.size() == 10_000);
    Check.isTrue(table.capacity() == 100);
    Check.isTrue(table.collisions() == 100f);
  }

  @Test
  public void testCollisisons() {
    final var table = new HashTable<Integer, Object>(0);
    table.collisions(1.0f);
    Check.isTrue(table.isEmpty());
    Check.isTrue(table.capacity() == 0);
    for (var i = 0; i < 10_000; i++) {
      table.replace(KV.of(i, i));
    }
    Check.isTrue(table.size() == 10_000);
    Check.isTrue(table.capacity() == 16384);
    Check.isTrue(table.collisions() == 10_000f / 16384f);
    //newCollisions = 1f
    table.collisions(1f);
    Check.isTrue(table.size() == 10_000);
    Check.isTrue(table.capacity() == table.size());
    Check.isTrue(table.collisions() == 1f);
  }

  @Test
  public void testCrud() {
    final var table = new HashTable<Integer, Object>();
    Check.isTrue(table.isEmpty());
    Check.equals(table.size(), 0);
    Check.isNull(table.get(RandUtils.randInt()));
    //add 1
    table.replace(KV.of(1, null));
    Check.isFalse(table.isEmpty());
    Check.equals(table.size(), 1);
    Check.isTrue(table.contains(1));
    Check.isTrue(table.get(1).key() == 1);
    //add 2
    table.replace(KV.of(2, null));
    Check.isFalse(table.isEmpty());
    Check.equals(table.size(), 2);
    Check.isTrue(table.contains(2));
    Check.isTrue(table.get(2).key() == 2);
    //add same - 2
    table.replace(KV.of(2, null));
    Check.isFalse(table.isEmpty());
    Check.equals(table.size(), 2);
    //remove 1
    table.remove(1);
    Check.isFalse(table.isEmpty());
    Check.equals(table.size(), 1);
    Check.isFalse(table.contains(1));
    Check.isNull(table.get(1));
    Check.isTrue(table.contains(2));
    Check.isTrue(table.get(2).key() == 2);
    //clear
    table.clear();
    Check.isTrue(table.isEmpty());
    Check.equals(table.size(), 0);
  }

  @Test
  public void testDestroy() {
    final var size = 100;
    final var table = new HashTable<Integer, Object>();
    for (var i = 0; i < size; i++) {
      table.replace(KV.of(i, i));
    }
    Check.equals(table.size(), size);
    final var iTable = table.destroy();
    Check.equals(table.size(), 0);
    Check.equals(iTable.size(), size);
    int pos = 0;
    for (final var kv : iTable) {
      Check.isTrue(kv.key() == pos++);
    }
  }

  @Test
  public void testEquals() {
    final var a = HashTable.<Integer, Integer>of(KV.of(1, 1), KV.of(2, 2));
    final var b = HashTable.<Integer, Integer>of(KV.of(1, 1), KV.of(2, 2));
    Check.equals(a, b);
    Check.isTrue(a.equals(b));
    Check.isTrue(b.equals(a));
    Check.equals(a.hashCode(), b.hashCode());
  }

  @Test
  public void testHighCollisison() {
    final var size = 10_000;
    final var maxCollisions = 10f;
    final var table = new HashTable<Integer, Object>(1, (int) (size / maxCollisions), maxCollisions);
    for (var i = 0; i < size; i++) {
      table.replace(KV.of(i, i));
    }
    Check.equals(table.size(), size);
    for (var i = 0; i < size; i++) {
      table.contains(i);
      table.remove(i);
    }
  }

  @Test
  public void testIterator() {
    final var table = HashTable.<Integer, Integer>of(KV.of(1, 1), KV.of(2, 2));
    final var iterator = table.iterator();
    Check.equals(iterator.next(), KV.of(1, 1));
    table.clear();
    Check.withThrowType(ConcurrentModificationException.class, () -> iterator.hasNext());
  }

  @Test
  public void testOrder() {
    final var size = 100;
    final var table = new HashTable<Integer, Integer>();
    for (var i = 0; i < size; i++) {
      table.replace(KV.of(i, i));
    }
    var pos = 0;
    for (final var kv : table) {
      Check.isTrue(kv.key() == pos++);
    }
  }

  @Test
  public void testSize() {
    final var table = new HashTable<Integer, Object>();
    Check.isTrue(table.isEmpty());
    Check.equals(table.size(), 0);
    for (var i = 0; i < 4; i++) {
      table.replace(KV.of(i, i));
    }
    //lru true
    table.lru(true);
    Check.equals(table.size(), 4);
    table.size(3);
    Check.equals(table.size(), 3);
    Check.equals(table.toArray(), new KV[] { KV.of(1, 1), KV.of(2, 2), KV.of(3, 3) });
    //lru false
    table.lru(false);
    Check.equals(table.size(), 3);
    table.size(2);
    Check.equals(table.size(), 2);
    Check.equals(table.toArray(), new KV[] { KV.of(1, 1), KV.of(2, 2) });
  }

  @Test
  public void testSizeMax() {
    final var table = new HashTable<Integer, Object>();
    Check.isTrue(table.isEmpty());
    Check.equals(table.size(), 0);
    for (var i = 0; i < 4; i++) {
      table.replace(KV.of(i, i));
    }
    table.lru(true);
    Check.equals(table.size(), 4);
    table.sizeMax(1);
    Check.equals(table.size(), 1);
    Check.equals(table.toArray(), new KV[] { KV.of(3, 3) });
    table.replace(KV.of(0, 0));
    Check.equals(table.size(), 1);
    Check.equals(table.toArray(), new KV[] { KV.of(0, 0) });
  }

  @Test
  public void testToImmutable() {
    final var size = 10;
    final var table = new HashTable<Integer, Object>();
    for (var i = 0; i < size; i++) {
      table.replace(KV.of(i, i));
    }
    Check.equals(table.size(), size);
    final var iTable = table.toImmutable();
    Check.equals(table.size(), size);
    Check.equals(iTable.size(), size);
    int pos = 0;
    for (final var kv : iTable) {
      Check.isTrue(kv.key() == pos++);
    }
    Check.equals(iTable.toArray(), table.toArray());
  }

  @Test
  public void testWalk() {
    final var size = 10_000;
    final var table = new HashTable<Integer, Object>();
    for (var i = 0; i < size; i++) {
      table.replace(KV.of(i, null));
    }
    Check.equals(table.size(), size);
    final var counterRef = Ref.<Integer>of(0);
    table.walk(ctx -> {
      counterRef.set(counterRef.get() + 1);
      ctx.remove();
    });
    Check.isTrue(counterRef.get() == size);
    Check.equals(table.size(), 0);
  }

  @Test
  public void testWalkConcurent() {
    final var table = HashTable.<Integer, Object>of(KV.of(1, 1), KV.of(2, 2));
    Check.withThrowType(ConcurrentModificationException.class, () -> table.walk(ctx -> table.clear()));
  }

}
