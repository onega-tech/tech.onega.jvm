package tech.onega.jvm.std.struct.queue;

import tech.onega.jvm.std.annotation.Mutable;
import tech.onega.jvm.std.annotation.NotThreadSafe;

@Mutable
@NotThreadSafe
final public class QueueInt {

  private final int size;

  private int head;

  private int tail;

  private final int[] data;

  public QueueInt(final int size) {
    data = new int[size];
    this.size = size;
  }

  public QueueInt add(final int value) {
    if (++tail == size) {
      tail = 0;
    }
    data[tail] = value;
    return this;
  }

  public boolean empty() {
    return head == tail;
  }

  public int remove() {
    if (++head == size) {
      head = 0;
    }
    return data[head];
  }

}
