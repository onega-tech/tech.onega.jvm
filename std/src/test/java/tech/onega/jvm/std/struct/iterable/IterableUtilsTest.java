package tech.onega.jvm.std.struct.iterable;

import org.testng.annotations.Test;
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.validate.Check;

public class IterableUtilsTest {

  @Test
  public void testConcat() {
    Check.equals(
      IList.copy(IterableUtils.concat(IList.of(1, 2), IList.<Integer>empty(), IList.of(3, 4), IList.<Integer>empty())),
      IList.of(1, 2, 3, 4));
    Check.equals(
      IList.copy(IterableUtils.concat(IList.of())),
      IList.of());
  }

}
