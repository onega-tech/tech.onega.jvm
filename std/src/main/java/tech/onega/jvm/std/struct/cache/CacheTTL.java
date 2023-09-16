package tech.onega.jvm.std.struct.cache;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import tech.onega.jvm.std.lang.Lambda;

final public class CacheTTL<V, E extends Throwable> {

  private final Duration duration;

  private final AtomicReference<V> valueRef = new AtomicReference<>();

  private final AtomicLong expiredRef = new AtomicLong(0);

  private final Lambda.Supplier<V, E> supplier;

  public CacheTTL(Duration duration, Lambda.Supplier<V, E> supplier) {
    this.duration = duration;
    this.supplier = supplier;
  }

  public V get() throws E {
    V value = valueRef.get();
    var now = System.currentTimeMillis();
    if (value == null || now > expiredRef.get()) {
      value = supplier.invoke();
      valueRef.set(value);
      expiredRef.set(now + duration.toMillis());
    }
    return value;
  }

}
