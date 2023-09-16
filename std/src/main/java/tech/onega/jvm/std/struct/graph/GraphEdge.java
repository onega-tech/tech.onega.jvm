package tech.onega.jvm.std.struct.graph;

import tech.onega.jvm.std.annotation.Immutable;
import tech.onega.jvm.std.annotation.ThreadSafe;
import tech.onega.jvm.std.lang.Equals;
import tech.onega.jvm.std.struct.hash.Hash;

@Immutable
@ThreadSafe
final public class GraphEdge<V> {

  public static <V> GraphEdge<V> of(final V start, final V end) {
    return new GraphEdge<>(start, end, 1);
  }

  public static <V> GraphEdge<V> of(final V start, final V end, final int distance) {
    return new GraphEdge<>(start, end, distance);
  }

  private final V start;

  private final V end;

  private final int distance;

  private final int hashCode;

  private GraphEdge(final V start, final V end, final int distance) {
    this.start = start;
    this.end = end;
    this.distance = Math.max(1, distance);
    this.hashCode = Hash.codes(start, end);
  }

  public int distance() {
    return distance;
  }

  public V end() {
    return end;
  }

  @Override
  public boolean equals(final Object obj) {
    return Equals.check(this, obj, f -> new Object[] { f.start, f.end });
  }

  @Override
  public int hashCode() {
    return hashCode;
  }

  public V start() {
    return start;
  }

  @Override
  public String toString() {
    return String.format("%s -(%s)-> %s", start, distance, end);
  }

}
