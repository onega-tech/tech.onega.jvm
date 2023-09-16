package tech.onega.jvm.std.struct.map;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.testng.annotations.Test;
import tech.onega.jvm.std.codec.json.JsonCodec;
import tech.onega.jvm.std.struct.hash.KV;
import tech.onega.jvm.std.struct.iterator.IteratorUtils;
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.validate.Check;

public class IMapTest {

  @Test
  public void testAsCollection() {
    Check.equals(IMap.of(KV.of(1, 1)).asCollection(), Arrays.asList(KV.of(1, 1)));
  }

  @Test
  public void testCollector() {
    Check.equals(IMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)).stream().collect(IMap.collector()),
      IMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testConstructorRange() {
    Check.equals(IMap.of(new KV[] { KV.of(1, 1), KV.of(2, 2), KV.of(3, 3), KV.of(4, 4), KV.of(5, 5) }, 2, 2),
      IMap.of(KV.of(3, 3), KV.of(4, 4)));
  }

  @Test
  public void testContains() {
    final var map = IMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3));
    Check.isTrue(map.contains(KV.of(1, 1)));
    Check.isFalse(map.contains(KV.of(0, 0)));
  }

  @Test
  public void testContainsKey() {
    final var map = IMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3), KV.of(4, 4), KV.of(5, 5),
      KV.of(6, 6));
    Check.isTrue(map.containsKey(1));
    Check.isFalse(map.containsKey(0));
  }

  @Test
  public void testContainsValue() {
    final var map = IMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3), KV.of(4, 4), KV.of(5, 5),
      KV.of(6, 6));
    Check.isTrue(map.containsValue(1));
    Check.isFalse(map.containsValue(0));
  }

  @Test
  public void testCopy() {
    final var map = IMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3));
    final var copy = map.stream().collect(IMap.collector());
    Check.isTrue(map != copy);
    Check.equals(map, copy);
  }

  @Test
  public void testCopyLimitOffset() {
    final var map = IMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3), KV.of(4, 4), KV.of(5, 5),
      KV.of(6, 6));
    final var copy = map.stream().skip(1).limit(3).collect(IMap.collector());
    Check.isTrue(map != copy);
    Check.equals(copy, IMap.of(KV.of(2, 2), KV.of(3, 3), KV.of(4, 4)));
  }

  @Test
  public void testCopyToList() {
    Check.equals(IMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)).stream().collect(Collectors.toList()),
      Arrays.asList(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)));
  }

  @Test
  public void testCopyWithComparator() {
    final var map = IMap.of(KV.of(3, 3), KV.of(2, 2), KV.of(1, 1));
    final var copy = map.stream().sorted((a, b) -> Integer.compare(a.key(), b.key()))
      .collect(IMap.collector());
    Check.equals(copy, IMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)));
  }

  @Test
  public void testCopyWithFilter() {
    final var map = IMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3));
    final var copy = map.stream().filter(a -> a.key() > 2).collect(IMap.collector());
    Check.equals(copy, IMap.of(KV.of(3, 3)));
  }

  @Test
  public void testCopyWithMap() {
    final IMap<Integer, Integer> map = IMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3));
    final IMap<Integer, String> copy = map.stream().map(kv -> KV.of(kv.key(), kv.value().toString()))
      .collect(IMap.collector());
    Check.equals(copy, IMap.of(KV.of(1, "1"), KV.of(2, "2"), KV.of(3, "3")));
  }

  @Test
  public void testEmpty() {
    Check.isTrue(IMap.empty().isEmpty());
    Check.equals(IMap.empty().size(), 0);
    Check.isTrue(IMap.empty() == IMap.empty());
    Check.equals(IMap.empty(), IMap.empty());
    Check.isTrue(IMap.of().isEmpty());
    Check.equals(IMap.of().size(), 0);
    Check.isTrue(IMap.of() == IMap.of());
    Check.equals(IMap.of(), IMap.of());
    Check.isTrue(IMap.empty() == IMap.of());
    Check.equals(IMap.empty(), IMap.of());
    Check.isFalse(IMap.of(KV.of(1, 1)).isEmpty());
    Check.equals(IMap.of(KV.of(1, 1)).size(), 1);
  }

  @Test
  public void testEquals() {
    Check.equals(IMap.of(KV.of(1, 1)), IMap.of(KV.of(1, 1)));
    Check.isTrue(IMap.of(KV.of(1, 1)).equals(IMap.of(KV.of(1, 1))));
    Check.isTrue(
      IMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)).equals(IMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3))));
    Check.isFalse(IMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)).equals(IMap.of(KV.of(1, 1), KV.of(2, 2))));
    Check.isFalse(
      IMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)).equals(IMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(4, 4))));
  }

  @Test
  public void testGet() {
    Check.isTrue(IMap.of(KV.of(1, 1)).get(1) == 1);
  }

  @Test
  public void testHashCode() {
    final var empty = IMap.empty();
    final var a = IMap.of(KV.of(1, 1));
    final var b = IMap.of(KV.of(2, 2));
    Check.equals(empty.hashCode(), 0);
    Check.notEquals(a.hashCode(), 0);
    Check.notEquals(b.hashCode(), 0);
    Check.notEquals(a.hashCode(), b.hashCode());
  }

  @Test
  public void testIterator() {
    final var map = IMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3), KV.of(4, 4), KV.of(5, 5),
      KV.of(6, 6));
    Check.equals(IteratorUtils.toArray(map.iterator()),
      new Object[] { KV.of(1, 1), KV.of(2, 2), KV.of(3, 3), KV.of(4, 4), KV.of(5, 5), KV.of(6, 6) });
  }

  @Test
  public void testJson() {
    final var data = IMap.of(KV.of("a", 1), KV.of("b", 2), KV.of("c", IList.of(1, 2, 3)));
    final var json = JsonCodec.toString(data);
    Check.equals(json, "{\"a\":1,\"b\":2,\"c\":[1,2,3]}");
    Check.equals(data, JsonCodec.parse(json, IMap.class));
    final var fromJson = JsonCodec.parse(JsonCodec.toString(IMap.of(KV.of(1, 10), KV.of(2, 20), KV.of(3, 30))), IMap.class,
      Double.class, String.class);
    Check.equals(fromJson, IMap.of(KV.of(1D, "10"), KV.of(2D, "20"), KV.of(3D, "30")));
  }

  @Test
  public void testKeys() {
    final var map = IMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3), KV.of(4, 4), KV.of(5, 5),
      KV.of(6, 6));
    Check.equals(map.keys().toArray(), new Object[] { 1, 2, 3, 4, 5, 6 });
  }

  @Test
  public void testKeyValues() {
    final var map = IMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3), KV.of(4, 4), KV.of(5, 5),
      KV.of(6, 6));
    Check.equals(map.keyValues().toArray(),
      new Object[] { KV.of(1, 1), KV.of(2, 2), KV.of(3, 3), KV.of(4, 4), KV.of(5, 5), KV.of(6, 6) });
  }

  @Test
  public void testToArray() {
    Check.equals(IMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)).toArray(),
      new Object[] { KV.of(1, 1), KV.of(2, 2), KV.of(3, 3) });
  }

  @Test
  public void testToIList() {
    Check.equals(IMap.of(KV.of(1, 1)).toIList(), IList.of(KV.of(1, 1)));
  }

  @Test
  public void testToString() {
    Check.equals("[1:1,2:2,3:3]", IMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)).toString());
  }

  @Test
  public void testUnique() {
    Check.equals(
      IMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3), KV.of(4, 4), KV.of(5, 5), KV.of(1, 1), KV.of(2, 2), KV.of(3, 3)),
      IMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3), KV.of(4, 4), KV.of(5, 5)));
  }

  @Test
  public void testValues() {
    final var map = IMap.of(KV.of(1, 1), KV.of(2, 2), KV.of(3, 3), KV.of(4, 4), KV.of(5, 5),
      KV.of(6, 6));
    Check.equals(map.values().toArray(), new Object[] { 1, 2, 3, 4, 5, 6 });
  }

}
