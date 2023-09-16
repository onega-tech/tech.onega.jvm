package tech.onega.jvm.std.struct.list;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.testng.annotations.Test;
import tech.onega.jvm.std.codec.json.JsonCodec;
import tech.onega.jvm.std.struct.iterable.IterableUtils;
import tech.onega.jvm.std.validate.Check;

public class IListTest {

  @Test
  public void testAsCollection() {
    Check.equals(IList.of(1, 2, 3).asCollection(), Arrays.asList(1, 2, 3));
  }

  @Test
  public void testCollector() {
    Check.equals(IList.of(1, 2, 3).stream().collect(IList.collector()), IList.of(1, 2, 3));
  }

  @Test
  public void testConstructorRange() {
    Check.equals(IList.copy(new Integer[] { 1, 2, 3, 4, 5 }, 2, 2), IList.of(3, 4));
  }

  @Test
  public void testContains() {
    final var list = IList.of(1, 2, 3);
    Check.isTrue(list.contains(1));
    Check.isFalse(list.contains(0));
  }

  @Test
  public void testCopy() {
    final var list = IList.<Integer>of(1, 2, 3);
    final var copy = list.stream().collect(IList.collector());
    Check.isTrue(list != copy);
    Check.equals(list, copy);
  }

  @Test
  public void testCopyLimitOffset() {
    final var list = IList.<Integer>of(1, 2, 3, 4, 5, 6);
    final var copy = list.stream().skip(1).limit(3).collect(IList.collector());
    Check.isTrue(list != copy);
    Check.equals(copy, IList.of(2, 3, 4));
  }

  @Test
  public void testCopyMerge() {
    Check.equals(
      IList.copy(IList.of(1, 2, 3), IList.of(), IList.of(6, 7), IList.empty(), IList.of(111)),
      IList.of(1, 2, 3, 6, 7, 111));
  }

  @Test
  public void testCopyToList() {
    Check.equals(IList.of(1, 2, 3).stream().collect(Collectors.toList()), Arrays.asList(1, 2, 3));
  }

  @Test
  public void testCopyWithComparator() {
    final var list = IList.<Integer>of(3, 2, 1);
    final var copy = list.stream().sorted(Integer::compare).collect(IList.collector());
    Check.equals(copy, IList.of(1, 2, 3));
  }

  @Test
  public void testCopyWithFilter() {
    final var list = IList.<Integer>of(1, 2, 3);
    final var copy = list.stream().filter(a -> a > 2).collect(IList.collector());
    Check.equals(copy, IList.of(3));
  }

  @Test
  public void testCopyWithMap() {
    final var list = IList.<Object>of(1, 2, 3);
    final var copy = list.stream().map(String::valueOf).collect(IList.collector());
    Check.equals(copy, IList.of("1", "2", "3"));
  }

  @Test
  public void testEmpty() {
    Check.isTrue(IList.empty().isEmpty());
    Check.equals(IList.empty().size(), 0);
    Check.isTrue(IList.empty() == IList.empty());
    Check.equals(IList.empty(), IList.empty());
    Check.isTrue(IList.of().isEmpty());
    Check.equals(IList.of().size(), 0);
    Check.isTrue(IList.of() == IList.of());
    Check.equals(IList.of(), IList.of());
    Check.isTrue(IList.empty() == IList.of());
    Check.equals(IList.empty(), IList.of());
    Check.isFalse(IList.of(1).isEmpty());
    Check.equals(IList.of(1).size(), 1);
  }

  @Test
  public void testEquals() {
    Check.equals(IList.of("a"), IList.of("a"));
    Check.isTrue(IList.of("a").equals(IList.of("a")));
    Check.isTrue(IList.of(1, 2, 3).equals(IList.of(1, 2, 3)));
    Check.isFalse(IList.of(1, 2, 3).equals(IList.of(1, 2)));
    Check.isFalse(IList.of(1, 2, 3).equals(IList.of(1, 2, 4)));
  }

  @Test
  public void testFirst() {
    Check.isTrue(IList.of().first() == null);
    Check.isTrue(IList.of(1, 2, 3).first() == 1);
  }

  @Test
  public void testGet() {
    Check.isTrue(IList.of(1, 2, 3).get(1) == 2);
  }

  @Test
  public void testHashCode() {
    final var empty = IList.<Integer>empty();
    final var a = IList.<Integer>of(1);
    final var b = IList.<Integer>of(2);
    Check.equals(empty.hashCode(), 0);
    Check.notEquals(a.hashCode(), 0);
    Check.notEquals(b.hashCode(), 0);
    Check.notEquals(a.hashCode(), b.hashCode());
  }

  @Test
  public void testIndexOf() {
    final var list = IList.<Integer>of(1, 2, 3, 4, 5, 6);
    Check.equals(list.indexOf(33), -1);
    Check.equals(list.indexOf(2), 1);
  }

  @Test
  public void testIterator() {
    final var list = IList.<Integer>of(1, 2, 3, 4, 5, 6);
    Check.equals(IterableUtils.toArray(list), new Object[] { 1, 2, 3, 4, 5, 6 });
  }

  @Test
  public void testJson() {
    final var data = IList.of(1, 2, 3, 4, IList.of("f", "b"));
    final var json = JsonCodec.toString(data);
    Check.equals(json, "[1,2,3,4,[\"f\",\"b\"]]");
    Check.equals(data, JsonCodec.parse(json, IList.class));
    final var fromJson = JsonCodec.parse(JsonCodec.toString(IList.of("1", "2", "3")), IList.class, Double.class);
    Check.equals(fromJson, IList.of(1D, 2D, 3D));
  }

  @Test
  public void testToArray() {
    Check.equals(IList.of(1, 2, 3).toArray(), new Object[] { 1, 2, 3 });
  }

  @Test
  public void testToIList() {
    final var list = IList.of(1, 2, 3);
    Check.isTrue(list == list.toIList());
  }

  @Test
  public void testToString() {
    Check.equals("[1,2,3]", IList.of(1, 2, 3).toString());
  }

}
