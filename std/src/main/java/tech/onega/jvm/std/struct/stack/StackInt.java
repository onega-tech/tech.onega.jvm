package tech.onega.jvm.std.struct.stack;

import java.util.Arrays;
import tech.onega.jvm.std.annotation.Mutable;
import tech.onega.jvm.std.annotation.NotThreadSafe;

@Mutable
@NotThreadSafe
final public class StackInt {

  private final int[] data;

  private int size = 0;

  public StackInt(final int capacity) {
    data = new int[capacity];
  }

  public int capacity() {
    return data.length;
  }

  public boolean empty() {
    return size == 0;
  }

  public int pop() {
    return data[--size];
  }

  public StackInt push(final int value) {
    data[size++] = value;
    return this;
  }

  public int size() {
    return size;
  }

  public int[] toArray() {
    return Arrays.copyOf(data, size);
  }

  @Override
  public String toString() {
    return Arrays.toString(toArray());
  }

}