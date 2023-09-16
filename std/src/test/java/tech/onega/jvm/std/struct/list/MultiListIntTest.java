package tech.onega.jvm.std.struct.list;

import org.testng.annotations.Test;
import tech.onega.jvm.std.validate.Check;

public class MultiListIntTest {

  @Test
  public void test() {
    final var ml = new MultiListInt(20, 20);
    for (var v = 1; v <= 5; v++) {
      ml.addLast(0, v);
    }
    for (var v = 1; v <= 5; v++) {
      ml.addLast(1, 20 + v);
    }
    for (var v = 6; v <= 10; v++) {
      ml.addLast(0, v);
    }
    Check.equals(ml.list(0), new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
    Check.equals(ml.list(1), new int[] { 21, 22, 23, 24, 25 });
    final var list = 0;
    var k = ml.size(list);
    for (final var i : ml.indexes(list)) {
      Check.equals(ml.value(i), ml.value(list, --k));
    }
    k = ml.size(list);
    for (final var i : ml.values(list)) {
      Check.equals(i, ml.value(list, --k));
    }
  }

}
