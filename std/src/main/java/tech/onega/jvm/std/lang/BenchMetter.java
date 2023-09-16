package tech.onega.jvm.std.lang;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import tech.onega.jvm.std.annotation.NotThreadSafe;
import tech.onega.jvm.std.struct.array.ArrayUtils;

@NotThreadSafe
final public class BenchMetter {

  public static <E extends Throwable> Duration bench(final Lambda.Void<E> func) throws E {
    var st = System.nanoTime();
    func.invoke();
    return Duration.ofNanos(System.nanoTime() - st);
  }

  private long[] data;

  private int size;

  private final int maxCapacity;

  public BenchMetter() {
    this(16, Integer.MAX_VALUE - 8);
  }

  public BenchMetter(int initialCpacity, int maxCapacity) {
    data = new long[initialCpacity];
    this.maxCapacity = maxCapacity;
  }

  public long avgMillis() {
    return TimeUnit.NANOSECONDS.toMillis(avgNano());
  }

  public long avgNano() {
    final long[] stat = Arrays.copyOf(data, size);
    Arrays.sort(stat);
    return stat[size / 2];
  }

  public void clear() {
    size = 0;
  }

  public <E extends Throwable> void mark(Lambda.Void<E> func) throws E {
    var st = System.nanoTime();
    func.invoke();
    var dur = System.nanoTime() - st;
    data[size++] = dur;
    if (size == data.length) {
      data = ArrayUtils.growLong(data, size + 1, maxCapacity);
    }
  }

  public int size() {
    return size;
  }

}
