package tech.onega.jvm.std.struct.set;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.testng.annotations.Test;
import tech.onega.jvm.std.codec.json.JsonCodec;
import tech.onega.jvm.std.struct.iterable.IterableUtils;
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.validate.Check;

public class ISetTest {

  @Test
  public void testAsCollection() {
    Check.equals(ISet.of(1).asCollection(), Arrays.asList(1));
  }

  @Test
  public void testCollector() {
    Check.equals(ISet.of(1, 2, 3).stream().collect(ISet.collector()), ISet.of(1, 2, 3));
  }

  @Test
  public void testConstructorRange() {
    Check.equals(ISet.copy(new Integer[] { 1, 2, 3, 4, 5 }, 2, 2), ISet.of(3, 4));
  }

  @Test
  public void testContains() {
    final var set = ISet.of(1, 2, 3);
    Check.isTrue(set.contains(1));
    Check.isFalse(set.contains(0));
  }

  @Test
  public void testCopy() {
    final var set = ISet.of(1, 2, 3);
    final var copy = set.stream().collect(ISet.collector());
    Check.isTrue(set != copy);
    Check.equals(set, copy);
  }

  @Test
  public void testCopyLimitOffset() {
    final var set = ISet.of(1, 2, 3, 4, 5, 6);
    final var copy = set.stream().skip(1).limit(3).collect(ISet.collector());
    Check.isTrue(set != copy);
    Check.equals(copy, ISet.of(2, 3, 4));
  }

  @Test
  public void testCopyToList() {
    Check.equals(ISet.of(1, 2, 3).stream().collect(Collectors.toList()), Arrays.asList(1, 2, 3));
  }

  @Test
  public void testCopyWithComparator() {
    final var set = ISet.of(3, 2, 1);
    final var copy = set.stream().sorted(Integer::compare).collect(ISet.collector());
    Check.equals(copy, ISet.of(1, 2, 3));
  }

  @Test
  public void testCopyWithFilter() {
    final var set = ISet.of(1, 2, 3);
    final var copy = set.stream().filter(a -> a > 2).collect(ISet.collector());
    Check.equals(copy, ISet.of(3));
  }

  @Test
  public void testCopyWithMap() {
    final var set = ISet.of(1, 2, 3);
    final var copy = set.stream().map(String::valueOf).collect(ISet.collector());
    Check.equals(copy, ISet.of("1", "2", "3"));
  }

  @Test
  public void testEmpty() {
    Check.isTrue(ISet.empty().isEmpty());
    Check.equals(ISet.empty().size(), 0);
    Check.isTrue(ISet.empty() == ISet.empty());
    Check.equals(ISet.empty(), ISet.empty());
    Check.isTrue(ISet.of().isEmpty());
    Check.equals(ISet.of().size(), 0);
    Check.isTrue(ISet.of() == ISet.of());
    Check.equals(ISet.of(), ISet.of());
    Check.isTrue(ISet.empty() == ISet.of());
    Check.equals(ISet.empty(), ISet.of());
    Check.isFalse(ISet.of(1).isEmpty());
    Check.equals(ISet.of(1).size(), 1);
  }

  @Test
  public void testEquals() {
    Check.equals(ISet.of("a"), ISet.of("a"));
    Check.isTrue(ISet.of("a").equals(ISet.of("a")));
    Check.isTrue(ISet.of(1, 2, 3).equals(ISet.of(1, 2, 3)));
    Check.isFalse(ISet.of(1, 2, 3).equals(ISet.of(1, 2)));
    Check.isFalse(ISet.of(1, 2, 3).equals(ISet.of(1, 2, 4)));
  }

  @Test
  public void testHashCode() {
    final var empty = ISet.empty();
    final var a = ISet.of(1);
    final var b = ISet.of(2);
    Check.equals(empty.hashCode(), 0);
    Check.notEquals(a.hashCode(), 0);
    Check.notEquals(b.hashCode(), 0);
    Check.notEquals(a.hashCode(), b.hashCode());
  }

  @Test
  public void testIterator() {
    final var vector = ISet.of(1, 2, 3, 4, 5, 6);
    Check.equals(IterableUtils.toArray(vector), new Object[] { 1, 2, 3, 4, 5, 6 });
  }

  @Test
  public void testJson() {
    final var data = ISet.of(1, 2, 3, 4, IList.of("f", "b"));
    final var json = JsonCodec.toString(data);
    Check.equals(json, "[1,2,3,4,[\"f\",\"b\"]]");
    Check.equals(data, JsonCodec.parse(json, ISet.class));
    final var fromJson = JsonCodec.parse(JsonCodec.toString(ISet.of("1", "2", "3")), ISet.class, Double.class);
    Check.equals(fromJson, ISet.of(1D, 2D, 3D));
  }

  @Test
  public void testToArray() {
    Check.equals(ISet.of(1, 2, 3).toArray(), new Object[] { 1, 2, 3 });
  }

  @Test
  public void testToIList() {
    Check.equals(ISet.of(1).toIList(), IList.of(1));
  }

  @Test
  public void testToString() {
    Check.equals("[1,2,3]", ISet.of(1, 2, 3).toString());
  }

  @Test
  public void testUnique() {
    Check.equals(ISet.of(1, 2, 3, 4, 5, 1, 2, 3), ISet.of(1, 2, 3, 4, 5));
  }

}
