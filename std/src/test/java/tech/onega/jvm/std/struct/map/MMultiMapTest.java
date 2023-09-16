package tech.onega.jvm.std.struct.map;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import org.testng.annotations.Test;
import tech.onega.jvm.std.codec.json.JsonCodec;
import tech.onega.jvm.std.struct.hash.KV;
import tech.onega.jvm.std.struct.iterator.IteratorUtils;
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.validate.Check;

public class MMultiMapTest {

  private static KV<Integer, Integer> kv(final int v) {
    return KV.of(v, v);
  }

  @Test
  public void testAdd() {
    final var map = MMultiMap.<Integer, Integer>create(10, 10);
    Check.equals(map.size(), 0);
    Check.equals(map.toArray(), new Object[] {});
    map.add(kv(1));
    Check.equals(map.size(), 1);
    Check.equals(map.toArray(), new Object[] { kv(1) });
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testAddAll() {
    Check.equals(MMultiMap.<Integer, Integer>create().addAll(kv(1), kv(2), kv(3)),
      MMultiMap.of(kv(1), kv(2), kv(3)));
    Check.equals(MMultiMap.create().addAll(new KV[] { kv(1), kv(2), kv(3) }, 2), MMultiMap.of(kv(1), kv(2)));
    Check.equals(MMultiMap.create().add(1, 1).toArray(), new Object[] { KV.of(1, 1) });
    Check.equals(
      MMultiMap.<Integer, Integer>create().addAll(Arrays.asList(kv(1), kv(2), kv(3)).stream()),
      MMultiMap.of(kv(1), kv(2), kv(3)));
    final var src = MMultiMap.<Integer, Integer>create();
    final var in = MMultiMap.<Integer, Integer>of(kv(1), kv(2), kv(3), kv(4), kv(5));
    src.addAll(in);
    Check.equals(src, MMultiMap.of(kv(1), kv(2), kv(3), kv(4), kv(5)));
  }

  @Test
  public void testAddAll2() {
    final var kvA = KV.of("10", "10");
    final var kvB = KV.of("10", "11");
    final var mapA = MMultiMap.<String, String>of(kvA, kvB);
    final var mapB = MMultiMap.<String, String>create();
    mapB.addAll(mapA);
    Check.isTrue(mapA.contains(kvA));
    Check.isTrue(mapA.contains(kvB));
    Check.isTrue(mapB.contains(kvA));
    Check.isTrue(mapB.contains(kvB));
    Check.equals(mapA, mapB);
  }

  @Test
  public void testAddStreamEntry() {
    final var tmp = new LinkedHashMap<Integer, Integer>(3);
    tmp.put(1, 1);
    tmp.put(2, 2);
    tmp.put(3, 3);
    Check.equals(
      MMultiMap.<Integer, Integer>create().addStreamEntry(tmp.entrySet().stream()),
      MMultiMap.of(kv(1), kv(2), kv(3)));
  }

  @Test
  public void testAsCollection() {
    Check.equals(MMultiMap.of(kv(1), kv(2), kv(3)).asCollection(), Arrays.asList(kv(1), kv(2), kv(3)));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testCapacity() {
    Check.equals(MMultiMap.of(kv(1), kv(2), kv(3)).capacity(1), MMultiMap.of(kv(1)));
    Check.equals(MMultiMap.create(3).capacity(), 3);
    Check.equals(MMultiMap.<Integer, Integer>create(3).addAll(kv(1), kv(2), kv(3)).capacity(), 3);
    Check.equals(MMultiMap.create(2, 4).capacity(), 2);
    Check.equals(MMultiMap.create(2, 4).capacity(4).capacity(), 4);
    Check.withThrowType(Throwable.class, () -> MMultiMap.create(2, 4).capacity(8));
  }

  @Test
  public void testClear() {
    final var map = MMultiMap.of(kv(1), kv(2), kv(3));
    Check.isFalse(map.isEmpty());
    Check.equals(map.size(), 3);
    map.clear();
    Check.isTrue(map.isEmpty());
    Check.equals(map.size(), 0);
  }

  @Test
  public void testCollector() {
    Check.equals(
      MMultiMap.of(kv(1), kv(2), kv(3))
        .stream()
        .collect(MMultiMap.collector()),
      MMultiMap.of(kv(1), kv(2), kv(3)));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testConstructorRange() {
    Check.equals(
      MMultiMap.of(new KV[] { kv(1), kv(2), kv(3), kv(4), kv(5) }, 2, 2),
      MMultiMap.of(kv(3), kv(4)));
  }

  @Test
  public void testContains() {
    final var map = MMultiMap.of(kv(1), kv(2), kv(3));
    Check.isTrue(map.contains(kv(1)));
    Check.isFalse(map.contains(kv(0)));
  }

  @Test
  public void testContainsKey() {
    final var map = MMultiMap.of(kv(1), kv(2), kv(3), kv(4), kv(5), kv(6));
    Check.isTrue(map.containsKey(1));
    Check.isFalse(map.containsKey(0));
  }

  @Test
  public void testContainsValue() {
    final var map = MMultiMap.of(kv(1), kv(2), kv(3), kv(4), kv(5), kv(6));
    Check.isTrue(map.containsValue(1));
    Check.isFalse(map.containsValue(0));
  }

  @Test
  public void testCopy() {
    final var map = MMultiMap.of(kv(1), kv(2), kv(3));
    final var copy = map.stream().collect(MMultiMap.collector());
    Check.isTrue(map != copy);
    Check.equals(map, copy);
  }

  @Test
  public void testCopyLimitOffset() {
    final var map = MMultiMap.of(kv(1), kv(2), kv(3), kv(4), kv(5), kv(6));
    final var copy = map.stream().skip(1).limit(3).collect(MMultiMap.collector());
    Check.isTrue(map != copy);
    Check.equals(copy, MMultiMap.of(kv(2), kv(3), kv(4)));
  }

  @Test
  public void testCopyToList() {
    Check.equals(MMultiMap.of(kv(1), kv(2), kv(3)).stream().collect(Collectors.toList()),
      Arrays.asList(kv(1), kv(2), kv(3)));
  }

  @Test
  public void testCopyWithComparator() {
    final var map = MMultiMap.of(kv(3), kv(2), kv(1));
    final var copy = map.stream().sorted((a, b) -> Integer.compare(a.key(), b.key()))
      .collect(MMultiMap.collector());
    Check.equals(copy, MMultiMap.of(kv(1), kv(2), kv(3)));
  }

  @Test
  public void testCopyWithFilter() {
    final var map = MMultiMap.of(kv(1), kv(2), kv(3));
    final var copy = map.stream().filter(a -> a.key() > 2).collect(MMultiMap.collector());
    Check.equals(copy, MMultiMap.of(kv(3)));
  }

  @Test
  public void testCopyWithMap() {
    final var map = MMultiMap.of(kv(1), kv(2), kv(3));
    final var copy = map.stream().map(kv -> KV.of(kv.key(), kv.value().toString()))
      .collect(MMultiMap.collector());
    Check.equals(copy, MMultiMap.of(KV.of(1, "1"), KV.of(2, "2"), KV.of(3, "3")));
  }

  @Test
  public void testEmpty() {
    Check.isTrue(MMultiMap.create().isEmpty());
    Check.equals(MMultiMap.create().size(), 0);
    Check.isFalse(MMultiMap.create() == MMultiMap.create());
    Check.equals(MMultiMap.create(), MMultiMap.create());
    Check.isTrue(MMultiMap.of().isEmpty());
    Check.equals(MMultiMap.of().size(), 0);
    Check.isFalse(MMultiMap.of() == MMultiMap.of());
    Check.equals(MMultiMap.of(), MMultiMap.of());
    Check.isFalse(MMultiMap.create() == MMultiMap.of());
    Check.equals(MMultiMap.create(), MMultiMap.of());
    Check.isFalse(MMultiMap.of(kv(1)).isEmpty());
    Check.equals(MMultiMap.of(kv(1)).size(), 1);
  }

  @Test
  public void testEquals() {
    Check.equals(MMultiMap.of(kv(1)), MMultiMap.of(kv(1)));
    Check.isTrue(MMultiMap.of(kv(1)).equals(MMultiMap.of(kv(1))));
    Check.isTrue(MMultiMap.of(kv(1), kv(2), kv(3)).equals(MMultiMap.of(kv(1), kv(2), kv(3))));
    Check.isFalse(MMultiMap.of(kv(1), kv(2), kv(3)).equals(MMultiMap.of(kv(1), kv(2))));
    Check.isFalse(MMultiMap.of(kv(1), kv(2), kv(3)).equals(MMultiMap.of(kv(1), kv(2), kv(4))));
  }

  @Test
  public void testFirst() {
    Check.isTrue(MMultiMap.of(kv(1), kv(2), kv(3)).first(1) == 1);
  }

  @Test
  public void testFull() {
    final var size = 10;
    final var map = MMultiMap.<Integer, Integer>create(1, size);
    Check.isTrue(map.isEmpty());
    var i = 0;
    while (!map.isFull()) {
      map.add(kv(i++));
    }
    Check.equals(map.size(), size);
    Check.equals(map.capacity(), size);
    Check.isTrue(map.isFull());
  }

  @Test
  public void testGet() {
    Check.isTrue(MMultiMap.of(kv(1), kv(2)).get(1).first() == 1);
  }

  @Test
  public void testHashCode() {
    final var empty = MMultiMap.create();
    final var a = MMultiMap.of(kv(1));
    final var b = MMultiMap.of(kv(2));
    Check.equals(empty.hashCode(), 0);
    Check.notEquals(a.hashCode(), 0);
    Check.notEquals(b.hashCode(), 0);
    Check.notEquals(a.hashCode(), b.hashCode());
  }

  @Test
  public void testHashCodeMutable() {
    final var map = MMultiMap.<Integer, Integer>create();
    Check.equals(map.hashCode(), 0);
    map.add(kv(1));
    final int hashCodeA = map.hashCode();
    map.add(kv(2));
    final int hashCodeB = map.hashCode();
    Check.notEquals(hashCodeA, 0);
    Check.notEquals(hashCodeB, 0);
    Check.notEquals(hashCodeA, hashCodeB);
  }

  @Test
  public void testIterator() {
    final var map = MMultiMap.of(kv(1), kv(2), kv(3), kv(4), kv(5), kv(6));
    Check.equals(IteratorUtils.toArray(map.iterator()),
      new Object[] { kv(1), kv(2), kv(3), kv(4), kv(5), kv(6) });
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testJson() {
    final var data = MMultiMap.of(KV.of("a", 1), KV.of("a", 2), KV.of("c", IList.of(1, 2, 3)));
    final var json = JsonCodec.toString(data);
    final var dData = JsonCodec.parse(json, MMultiMap.class);
    Check.equals(dData.get("a"), data.get("a"));
    Check.equals(data, JsonCodec.parse(json, MMultiMap.class));
    final var fromJson = JsonCodec.parse(JsonCodec.toString(
      MMultiMap.of(
        KV.of(1, 10),
        KV.of(1, 20),
        KV.of(3, 30))),
      MMultiMap.class, Double.class, String.class);
    Check.equals(fromJson, MMultiMap.of(KV.of(1D, "10"), KV.of(1D, "20"), KV.of(3D, "30")));
  }

  @Test
  public void testKeyMultiValues() {
    Check.equals(
      MMultiMap.of(kv(1), kv(2), kv(3), kv(1), kv(2), kv(3)).keyMultiValues(),
      IList.of(
        KV.of(1, IList.of(1, 1)),
        KV.of(2, IList.of(2, 2)),
        KV.of(3, IList.of(3, 3))));
  }

  @Test
  public void testKeys() {
    final var map = MMultiMap.of(kv(1), kv(2), kv(3), kv(4), kv(5), kv(6));
    Check.equals(map.keys().toArray(), new Object[] { 1, 2, 3, 4, 5, 6 });
  }

  @Test
  public void testKeySize() {
    Check.equals(MMultiMap.of(kv(1), kv(2), kv(3), kv(1), kv(2), kv(3)).keysSize(), 3);
  }

  @Test
  public void testKeyValues() {
    final var map = MMultiMap.of(kv(1), kv(2), kv(3), kv(4), kv(5), kv(6));
    Check.equals(map.keyValues().toArray(), new Object[] { kv(1), kv(2), kv(3), kv(4), kv(5), kv(6) });
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testOutOfMemmory() {
    final var size = 10;
    final var map = MMultiMap.<Integer, Integer>create(size, size);
    final var tmp = new KV[size + 1];
    for (var i = 0; i < tmp.length; i++) {
      tmp[i] = KV.of(i, i);
    }
    Check.withThrowType(Throwable.class, () -> map.addAll(tmp));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testRemove() {
    final var map = MMultiMap.<Integer, Integer>create(6, 6);
    map.addAll(kv(1), kv(2), kv(3), kv(4), kv(5), kv(6));
    Check.equals(map.size(), 6);
    Check.equals(map.maxCapacity(), 6);
    Check.isTrue(map.equals(MMultiMap.of(kv(1), kv(2), kv(3), kv(4), kv(5), kv(6))));
    map.remove(kv(3));
    Check.equals(map.size(), 5);
    Check.isTrue(map.equals(MMultiMap.of(kv(1), kv(2), kv(4), kv(5), kv(6))));
    Check.equals(map.maxCapacity(), 6);
    Check.isTrue(map.equals(MMultiMap.of(kv(1), kv(2), kv(4), kv(5), kv(6))));
    map.remove(kv(1)).remove(kv(2)).remove(kv(4)).remove(kv(5)).removeKey(6);
    Check.isTrue(map.isEmpty());
  }

  @Test
  public void testRemoveKey() {
    Check.equals(
      MMultiMap.of(kv(1), kv(2), kv(3), kv(1), kv(2), kv(3)).removeKey(1).keyMultiValues(),
      IList.of(
        KV.of(2, IList.of(2, 2)),
        KV.of(3, IList.of(3, 3))));
  }

  @Test
  public void testRemoveKV() {
    Check.equals(
      MMultiMap.of(kv(1), kv(2), kv(3), kv(1), kv(2), kv(3)).remove(KV.of(1, 1)).keyMultiValues(),
      IList.of(
        KV.of(1, IList.of(1)),
        KV.of(2, IList.of(2, 2)),
        KV.of(3, IList.of(3, 3))));
  }

  @Test
  public void testResize() {
    final var map = MMultiMap.<Integer, Integer>of(kv(1), kv(2), kv(3), kv(4), kv(5));
    Check.equals(map.size(), 5);
    map.size(2);
    Check.equals(map.size(), 2);
    Check.isTrue(map.equals(MMultiMap.of(kv(1), kv(2))));
  }

  @Test
  public void testSize() {
    Check.equals(MMultiMap.of(kv(1), kv(2), kv(3), kv(1), kv(2), kv(3)).size(), 6);
  }

  @Test
  public void testSort() {
    Check.isTrue(MMultiMap.of(kv(3), kv(2), kv(1))
      .sort((a, b) -> Integer.compare(a.key(), b.key()))
      .equals(MMultiMap.of(kv(1), kv(2), kv(3))));
  }

  @Test
  public void testToArray() {
    Check.equals(MMultiMap.of(kv(1), kv(2), kv(3)).toArray(), new Object[] { kv(1), kv(2), kv(3) });
  }

  @Test
  public void testToIList() {
    Check.equals(MMultiMap.of(kv(1)).toIList(), IList.of(kv(1)));
  }

  @Test
  public void testToString() {
    Check.equals("[1:[1],2:[2],3:[3]]", MMultiMap.of(kv(1), kv(2), kv(3)).toString());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testTrim() {
    final var map = MMultiMap.<Integer, Integer>create(100, 100);
    map.addAll(kv(1), kv(2), kv(3));
    Check.equals(map.capacity(), 100);
    Check.equals(map.size(), 3);
    map.trim();
    Check.equals(map.capacity(), map.size());
    Check.equals(map, MMultiMap.of(kv(1), kv(2), kv(3)));
  }

  @Test
  public void testUnique() {
    Check.equals(
      MMultiMap.of(kv(1), kv(2), kv(3), kv(4), kv(5), kv(1), kv(2), kv(3)).keys(),
      MMultiMap.of(kv(1), kv(2), kv(3), kv(4), kv(5)).keys());
  }

  @Test
  public void testValues() {
    final var map = MMultiMap.<Integer, Integer>of(kv(1), kv(2), kv(3), kv(4), kv(5), kv(6));
    Check.equals(map.values().toArray(), new Object[] { 1, 2, 3, 4, 5, 6 });
  }

}
