package tech.onega.jvm.std.lang;

import tech.onega.jvm.std.annotation.Mutable;
import tech.onega.jvm.std.annotation.NotThreadSafe;

@Mutable
@NotThreadSafe
final public class Ref<T> {

  public static <T> Ref<T> of(final T val) {
    return new Ref<>(val);
  }

  private T val;

  private Ref(final T val) {
    this.val = val;
  }

  public T get() {
    return val;
  }

  public T getAndSet(final T newValue) {
    final T oldValue = val;
    val = newValue;
    return oldValue;
  }

  public T set(final T newVal) {
    final T old = newVal;
    this.val = newVal;
    return old;
  }

}
