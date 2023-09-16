package tech.onega.jvm.std.reflection;

import java.util.Collection;
import org.testng.annotations.Test;
import tech.onega.jvm.std.validate.Check;

public class ReflectionUtilsTest {

  @Test
  public void testInstanceof() {
    Check.isTrue(ReflectionUtils.isExtendedFrom(Collection.class, Iterable.class));
  }

}
