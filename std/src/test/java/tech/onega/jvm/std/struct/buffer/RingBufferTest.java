package tech.onega.jvm.std.struct.buffer;

import org.testng.annotations.Test;
import tech.onega.jvm.std.validate.Check;

public class RingBufferTest {

  @Test
  public void test0() {
    Check.equals(RingBuffer.of(0).toArray(), new Object[] {});
    Check.equals(RingBuffer.of(0, 1).toArray(), new Object[] {});
    Check.equals(RingBuffer.of(0, 1, 2).toArray(), new Object[] {});
    Check.equals(RingBuffer.of(0, 1, 2, 3).toArray(), new Object[] {});
  }

  @Test
  public void test1() {
    Check.equals(RingBuffer.of(1).toArray(), new Object[] {});
    Check.equals(RingBuffer.of(1, 1).toArray(), new Object[] { 1 });
    Check.equals(RingBuffer.of(1, 1, 2).toArray(), new Object[] { 2 });
    Check.equals(RingBuffer.of(1, 1, 2, 3).toArray(), new Object[] { 3 });
  }

  @Test
  public void test2() {
    Check.equals(RingBuffer.of(2).toArray(), new Object[] {});
    Check.equals(RingBuffer.of(2, 1).toArray(), new Object[] { 1 });
    Check.equals(RingBuffer.of(2, 1, 2).toArray(), new Object[] { 1, 2 });
    Check.equals(RingBuffer.of(2, 1, 2, 3).toArray(), new Object[] { 2, 3 });
  }

  @Test
  public void test3() {
    Check.equals(RingBuffer.create(3).capacity(), 3, "capacity");
    Check.equals(RingBuffer.create(3).size(), 0, "size");
    Check.equals(RingBuffer.create(3).firstIndex(), -1, "first");
    Check.equals(RingBuffer.create(3).lastIndex(), -1, "last");
    Check.equals(RingBuffer.create(3).addArray(new Integer[1]).size(), 1, "size");
    Check.equals(RingBuffer.create(3).addArray(new Integer[1]).firstIndex(), 0, "first");
    Check.equals(RingBuffer.create(3).addArray(new Integer[1]).lastIndex(), 0, "last");
    Check.equals(RingBuffer.create(3).addArray(new Integer[2]).size(), 2, "size");
    Check.equals(RingBuffer.create(3).addArray(new Integer[2]).firstIndex(), 0, "first");
    Check.equals(RingBuffer.create(3).addArray(new Integer[2]).lastIndex(), 1, "last");
    Check.equals(RingBuffer.create(3).addArray(new Integer[3]).size(), 3, "size");
    Check.equals(RingBuffer.create(3).addArray(new Integer[3]).firstIndex(), 0, "first");
    Check.equals(RingBuffer.create(3).addArray(new Integer[3]).lastIndex(), 2, "last");
    Check.equals(RingBuffer.create(3).addArray(new Integer[4]).size(), 3, "size");
    Check.equals(RingBuffer.create(3).addArray(new Integer[4]).firstIndex(), 1, "first");
    Check.equals(RingBuffer.create(3).addArray(new Integer[4]).lastIndex(), 0, "last");
    Check.equals(RingBuffer.create(3).addArray(new Integer[5]).size(), 3, "size");
    Check.equals(RingBuffer.create(3).addArray(new Integer[5]).firstIndex(), 2, "first");
    Check.equals(RingBuffer.create(3).addArray(new Integer[5]).lastIndex(), 1, "last");
    Check.equals(RingBuffer.create(3).addArray(new Integer[6]).size(), 3, "size");
    Check.equals(RingBuffer.create(3).addArray(new Integer[6]).firstIndex(), 0, "first");
    Check.equals(RingBuffer.create(3).addArray(new Integer[6]).lastIndex(), 2, "last");
    Check.equals(RingBuffer.of(3).toArray(), new Object[] {});
    Check.equals(RingBuffer.of(3, 1).toArray(), new Object[] { 1 });
    Check.equals(RingBuffer.of(3, 1, 2).toArray(), new Object[] { 1, 2 });
    Check.equals(RingBuffer.of(3, 1, 2, 3).toArray(), new Object[] { 1, 2, 3 });
    Check.equals(RingBuffer.of(3, 1, 2, 3, 4).toArray(), new Object[] { 2, 3, 4 });
    Check.equals(RingBuffer.of(3, 1, 2, 3, 4, 5).toArray(), new Object[] { 3, 4, 5 });
    Check.equals(RingBuffer.of(3, 1, 2, 3, 4, 5, 6).toArray(), new Object[] { 4, 5, 6 });
    Check.equals(RingBuffer.of(3, 1, 2, 3, 4, 5, 6, 7).toArray(), new Object[] { 5, 6, 7 });
  }

  @Test
  public void test4() {
    Check.equals(RingBuffer.of(4).toArray(), new Object[] {});
    Check.equals(RingBuffer.of(4, 1).toArray(), new Object[] { 1 });
    Check.equals(RingBuffer.of(4, 1, 2).toArray(), new Object[] { 1, 2 });
    Check.equals(RingBuffer.of(4, 1, 2, 3).toArray(), new Object[] { 1, 2, 3 });
    Check.equals(RingBuffer.of(4, 1, 2, 3, 4).toArray(), new Object[] { 1, 2, 3, 4 });
    Check.equals(RingBuffer.of(4, 1, 2, 3, 4, 5).toArray(), new Object[] { 2, 3, 4, 5 });
  }

}
