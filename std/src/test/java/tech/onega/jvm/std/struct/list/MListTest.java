package tech.onega.jvm.std.struct.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.testng.annotations.Test;
import tech.onega.jvm.std.codec.json.JsonCodec;
import tech.onega.jvm.std.struct.iterable.IterableUtils;
import tech.onega.jvm.std.struct.set.MSet;
import tech.onega.jvm.std.struct.vector.Vector;
import tech.onega.jvm.std.validate.Check;

public class MListTest {

  @Test
  public void testAdd() {
    Check.equals(MList.create().add(1), MList.of(1));
  }

  @Test
  public void testAddAll() {
    Check.equals(MList.create().addAll(1, 2, 3), MList.of(1, 2, 3));
    Check.equals(MList.create().addAll(new Integer[] { 1, 2, 3 }, 2), MList.of(1, 2));
    Check.equals(MList.create().addAll(Arrays.asList(1, 2, 3).stream()), MList.of(1, 2, 3));
    Check.equals(MList.create().addAll(MList.of(1, 2, 3)), MList.of(1, 2, 3));
    Check.equals(MList.create().addAll(IList.of(1, 2, 3)), MList.of(1, 2, 3));
    Check.equals(MList.create().addAll(MSet.of(1, 2, 3)), MList.of(1, 2, 3));
  }

  @Test
  public void testAsCollection() {
    Check.equals(new ArrayList<>(MList.of(1, 2, 3).asCollection()), Arrays.asList(1, 2, 3));
  }

  @Test
  public void testCapacity() {
    Check.equals(MList.of(1, 2, 3).capacity(1), MList.of(1));
    Check.equals(MList.create(3).capacity(), 3);
    Check.equals(MList.create(3).addAll(1, 2, 3).capacity(), 3);
    Check.equals(MList.create(2, 4).capacity(), 2);
    Check.equals(MList.create(2, 4).capacity(4).capacity(), 4);
    Check.withThrowType(Throwable.class, () -> MList.create(2, 4).capacity(8));
  }

  @Test
  public void testCheckIndexInBoundsRange() {
    Check.withThrowType(Throwable.class, () -> MList.create(1, 1).fill(99, 4));
  }

  @Test
  public void testClear() {
    final var list = MList.of(1, 2, 3);
    Check.isFalse(list.isEmpty());
    Check.equals(list.size(), 3);
    list.clear();
    Check.isTrue(list.isEmpty());
    Check.equals(list.size(), 0);
  }

  @Test
  public void testCollector() {
    Check.equals(MList.of(1, 2, 3).stream().collect(MList.collector()), MList.of(1, 2, 3));
  }

  @Test
  public void testConstructorRange() {
    Check.equals(MList.copy(new Integer[] { 1, 2, 3, 4, 5 }, 2, 2), MList.of(3, 4));
  }

  @Test
  public void testContains() {
    final var list = MList.of(1, 2, 3);
    Check.isTrue(list.contains(1));
    Check.isFalse(list.contains(0));
  }

  @Test
  public void testCopy() {
    final var list = MList.of(1, 2, 3);
    final var copy = list.stream().collect(MList.collector());
    Check.isTrue(list != copy);
    Check.equals(list, copy);
  }

  @Test
  public void testCopyLimitOffset() {
    final var list = MList.of(1, 2, 3, 4, 5, 6);
    final var copy = list.stream().skip(1).limit(3).collect(MList.collector());
    Check.isTrue(list != copy);
    Check.equals(copy, MList.of(2, 3, 4));
  }

  @Test
  public void testCopyToList() {
    Check.equals(MList.of(1, 2, 3).stream().collect(Collectors.toList()), Arrays.asList(1, 2, 3));
  }

  @Test
  public void testCopyWithComparator() {
    final var list = MList.of(3, 2, 1);
    final var copy = list.stream().sorted(Integer::compare).collect(MList.collector());
    Check.equals(copy, MList.of(1, 2, 3));
  }

  @Test
  public void testCopyWithFilter() {
    final var list = MList.of(1, 2, 3);
    final var copy = list.stream().filter(a -> a > 2).collect(MList.collector());
    Check.equals(copy, MList.of(3));
  }

  @Test
  public void testCopyWithMap() {
    final Vector<Object> list = MList.of(1, 2, 3);
    final Vector<Object> copy = list.stream().map(String::valueOf).collect(MList.collector());
    Check.equals(copy, MList.of("1", "2", "3"));
  }

  @Test
  public void testEmpty() {
    Check.isTrue(MList.create().isEmpty());
    Check.equals(MList.create().size(), 0);
    Check.isFalse(MList.create() == MList.create());
    Check.equals(MList.create(), MList.create());
    Check.isTrue(MList.of().isEmpty());
    Check.equals(MList.of().size(), 0);
    Check.isFalse(MList.of() == MList.of());
    Check.equals(MList.of(), MList.of());
    Check.isFalse(MList.create() == MList.of());
    Check.equals(MList.create(), MList.of());
    Check.isTrue(MList.create(1).isEmpty());
    Check.equals(MList.of(1).size(), 1);
  }

  @Test
  public void testEquals() {
    Check.isFalse(MList.of("a") == MList.of("a"));
    Check.equals(MList.of("a"), MList.of("a"));
    Check.isTrue(MList.of("a").equals(MList.of("a")));
    Check.isTrue(MList.of(1, 2, 3).equals(MList.of(1, 2, 3)));
    Check.isFalse(MList.of(1, 2, 3).equals(MList.create(1, 2)));
    Check.isFalse(MList.of(1, 2, 3).equals(MList.of(1, 2, 4)));
  }

  @Test
  public void testFill() {
    final var x = 99;
    Check.equals(MList.of(1, 2, 3).fill(x), MList.of(x, x, x));
    Check.equals(MList.of(1, 2, 3).fill(x, 2), MList.of(x, x, 3));
    Check.equals(MList.of(1, 2, 3).fill(x, 1, 1), MList.of(1, x, 3));
  }

  @Test
  public void testFilter() {
    Check.equals(MList.of(0, 0, 3).filter(f -> f == 0), MList.of(0, 0));
    Check.equals(MList.of(0, 0, 3).filter(f -> f == 0, 1), MList.of(0));
  }

  @Test
  public void testFirst() {
    Check.isTrue(MList.of().first() == null);
    Check.isTrue(MList.of(1, 2, 3).first() == 1);
  }

  @Test
  public void testFull() {
    final var size = 10;
    final var list = MList.create(1, size);
    Check.isTrue(list.isEmpty());
    var i = 0;
    while (!list.isFull()) {
      list.add(i++);
    }
    Check.equals(list.size(), size);
    Check.equals(list.capacity(), size);
    Check.isTrue(list.isFull());
  }

  @Test
  public void testGet() {
    Check.isTrue(MList.of(1, 2, 3).get(1) == 2);
    Check.withThrowType(Exception.class, () -> MList.create(1).get(2));
  }

  @Test
  public void testHashCode() {
    final var empty = MList.create();
    final var a = MList.of(1);
    final var b = MList.of(2);
    Check.equals(empty.hashCode(), 0);
    Check.notEquals(a.hashCode(), 0);
    Check.notEquals(b.hashCode(), 0);
    Check.notEquals(a.hashCode(), b.hashCode());
  }

  @Test
  public void testHashCodeMutable() {
    final var list = MList.create();
    Check.equals(list.hashCode(), 0);
    list.add(1);
    final var hashCodeA = list.hashCode();
    list.add(2);
    final var hashCodeB = list.hashCode();
    Check.notEquals(hashCodeA, 0);
    Check.notEquals(hashCodeB, 0);
    Check.notEquals(hashCodeA, hashCodeB);
  }

  @Test
  public void testIndexOf() {
    final var list = MList.of(1, 2, 3, 4, 5, 6);
    Check.equals(list.indexOf(33), -1);
    Check.equals(list.indexOf(2), 1);
  }

  @Test
  public void testIterator() {
    final var list = MList.of(1, 2, 3, 4, 5, 6);
    Check.equals(IterableUtils.toArray(list), new Object[] { 1, 2, 3, 4, 5, 6 });
  }

  @Test
  public void testJson() {
    final var data = MList.of(1, 2, 3, 4, IList.of("f", "b"));
    final var json = JsonCodec.toString(data);
    Check.equals(json, "[1,2,3,4,[\"f\",\"b\"]]");
    Check.equals(data, JsonCodec.parse(json, MList.class));
    final var fromJson = JsonCodec.parse(JsonCodec.toString(MList.of("1", "2", "3")), MList.class, Double.class);
    Check.equals(fromJson, MList.of(1D, 2D, 3D));
  }

  @Test
  public void testMap() {
    Check.equals(MList.of(1, 2, 3).map(a -> a * 2), MList.of(2, 4, 6));
    Check.equals(MList.of(1, 2, 3).map(a -> a * 2, 1), MList.of(2, 2, 3));
  }

  @Test
  public void testOutOfMemmory() {
    final var size = 10;
    final var list = MList.create(size, size);
    Check.withThrowType(Throwable.class, () -> list.addAll());
  }

  @Test
  public void testRemove() {
    final var list = MList.create(6, 6);
    list.addAll(1, 2, 3, 4, 5, 6);
    Check.equals(list.size(), 6);
    Check.equals(list.maxCapacity(), 6);
    Check.isTrue(list.equals(MList.of(1, 2, 3, 4, 5, 6)));
    list.remove(3);
    Check.equals(list.size(), 5);
    Check.equals(list.maxCapacity(), 6);
    Check.isTrue(list.equals(MList.of(1, 2, 4, 5, 6)));
    list.remove(1).remove(2).remove(4).remove(5).remove(6);
    Check.isTrue(list.isEmpty());
  }

  @Test
  public void testRemoveLast() {
    Check.equals(MList.create().removeLast(), MList.create());
    Check.equals(MList.create(1).removeLast(), MList.create());
    Check.equals(MList.create(1, 2).removeLast(), MList.create(1));
  }

  @Test
  public void testRemovePos() {
    Check.equals(MList.of(1).removePos(0), MList.create());
    Check.equals(MList.of(1, 2).removePos(1), MList.of(1));
    Check.equals(MList.of(1, 2).removePos(0), MList.of(2));
  }

  @Test
  public void testResize() {
    Check.equals(MList.of(1, 2, 3, 4, 5).size(2), MList.of(1, 2));
    Check.withThrowType(Throwable.class, () -> MList.create(1, 1).size(2));
    Check.equals(MList.create(3).addAll(1, 2, 3).size(5), MList.of(1, 2, 3, null, null));
  }

  @Test
  public void testReverse() {
    Check.equals(MList.create(100, 100).addAll(1, 2, 3, 4).reverse(), MList.of(4, 3, 2, 1));
  }

  @Test
  public void testSetAll() {
    final var list = MList.<Integer>of(8, 9).setAll(1, 5, 6, 7);
    Check.equals(list, MList.of(8, 5, 6, 7));
  }

  @Test
  public void testSetArray() {
    final var list = MList.<Integer>create().setAll(0, 1, 2, 3);
    Check.equals(list.size(), 3);
    Check.equals(list, MList.of(1, 2, 3));
  }

  @Test
  public void testSetStream() {
    Check.equals(
      MList.of(1, 2, 3),
      MList.create().setAll(0, Arrays.asList(1, 2, 3).stream()));
  }

  @Test
  public void testSort() {
    Check.equals(MList.of(3, 2, 1).sort(Integer::compare), MList.of(1, 2, 3));
    Check.equals(MList.of(3, 2, 1).sort(Integer::compare, 2), MList.of(2, 3, 1));
  }

  @Test
  public void testToArray() {
    Check.equals(MList.of(1, 2, 3).toArray(), new Object[] { 1, 2, 3 });
  }

  @Test
  public void testToIList() {
    Check.equals(MList.of(1, 2, 3).toIList(), IList.of(1, 2, 3));
  }

  @Test
  public void testToString() {
    Check.equals("[1,2,3]", MList.of(1, 2, 3).toString());
  }

  @Test
  public void testTrim() {
    final var list = MList.create(100, 100);
    list.addAll(1, 2, 3);
    Check.equals(list.capacity(), 100);
    Check.equals(list.size(), 3);
    list.trim();
    Check.equals(list.capacity(), list.size());
    Check.equals(list, MList.of(1, 2, 3));
  }

}
