package tech.onega.jvm.std.struct.set;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.testng.annotations.Test;
import tech.onega.jvm.std.codec.json.JsonCodec;
import tech.onega.jvm.std.struct.hash.KV;
import tech.onega.jvm.std.struct.iterable.IterableUtils;
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.struct.list.MList;
import tech.onega.jvm.std.struct.map.MMap;
import tech.onega.jvm.std.validate.Check;

public class MSetTest {

  @Test
  public void testAdd() {
    Check.equals(MSet.create().add(1), MSet.of(1));
  }

  @Test
  public void testAddAll() {
    Check.equals(MSet.create().addAll(1, 2, 3), MSet.of(1, 2, 3));
    Check.equals(MSet.create().addAll(new Integer[] { 1, 2, 3 }, 2), MSet.of(1, 2));
    Check.equals(MSet.create().addAll(Arrays.asList(1, 2, 3).stream()), MSet.of(1, 2, 3));
    final var src = MSet.create();
    final var in = MSet.of(1, 2, 3, 4, 5);
    src.addAll(in);
    Check.equals(src, MSet.of(1, 2, 3, 4, 5));
  }

  @Test
  public void testAsCollection() {
    Check.equals(MSet.of(1, 2, 3).asCollection(), Arrays.asList(1, 2, 3));
  }

  @Test
  public void testCapacity() {
    Check.equals(MSet.of(1, 2, 3).capacity(1), MSet.of(1));
    Check.equals(MSet.create(3).capacity(), 3);
    Check.equals(MSet.create(3).addAll(1, 2, 3).capacity(), 3);
    Check.equals(MSet.create(2, 4).capacity(), 2);
    Check.equals(MSet.create(2, 4).capacity(4).capacity(), 4);
    Check.withThrowType(Throwable.class, () -> MSet.create(2, 4).capacity(8));
  }

  @Test
  public void testClear() {
    final var set = MSet.of(1, 2, 3);
    Check.isFalse(set.isEmpty());
    Check.equals(set.size(), 3);
    set.clear();
    Check.isTrue(set.isEmpty());
    Check.equals(set.size(), 0);
  }

  @Test
  public void testCollector() {
    Check.equals(MSet.of(1, 2, 3).stream().collect(MSet.collector()), MSet.of(1, 2, 3));
  }

  @Test
  public void testConstructorRange() {
    Check.equals(MSet.copy(new Integer[] { 1, 2, 3, 4, 5 }, 2, 2), MSet.of(3, 4));
  }

  @Test
  public void testContains() {
    final MSet<Integer> set = MSet.of(1, 2, 3);
    Check.isTrue(set.contains(1));
    Check.isFalse(set.contains(0));
  }

  @Test
  public void testCopy() {
    final var set = MSet.of(1, 2, 3);
    final var copy = set.stream().collect(MSet.collector());
    Check.isTrue(set != copy);
    Check.equals(set, copy);
  }

  @Test
  public void testCopyLimitOffset() {
    final var set = MSet.of(1, 2, 3, 4, 5, 6);
    final var copy = set.stream().skip(1).limit(3).collect(MSet.collector());
    Check.isTrue(set != copy);
    Check.equals(copy, MSet.of(2, 3, 4));
  }

  @Test
  public void testCopyToList() {
    Check.equals(MSet.of(1, 2, 3).stream().collect(Collectors.toList()), Arrays.asList(1, 2, 3));
  }

  @Test
  public void testCopyWithComparator() {
    final var set = MSet.of(3, 2, 1);
    final var copy = set.stream().sorted(Integer::compare).collect(MSet.collector());
    Check.equals(copy, MSet.of(1, 2, 3));
  }

  @Test
  public void testCopyWithFilter() {
    final var set = MSet.<Integer>of(1, 2, 3);
    final var copy = set.stream().filter(a -> a > 2).collect(MSet.collector());
    Check.equals(copy, MSet.of(3));
  }

  @Test
  public void testCopyWithMap() {
    final var set = MSet.of(1, 2, 3);
    final var copy = set.stream().map(String::valueOf).collect(MSet.collector());
    Check.equals(copy, MSet.of("1", "2", "3"));
  }

  @Test
  public void testEmpty() {
    Check.isTrue(MSet.create().isEmpty());
    Check.equals(MSet.create().size(), 0);
    Check.isFalse(MSet.create() == MSet.create());
    Check.equals(MSet.create(), MSet.create());
    Check.isTrue(MSet.of().isEmpty());
    Check.equals(MSet.of().size(), 0);
    Check.isFalse(MSet.of() == MSet.of());
    Check.equals(MSet.of(), MSet.of());
    Check.isFalse(MSet.create() == MSet.of());
    Check.equals(MSet.create(), MSet.of());
    Check.isFalse(MSet.of(1).isEmpty());
    Check.equals(MSet.of(1).size(), 1);
  }

  @Test
  public void testEquals() {
    Check.isFalse(MSet.of("a") == MSet.of("a"));
    Check.equals(MSet.of("a"), MSet.of("a"));
    Check.isTrue(MSet.of("a").equals(MSet.of("a")));
    Check.isTrue(MSet.of(1, 2, 3).equals(MSet.of(1, 2, 3)));
    Check.isFalse(MSet.of(1, 2, 3).equals(MSet.of(1, 2)));
    Check.isFalse(MSet.of(1, 2, 3).equals(MSet.of(1, 2, 4)));
  }

  @Test
  public void testFull() {
    final var size = 100;
    final var set = MSet.create(1, size);
    Check.isTrue(set.isEmpty());
    var i = 0;
    while (!set.isFull()) {
      set.add(i++);
    }
    Check.equals(set.size(), size);
    Check.equals(set.capacity(), size);
    Check.isTrue(set.isFull());
  }

  @Test
  public void testHashCode() {
    final var empty = MSet.create();
    final var a = MSet.of(1);
    final var b = MSet.of(2);
    Check.equals(empty.hashCode(), 0);
    Check.notEquals(a.hashCode(), 0);
    Check.notEquals(b.hashCode(), 0);
    Check.notEquals(a.hashCode(), b.hashCode());
  }

  @Test
  public void testHashCodeMutable() {
    final var set = MSet.create();
    Check.equals(set.hashCode(), 0);
    set.add(1);
    final int hashCodeA = set.hashCode();
    set.add(2);
    final var hashCodeB = set.hashCode();
    Check.notEquals(hashCodeA, 0);
    Check.notEquals(hashCodeB, 0);
    Check.notEquals(hashCodeA, hashCodeB);
  }

  @Test
  public void testIterator() {
    final var set = MSet.of(1, 2, 3, 4, 5, 6);
    Check.equals(IterableUtils.toArray(set), new Object[] { 1, 2, 3, 4, 5, 6 });
  }

  @Test
  public void testJson() {
    final var data = MSet.of(1, 2, 3, 4, IList.of("f", "b"));
    final var json = JsonCodec.toString(data);
    Check.equals(json, "[1,2,3,4,[\"f\",\"b\"]]");
    Check.equals(data, JsonCodec.parse(json, MSet.class));
    final var fromJson = JsonCodec.parse(JsonCodec.toString(MList.of("1", "2", "3")), MSet.class, Double.class);
    Check.equals(fromJson, MSet.of(1D, 2D, 3D));
  }

  @Test
  public void testOutOfMemmory() {
    final var size = 10;
    final var set = MSet.<Integer>create(size, size);
    final var tmp = new Integer[size + 1];
    for (var i = 0; i < tmp.length; i++) {
      tmp[i] = i;
    }
    Check.withThrowType(Throwable.class, () -> set.addAll(tmp));
  }

  @Test
  public void testRemove() {
    final var set = MSet.<Integer>create(6, 6);
    set.addAll(1, 2, 3, 4, 5, 6);
    Check.equals(set.size(), 6);
    Check.equals(set.maxCapacity(), 6);
    Check.isTrue(set.equals(MSet.of(1, 2, 3, 4, 5, 6)));
    set.remove(3);
    Check.equals(set.size(), 5);
    Check.equals(set.maxCapacity(), 6);
    Check.isTrue(set.equals(MSet.of(1, 2, 4, 5, 6)));
    set.remove(1).remove(2).remove(4).remove(5).remove(6);
    Check.isTrue(set.isEmpty());
  }

  @Test
  public void testRemoveFiltered() {
    final var map = MMap.create();
    for (var i = 0; i < 100; i++) {
      map.add(KV.of(i, i));
    }
    map.remove(kv -> false);
    Check.isTrue(map.isEmpty());
  }

  @Test
  public void testResize() {
    final var set = MSet.of(1, 2, 3, 4, 5);
    Check.equals(set.size(), 5);
    set.size(2);
    Check.equals(set.size(), 2);
    Check.isTrue(set.equals(MSet.of(1, 2)));
  }

  @Test
  public void testSort() {
    Check.isTrue(MSet.of(3, 2, 1).sort(Integer::compare).equals(MSet.of(1, 2, 3)));
  }

  @Test
  public void testToArray() {
    Check.equals(MSet.of(1, 2, 3).toArray(), new Object[] { 1, 2, 3 });
  }

  @Test
  public void testToIList() {
    Check.equals(MSet.of(1, 2, 3).toIList(), IList.of(1, 2, 3));
  }

  @Test
  public void testToString() {
    Check.equals("[1,2,3]", MSet.of(1, 2, 3).toString());
  }

  @Test
  public void testTrim() {
    final var set = MSet.create(100, 100);
    set.addAll(1, 2, 3);
    Check.equals(set.capacity(), 100);
    Check.equals(set.size(), 3);
    set.trim();
    Check.equals(set.capacity(), set.size());
    Check.equals(set, MSet.of(1, 2, 3));
  }

  @Test
  public void testUnique() {
    Check.equals(MSet.of(1, 2, 3, 4, 5, 1, 2, 3), MSet.of(1, 2, 3, 4, 5));
  }

}
