package tech.onega.jvm.std.struct.stream;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import tech.onega.jvm.std.struct.array.ArrayUtils;
import tech.onega.jvm.std.struct.vector.Vector;

final public class StreamUtils {

  public static final Set<Characteristics> EMPTY_COLLECTOR_CHARACTERISTICS = Collections
    .unmodifiableSet(new java.util.HashSet<>(0));

  public static <V> Stream<V> createStream(final Iterable<V> iterable) {
    if (iterable instanceof Vector) {
      return ((Vector<V>) iterable).stream();
    }
    else if (iterable instanceof Collection) {
      return ((Collection<V>) iterable).stream();
    }
    else {
      return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(iterable.iterator(), Spliterator.ORDERED),
        false);
    }
  }

  public static <V> Stream<V> createStream(final Iterator<V> iterator) {
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
  }

  public static <T> Stream<T> createStream(final T[] data) {
    return createStream(data, data.length, 0);
  }

  public static <T> Stream<T> createStream(final T[] data, final int limit, final int offset) {
    return StreamSupport.stream(ArrayUtils.spliterator(data, limit, offset), false);
  }

  public static <V, R> Collector<V, R, R> selfCollector(final Supplier<R> accumulatorFactory,
    final BiConsumer<R, V> accumulate) {
    return simpleCollector(accumulatorFactory, accumulate, a -> a);
  }

  public static <V, A, R> Collector<V, A, R> simpleCollector(final Supplier<A> accumulatorFactory,
    final BiConsumer<A, V> accumulate, final Function<A, R> resultFactory) {
    return new Collector<>() {

      @Override
      public BiConsumer<A, V> accumulator() {
        return (a, p) -> accumulate.accept(a, p);
      }

      @Override
      public Set<Characteristics> characteristics() {
        return StreamUtils.EMPTY_COLLECTOR_CHARACTERISTICS;
      }

      @Override
      public BinaryOperator<A> combiner() {
        return null;
      }

      @Override
      public Function<A, R> finisher() {
        return resultFactory;
      }

      @Override
      public Supplier<A> supplier() {
        return accumulatorFactory;
      }

    };
  }

}
