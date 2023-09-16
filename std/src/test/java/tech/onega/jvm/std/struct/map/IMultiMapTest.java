package tech.onega.jvm.std.struct.map;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.testng.annotations.Test;
import tech.onega.jvm.std.codec.json.JsonCodec;
import tech.onega.jvm.std.struct.hash.KV;
import tech.onega.jvm.std.struct.iterator.IteratorUtils;
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.validate.Check;

public class IMultiMapTest {

  private static KV<Integer, Integer> kv(final int v) {
    return KV.of(v, v);
  }

  @Test
  public void testAsCollection() {
    Check.equals(IMultiMap.of(kv(1)).asCollection(), Arrays.asList(kv(1)));
  }

  @Test
  public void testCollector() {
    Check.equals(IMultiMap.of(kv(1), kv(2), kv(3)).stream().collect(IMultiMap.collector()),
      IMultiMap.of(kv(1), kv(2), kv(3)));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testConstructorRange() {
    Check.equals(IMultiMap.of(new KV[] { kv(1), kv(2), kv(3), kv(4), kv(5) }, 2, 2),
      IMultiMap.of(kv(3), kv(4)));
  }

  @Test
  public void testContains() {
    final var map = IMultiMap.of(kv(1), kv(2), kv(3));
    Check.isTrue(map.contains(kv(1)));
    Check.isFalse(map.contains(kv(0)));
  }

  @Test
  public void testContainsKey() {
    final var map = IMultiMap.of(kv(1), kv(2), kv(3), kv(4), kv(5), kv(6));
    Check.isTrue(map.containsKey(1));
    Check.isFalse(map.containsKey(0));
  }

  @Test
  public void testContainsValue() {
    final var map = IMultiMap.of(kv(1), kv(2), kv(3), kv(4), kv(5), kv(6));
    Check.isTrue(map.containsValue(1));
    Check.isFalse(map.containsValue(0));
  }

  @Test
  public void testCopy() {
    final var map = IMultiMap.of(kv(1), kv(2), kv(3));
    final var copy = map.stream().collect(IMultiMap.collector());
    Check.isTrue(map != copy);
    Check.equals(map, copy);
  }

  @Test
  public void testCopyLimitOffset() {
    final var map = IMultiMap.of(kv(1), kv(2), kv(3), kv(4), kv(5), kv(6));
    final var copy = map.stream().skip(1).limit(3).collect(IMultiMap.collector());
    Check.isTrue(map != copy);
    Check.equals(copy, IMultiMap.of(kv(2), kv(3), kv(4)));
  }

  @Test
  public void testCopyToList() {
    Check.equals(IMultiMap.of(kv(1), kv(2), kv(3)).stream().collect(Collectors.toList()),
      Arrays.asList(kv(1), kv(2), kv(3)));
  }

  @Test
  public void testCopyWithComparator() {
    final var map = IMultiMap.<Integer, Integer>of(kv(3), kv(2), kv(1));
    final var copy = map.stream().sorted((a, b) -> Integer.compare(a.key(), b.key()))
      .collect(IMultiMap.collector());
    Check.equals(copy, IMultiMap.of(kv(1), kv(2), kv(3)));
  }

  @Test
  public void testCopyWithFilter() {
    final var map = IMultiMap.<Integer, Integer>of(kv(1), kv(2), kv(3));
    final var copy = map.stream().filter(a -> a.key() > 2).collect(IMultiMap.collector());
    Check.equals(copy, IMultiMap.of(kv(3)));
  }

  @Test
  public void testCopyWithMap() {
    final var map = IMultiMap.<Integer, Integer>of(kv(1), kv(2), kv(3));
    final var copy = map.stream().map(kv -> KV.of(kv.key(), kv.value().toString()))
      .collect(IMultiMap.collector());
    Check.equals(copy, IMultiMap.of(KV.of(1, "1"), KV.of(2, "2"), KV.of(3, "3")));
  }

  @Test
  public void testEmpty() {
    Check.isTrue(IMultiMap.empty().isEmpty());
    Check.equals(IMultiMap.empty().size(), 0);
    Check.isTrue(IMultiMap.empty() == IMultiMap.empty());
    Check.equals(IMultiMap.empty(), IMultiMap.empty());
    Check.isTrue(IMultiMap.of().isEmpty());
    Check.equals(IMultiMap.of().size(), 0);
    Check.isTrue(IMultiMap.of() == IMultiMap.of());
    Check.equals(IMultiMap.of(), IMultiMap.of());
    Check.isTrue(IMultiMap.empty() == IMultiMap.of());
    Check.equals(IMultiMap.empty(), IMultiMap.of());
    Check.isFalse(IMultiMap.of(kv(1)).isEmpty());
    Check.equals(IMultiMap.of(kv(1)).size(), 1);
  }

  @Test
  public void testEquals() {
    Check.equals(IMultiMap.of(kv(1)), IMultiMap.of(kv(1)));
    Check.isTrue(IMultiMap.of(kv(1)).equals(IMultiMap.of(kv(1))));
    Check.isTrue(IMultiMap.of(kv(1), kv(2), kv(3)).equals(IMultiMap.of(kv(1), kv(2), kv(3))));
    Check.isFalse(IMultiMap.of(kv(1), kv(2), kv(3)).equals(IMultiMap.of(kv(1), kv(2))));
    Check.isFalse(IMultiMap.of(kv(1), kv(2), kv(3)).equals(IMultiMap.of(kv(1), kv(2), kv(4))));
  }

  @Test
  public void testFirst() {
    Check.isTrue(IMultiMap.of(kv(1), kv(2), kv(3)).first(1) == 1);
  }

  @Test
  public void testGet() {
    Check.isTrue(IMultiMap.of(kv(1)).get(1).first() == 1);
  }

  @Test
  public void testHashCode() {
    final var empty = IMultiMap.empty();
    final var a = IMultiMap.of(kv(1));
    final var b = IMultiMap.of(kv(2));
    Check.equals(empty.hashCode(), 0);
    Check.notEquals(a.hashCode(), 0);
    Check.notEquals(b.hashCode(), 0);
    Check.notEquals(a.hashCode(), b.hashCode());
  }

  @Test
  public void testIterator() {
    final var map = IMultiMap.of(kv(1), kv(2), kv(3), kv(4), kv(5), kv(6));
    Check.equals(IteratorUtils.toArray(map.iterator()),
      new Object[] { kv(1), kv(2), kv(3), kv(4), kv(5), kv(6) });
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testJson() {
    final var data = IMultiMap.<Object, Object>of(KV.of("a", 1), KV.of("a", 2), KV.of("c", IList.of(1, 2, 3)));
    final var json = JsonCodec.toString(data);
    Check.equals(json, "{\"a\":[1,2],\"c\":[[1,2,3]]}");
    final var dData = JsonCodec.parse(json, IMultiMap.class);
    Check.equals(dData.get("a"), data.get("a"));
    Check.equals(data, JsonCodec.parse(json, IMultiMap.class));
    final var fromJson = JsonCodec.parse(JsonCodec.toString(
      IMultiMap.of(
        KV.of(1, 10),
        KV.of(1, 20),
        KV.of(3, 30))),
      IMultiMap.class, Double.class, String.class);
    Check.equals(fromJson, IMultiMap.of(KV.of(1D, "10"), KV.of(1D, "20"), KV.of(3D, "30")));
  }

  @Test
  public void testKeyMultiValues() {
    Check.equals(
      IMultiMap.of(kv(1), kv(2), kv(3), kv(1), kv(2), kv(3)).keyMultiValues(),
      IList.of(
        KV.of(1, IList.of(1, 1)),
        KV.of(2, IList.of(2, 2)),
        KV.of(3, IList.of(3, 3))));
  }

  @Test
  public void testKeys() {
    final var map = IMultiMap.of(kv(1), kv(2), kv(3), kv(4), kv(5), kv(6));
    Check.equals(map.keys().toArray(), new Object[] { 1, 2, 3, 4, 5, 6 });
  }

  @Test
  public void testKeySize() {
    Check.equals(IMultiMap.of(kv(1), kv(2), kv(3), kv(1), kv(2), kv(3)).keysSize(), 3);
  }

  @Test
  public void testKeyValues() {
    final var map = IMultiMap.of(kv(1), kv(2), kv(3), kv(4), kv(5), kv(6));
    Check.equals(map.keyValues().toArray(), new Object[] { kv(1), kv(2), kv(3), kv(4), kv(5), kv(6) });
  }

  @Test
  public void testSize() {
    Check.equals(IMultiMap.of(kv(1), kv(2), kv(3), kv(1), kv(2), kv(3)).size(), 6);
  }

  @Test
  public void testToArray() {
    Check.equals(IMultiMap.of(kv(1), kv(2), kv(3)).toArray(), new Object[] { kv(1), kv(2), kv(3) });
  }

  @Test
  public void testToIList() {
    Check.equals(IMultiMap.of(kv(1)).toIList(), IList.of(kv(1)));
  }

  @Test
  public void testToString() {
    Check.equals("[1:[1],2:[2],3:[3]]", IMultiMap.of(kv(1), kv(2), kv(3)).toString());
  }

  @Test
  public void testUnique() {
    Check.equals(
      IMultiMap.of(kv(1), kv(2), kv(3), kv(4), kv(5), kv(1), kv(2), kv(3)).keys(),
      IMultiMap.of(kv(1), kv(2), kv(3), kv(4), kv(5)).keys());
  }

  @Test
  public void testValues() {
    final var map = IMultiMap.of(kv(1), kv(2), kv(3), kv(4), kv(5), kv(6));
    Check.equals(map.values().toArray(), new Object[] { 1, 2, 3, 4, 5, 6 });
  }

}
