package tech.onega.jvm.std.struct.stack;

import org.testng.annotations.Test;
import tech.onega.jvm.std.validate.Check;

public class StackIntTest {

  @Test
  public void test() {
    final var stack = new StackInt(2);
    stack.push(1).push(2);
    Check.equals(stack.toArray(), new int[] { 1, 2 });
    stack.pop();
    Check.equals(stack.toArray(), new int[] { 1 });
    stack.pop();
    Check.equals(stack.toArray(), new int[] {});
    Check.isTrue(stack.empty());
  }

}
