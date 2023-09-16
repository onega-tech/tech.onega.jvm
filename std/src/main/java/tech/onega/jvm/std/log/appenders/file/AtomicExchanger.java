package tech.onega.jvm.std.log.appenders.file;

import java.util.concurrent.atomic.AtomicReference;

final class AtomicExchanger<V> {

  private final AtomicReference<V> ref = new AtomicReference<>(null);

  public V exchange(final V newValue) {
    V result = null;
    while ((result = this.ref.getAndSet(newValue)) == newValue) {
      Thread.yield();
    }
    return result;
  }

}
