package tech.onega.jvm.std.struct.graph;

import java.util.stream.Stream;
import tech.onega.jvm.std.annotation.Mutable;
import tech.onega.jvm.std.lang.Equals;
import tech.onega.jvm.std.struct.set.ISet;
import tech.onega.jvm.std.struct.set.MSet;
import tech.onega.jvm.std.struct.vector.VectorUtils;

@Mutable
final public class MGraph<V> {

  public static <V> MGraph<V> create() {
    return new MGraph<>(VectorUtils.DEFAULT_INITIAL_CAPACITY);
  }

  public static <V> MGraph<V> create(final int edgesInitialSize) {
    return new MGraph<>(edgesInitialSize);
  }

  public static <V> MGraph<V> empty() {
    return new MGraph<>(VectorUtils.DEFAULT_INITIAL_CAPACITY);
  }

  @SafeVarargs
  public static <V> MGraph<V> of(final GraphEdge<V>... edges) {
    return new MGraph<V>(edges.length).addEdges(edges);
  }

  private final MSet<GraphEdge<V>> edgeSet;

  private MGraph(final int edgesInitialSize) {
    this.edgeSet = MSet.create(edgesInitialSize);
  }

  public MGraph<V> addEdge(final GraphEdge<V> edge) {
    edgeSet.add(edge);
    return this;
  }

  public MGraph<V> addEdge(final V start, final V end) {
    return addEdge(GraphEdge.of(start, end));
  }

  public MGraph<V> addEdge(final V start, final V end, final int weight) {
    return addEdge(GraphEdge.of(start, end, weight));
  }

  @SuppressWarnings("unchecked")
  public MGraph<V> addEdges(final GraphEdge<V>... edges) {
    edgeSet.addAll(edges);
    return this;
  }

  public MGraph<V> clear() {
    this.edgeSet.clear();
    return this;
  }

  public MSet<GraphEdge<V>> edges() {
    return edgeSet;
  }

  public int edgesSize() {
    return edgeSet.size();
  }

  @Override
  public boolean equals(final Object obj) {
    return Equals.check(this, obj,
      f -> new Object[] { f.edgeSet });
  }

  @Override
  public int hashCode() {
    return edgeSet.hashCode();
  }

  public MGraph<V> removeEdge(final GraphEdge<V> edge) {
    edgeSet.remove(edge);
    return this;
  }

  public MGraph<V> removeEdge(final V start, final V end) {
    return removeEdge(GraphEdge.of(start, end));
  }

  public MGraph<V> removeVertx(final V vertx) {
    edgeSet.filter(edge -> (!Equals.yes(edge.start(), vertx) && !Equals.yes(edge.end(), vertx)));
    return this;
  }

  public IGraph<V> toIGraph() {
    return IGraph.create(this);
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder(256);
    builder.append("graph:\n");
    for (final GraphEdge<V> edge : edgeSet) {
      builder.append("  ").append(edge).append("\n");
    }
    return builder.toString();
  }

  public ISet<V> vertex() {
    if (edgeSet.isEmpty()) {
      return ISet.empty();
    }
    return edgeSet.stream()
      .flatMap(edge -> Stream.of(edge.start(), edge.end()))
      .collect(ISet.collector(edgeSet.size() * 2));
  }

  public int vertxSize() {
    return vertex().size();
  }

}