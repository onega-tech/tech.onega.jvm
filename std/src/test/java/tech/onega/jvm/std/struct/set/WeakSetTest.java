package tech.onega.jvm.std.struct.set;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.testng.annotations.Test;
import tech.onega.jvm.std.validate.Check;

public class WeakSetTest {

  @Test
  public void test() throws Exception {
    final var set = new WeakSet<>();
    Check.isTrue(set.isEmpty());
    final var objectRef = new AtomicReference<>();
    objectRef.set(new Object());
    set.add(objectRef.get());
    Check.isFalse(set.isEmpty());
    Check.equals(set.size(), 1);
    objectRef.set(null);
    for (var i = 0; i < 100; i++) {
      System.gc();
      TimeUnit.MILLISECONDS.sleep(10);
      if (set.isEmpty()) {
        break;
      }
    }
    Check.isTrue(set.isEmpty());
  }

}
