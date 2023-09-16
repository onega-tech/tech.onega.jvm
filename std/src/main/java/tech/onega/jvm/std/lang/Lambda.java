package tech.onega.jvm.std.lang;

public interface Lambda {

  @FunctionalInterface
  public interface Consumer<P, E extends Throwable> {

    void invoke(P param) throws E;

  }

  @FunctionalInterface
  public interface Consumer2<P1, P2, E extends Throwable> {

    void invoke(P1 param1, P2 param2) throws E;

  }

  @FunctionalInterface
  public interface Consumer3<P1, P2, P3, E extends Throwable> {

    void invoke(P1 param1, P2 param2, P3 param3) throws E;

  }

  @FunctionalInterface
  public interface Function<P, R, E extends Throwable> {

    R invoke(P param) throws E;

  }

  @FunctionalInterface
  public interface Function2<P1, P2, R, E extends Throwable> {

    R invoke(P1 param1, P2 param2) throws E;

  }

  @FunctionalInterface
  public interface Function3<P1, P2, P3, R, E extends Throwable> {

    R invoke(P1 param1, P2 param2, P3 param3) throws E;

  }

  @FunctionalInterface
  public interface Supplier<R, E extends Throwable> {

    R invoke() throws E;

  }

  @FunctionalInterface
  public interface Void<E extends Throwable> {

    void invoke() throws E;

  }

}
