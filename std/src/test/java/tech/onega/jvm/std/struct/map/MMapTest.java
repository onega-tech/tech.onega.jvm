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

public class MMapTest {

  @Test
  public void testAdd() {
    final var map = MMap.create(10, 10);
    Check.equals(map.size(), 0);
    Check.equals(map.toArray(), new Object[] {});
    map.add(KV.of(1, 1));
    Check.equals(map.size(), 1);
    Check.equals(map.toArray(), new Object[] { KV.of(1, 1) });
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testAddAll() {
    Check.equals(MMap.<Integer, Integer>create().addAll(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)),
      MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)));
    Check.equals(MMap.create().addAll(new KV[] { KV.of(1, 1), KV.of(2, 2), KV.of(3, 3) }, 2),
      MMap.of(KV.of(1, 1), KV.of(2, 2)));
    Check.equals(MMap.create().add(1, 1).toArray(), new Object[] { KV.of(1, 1) });
    Check.equals(
      MMap.<Integer, Integer>create().addAll(Arrays.asList(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)).stream()),
      MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)));
    final var src = MMap.<Integer, Integer>create();
    final var in = MMap.<Integer, Integer>of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3), KV.of(4, 4), KV.of(5, 5));
    src.addAll(in);
    Check.equals(src, MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3), KV.of(4, 4), KV.of(5, 5)));
  }

  @Test
  public void testAddStreamEntry() {
    final var tmp = new LinkedHashMap<Integer, Integer>(3);
    tmp.put(1, 1);
    tmp.put(2, 2);
    tmp.put(3, 3);
    Check.equals(MMap.<Integer, Integer>create().addStreamEntry(tmp.entrySet().stream()),
      MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)));
  }

  @Test
  public void testAsCollection() {
    Check.equals(MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)).asCollection(),
      Arrays.asList(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testCapacity() {
    Check.equals(MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)).capacity(1), MMap.of(KV.of(1, 1)));
    Check.equals(MMap.create(3).capacity(), 3);
    Check.equals(MMap.<Integer, Integer>create(3).addAll(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)).capacity(), 3);
    Check.equals(MMap.create(2, 4).capacity(), 2);
    Check.equals(MMap.create(2, 4).capacity(4).capacity(), 4);
    Check.withThrowType(Throwable.class, () -> MMap.create(2, 4).capacity(8));
  }

  @Test
  public void testClear() {
    final var map = MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3));
    Check.isFalse(map.isEmpty());
    Check.equals(map.size(), 3);
    map.clear();
    Check.isTrue(map.isEmpty());
    Check.equals(map.size(), 0);
  }

  @Test
  public void testCollector() {
    Check.equals(MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)).stream().collect(MMap.collector()),
      MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testConstructorRange() {
    Check.equals(MMap.of(new KV[] { KV.of(1, 1), KV.of(2, 2), KV.of(3, 3), KV.of(4, 4), KV.of(5, 5) }, 2, 2),
      MMap.of(KV.of(3, 3), KV.of(4, 4)));
  }

  @Test
  public void testContains() {
    final var map = MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3));
    Check.isTrue(map.contains(KV.of(1, 1)));
    Check.isFalse(map.contains(KV.of(0, 0)));
  }

  @Test
  public void testContainsKey() {
    final var map = MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3), KV.of(4, 4), KV.of(5, 5),
      KV.of(6, 6));
    Check.isTrue(map.containsKey(1));
    Check.isFalse(map.containsKey(0));
  }

  @Test
  public void testContainsValue() {
    final var map = MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3), KV.of(4, 4), KV.of(5, 5),
      KV.of(6, 6));
    Check.isTrue(map.containsValue(1));
    Check.isFalse(map.containsValue(0));
  }

  @Test
  public void testCopy() {
    final var map = MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3));
    final var copy = map.stream().collect(MMap.collector());
    Check.isTrue(map != copy);
    Check.equals(map, copy);
  }

  @Test
  public void testCopyLimitOffset() {
    final var map = MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3), KV.of(4, 4), KV.of(5, 5),
      KV.of(6, 6));
    final var copy = map.stream().skip(1).limit(3).collect(MMap.collector());
    Check.isTrue(map != copy);
    Check.equals(copy, MMap.of(KV.of(2, 2), KV.of(3, 3), KV.of(4, 4)));
  }

  @Test
  public void testCopyToList() {
    Check.equals(MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)).stream().collect(Collectors.toList()),
      Arrays.asList(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)));
  }

  @Test
  public void testCopyWithComparator() {
    final var map = MMap.of(KV.of(3, 3), KV.of(2, 2), KV.of(1, 1));
    final var copy = map.stream().sorted((a, b) -> Integer.compare(a.key(), b.key()))
      .collect(MMap.collector());
    Check.equals(copy, MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)));
  }

  @Test
  public void testCopyWithFilter() {
    final var map = MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3));
    final var copy = map.stream().filter(a -> a.key() > 2).collect(MMap.collector());
    Check.equals(copy, MMap.of(KV.of(3, 3)));
  }

  @Test
  public void testCopyWithMap() {
    final var map = MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3));
    final var copy = map.stream().map(kv -> KV.of(kv.key(), kv.value().toString()))
      .collect(MMap.collector());
    Check.equals(copy, MMap.of(KV.of(1, "1"), KV.of(2, "2"), KV.of(3, "3")));
  }

  @Test
  public void testEmpty() {
    Check.isTrue(MMap.create().isEmpty());
    Check.equals(MMap.create().size(), 0);
    Check.isFalse(MMap.create() == MMap.create());
    Check.equals(MMap.create(), MMap.create());
    Check.isTrue(MMap.of().isEmpty());
    Check.equals(MMap.of().size(), 0);
    Check.isFalse(MMap.of() == MMap.of());
    Check.equals(MMap.of(), MMap.of());
    Check.isFalse(MMap.create() == MMap.of());
    Check.equals(MMap.create(), MMap.of());
    Check.isFalse(MMap.of(KV.of(1, 1)).isEmpty());
    Check.equals(MMap.of(KV.of(1, 1)).size(), 1);
  }

  @Test
  public void testEquals() {
    Check.equals(MMap.of(KV.of(1, 1)), MMap.of(KV.of(1, 1)));
    Check.isTrue(MMap.of(KV.of(1, 1)).equals(MMap.of(KV.of(1, 1))));
    Check.isTrue(
      MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)).equals(MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3))));
    Check.isFalse(MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)).equals(MMap.of(KV.of(1, 1), KV.of(2, 2))));
    Check.isFalse(
      MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)).equals(MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(4, 4))));
  }

  @Test
  public void testFull() {
    final var size = 10;
    final var map = MMap.create(1, size);
    Check.isTrue(map.isEmpty());
    int i = 0;
    while (!map.isFull()) {
      map.add(KV.of(i, i));
      i++;
    }
    Check.equals(map.size(), size);
    Check.equals(map.capacity(), size);
    Check.isTrue(map.isFull());
  }

  @Test
  public void testGet() {
    Check.isTrue(MMap.of(KV.of(1, 1), KV.of(2, 2)).get(1) == 1);
  }

  @Test
  public void testHashCode() {
    final var empty = MMap.create();
    final var a = MMap.of(KV.of(1, 1));
    final var b = MMap.of(KV.of(2, 2));
    Check.equals(empty.hashCode(), 0);
    Check.notEquals(a.hashCode(), 0);
    Check.notEquals(b.hashCode(), 0);
    Check.notEquals(a.hashCode(), b.hashCode());
  }

  @Test
  public void testHashCodeMutable() {
    final var map = MMap.create();
    Check.equals(map.hashCode(), 0);
    map.add(KV.of(1, 1));
    final var hashCodeA = map.hashCode();
    map.add(KV.of(2, 2));
    final var hashCodeB = map.hashCode();
    Check.notEquals(hashCodeA, 0);
    Check.notEquals(hashCodeB, 0);
    Check.notEquals(hashCodeA, hashCodeB);
  }

  @Test
  public void testIterator() {
    final var map = MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3), KV.of(4, 4), KV.of(5, 5),
      KV.of(6, 6));
    Check.equals(IteratorUtils.toArray(map.iterator()),
      new Object[] { KV.of(1, 1), KV.of(2, 2), KV.of(3, 3), KV.of(4, 4), KV.of(5, 5), KV.of(6, 6) });
  }

  @Test
  public void testJson() {
    final var data = MMap.of(KV.of("a", 1), KV.of("b", 2), KV.of("c", IList.of(1, 2, 3)));
    final var json = JsonCodec.toString(data);
    Check.equals(json, "{\"a\":1,\"b\":2,\"c\":[1,2,3]}");
    Check.equals(data, JsonCodec.parse(json, MMap.class));
    final var fromJson = JsonCodec.parse(JsonCodec.toString(MMap.of(KV.of(1, 10), KV.of(2, 20), KV.of(3, 30))), MMap.class,
      Double.class, String.class);
    Check.equals(fromJson, MMap.of(KV.of(1D, "10"), KV.of(2D, "20"), KV.of(3D, "30")));
  }

  @Test
  public void testKeys() {
    final var map = MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3), KV.of(4, 4), KV.of(5, 5),
      KV.of(6, 6));
    Check.equals(map.keys().toArray(), new Object[] { 1, 2, 3, 4, 5, 6 });
  }

  @Test
  public void testKeyValues() {
    final var map = MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3), KV.of(4, 4), KV.of(5, 5),
      KV.of(6, 6));
    Check.equals(map.keyValues().toArray(),
      new Object[] { KV.of(1, 1), KV.of(2, 2), KV.of(3, 3), KV.of(4, 4), KV.of(5, 5), KV.of(6, 6) });
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testOutOfMemmory() {
    final var size = 10;
    final var map = MMap.create(size, size);
    final var tmp = new KV[size + 1];
    for (var i = 0; i < tmp.length; i++) {
      tmp[i] = KV.of(i, i);
    }
    Check.withThrowType(Throwable.class, () -> map.addAll(tmp));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testRemove() {
    final var map = MMap.create(6, 6);
    map.addAll(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3), KV.of(4, 4), KV.of(5, 5), KV.of(6, 6));
    Check.equals(map.size(), 6);
    Check.equals(map.maxCapacity(), 6);
    Check.isTrue(map.equals(MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3), KV.of(4, 4), KV.of(5, 5), KV.of(6, 6))));
    map.remove(KV.of(3, 3));
    Check.equals(map.size(), 5);
    Check.equals(map.maxCapacity(), 6);
    Check.isTrue(map.equals(MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(4, 4), KV.of(5, 5), KV.of(6, 6))));
    map.remove(KV.of(1, 1)).remove(KV.of(2, 2)).remove(KV.of(4, 4)).remove(KV.of(5, 5)).removeKey(6);
    Check.isTrue(map.isEmpty());
  }

  @Test
  public void testResize() {
    final var map = MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3), KV.of(4, 4), KV.of(5, 5));
    Check.equals(map.size(), 5);
    map.size(2);
    Check.equals(map.size(), 2);
    Check.isTrue(map.equals(MMap.of(KV.of(1, 1), KV.of(2, 2))));
  }

  @Test
  public void testSort() {
    Check.isTrue(MMap.of(KV.of(3, 3), KV.of(2, 2), KV.of(1, 1))
      .sort((a, b) -> Integer.compare(a.key(), b.key()))
      .equals(MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3))));
  }

  @Test
  public void testToArray() {
    Check.equals(MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)).toArray(),
      new Object[] { KV.of(1, 1), KV.of(2, 2), KV.of(3, 3) });
  }

  @Test
  public void testToIList() {
    Check.equals(MMap.of(KV.of(1, 1)).toIList(), IList.of(KV.of(1, 1)));
  }

  @Test
  public void testToString() {
    Check.equals("[1:1,2:2,3:3]", MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)).toString());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testTrim() {
    final var map = MMap.create(100, 100);
    map.addAll(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3));
    Check.equals(map.capacity(), 100);
    Check.equals(map.size(), 3);
    map.trim();
    Check.equals(map.capacity(), map.size());
    Check.equals(map, MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)));
  }

  @Test
  public void testUnique() {
    Check.equals(
      MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3), KV.of(4, 4), KV.of(5, 5), KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)),
      MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3), KV.of(4, 4), KV.of(5, 5)));
  }

  @Test
  public void testValues() {
    final var map = MMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3), KV.of(4, 4), KV.of(5, 5),
      KV.of(6, 6));
    Check.equals(map.values().toArray(), new Object[] { 1, 2, 3, 4, 5, 6 });
  }

}
