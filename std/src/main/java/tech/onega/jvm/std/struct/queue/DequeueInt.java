package tech.onega.jvm.std.struct.queue;

import tech.onega.jvm.std.annotation.Mutable;
import tech.onega.jvm.std.annotation.NotThreadSafe;

@Mutable
@NotThreadSafe
//double ended queue
final public class DequeueInt {

  private int size;

  private int head;

  private int tail;

  private final int[] data;

  public DequeueInt(final int size) {
    data = new int[this.size = size];
  }

  public DequeueInt addFirst(final int value) {
    data[head] = value;
    if (--head < 0) {
      head = size - 1;
    }
    return this;
  }

  public DequeueInt addLast(final int value) {
    if (++tail == size) {
      tail = 0;
    }
    data[tail] = value;
    return this;
  }

  public boolean empty() {
    return head == tail;
  }

  public int removeFirst() {
    if (++head == size) {
      head = 0;
    }
    return data[head];
  }

  public int removeLast() {
    final int ret = data[tail];
    if (--tail < 0) {
      tail = size - 1;
    }
    return ret;
  }

}
