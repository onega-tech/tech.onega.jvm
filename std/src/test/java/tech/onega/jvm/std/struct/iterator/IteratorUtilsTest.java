package tech.onega.jvm.std.struct.iterator;

import java.util.Arrays;
import org.testng.annotations.Test;
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.validate.Check;

public class IteratorUtilsTest {

  @Test
  public void testConcat() {
    final var iterator = IteratorUtils.concat(
      IList.of(1, 2).iterator(),
      IList.<Integer>empty().iterator(),
      IList.of(3, 4).iterator(),
      IList.<Integer>empty().iterator());
    Check.equals(
      IList.copy(() -> iterator),
      IList.of(1, 2, 3, 4));
  }

  @Test
  public void testSkip() {
    final var iterable = Arrays.asList(1, 2, 3);
    Check.isTrue(IteratorUtils.skip(iterable.iterator(), 0).next() == 1);
    Check.isTrue(IteratorUtils.skip(iterable.iterator(), 1).next() == 2);
    Check.isTrue(IteratorUtils.skip(iterable.iterator(), 2).next() == 3);
  }

}
