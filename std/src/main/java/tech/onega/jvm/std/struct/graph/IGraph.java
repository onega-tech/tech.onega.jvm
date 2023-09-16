package tech.onega.jvm.std.struct.graph;

import java.util.Arrays;
import java.util.Iterator;
import tech.onega.jvm.std.annotation.Copy;
import tech.onega.jvm.std.annotation.Immutable;
import tech.onega.jvm.std.annotation.Unsafe;
import tech.onega.jvm.std.lang.Equals;
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.struct.map.IMap;
import tech.onega.jvm.std.struct.map.MMap;
import tech.onega.jvm.std.struct.range.RangeMinimumQuery;
import tech.onega.jvm.std.struct.set.ISet;
import tech.onega.jvm.std.struct.set.MSet;
import tech.onega.jvm.std.struct.stack.StackInt;

@Immutable
final public class IGraph<V> {

  private static class AdjacencyMatrix {

    private final int[] head;

    private final int[] next;

    private final int[] vertx;

    private final int[] distance;

    private int cnt = 1;

    public AdjacencyMatrix(final int vertxCount, final int edgeCount) {
      head = new int[vertxCount];
      next = new int[edgeCount + 1];
      vertx = new int[edgeCount + 1];
      distance = new int[edgeCount + 1];
    }

    public void add(final int start, final int end, final int distance) {
      next[cnt] = head[start];
      vertx[cnt] = end;
      this.distance[cnt] = distance;
      head[start] = cnt++;
    }
    /*/
    public int edgeCount() {
      return vertx.length - 1;
    }/*/

    public Iterable<Integer> indexes(final int vertx) {
      return () -> new Iterator<>() {

        private int i = head[vertx];

        @Override
        public boolean hasNext() {
          return i != 0;
        }

        @Override
        public Integer next() {
          final int r = i;
          i = next[i];
          return r;
        }

      };
    }

    public int vertxCount() {
      return head.length;
    }

  }

  private static class Dijkstra {

    private final static int INF = Integer.MAX_VALUE / 2;

    public static StackInt path(
      final int start,
      final int end,
      final AdjacencyMatrix adjacencyMatrix) {
      final int vertexSize = adjacencyMatrix.vertxCount();
      final int[] distances = new int[vertexSize];
      final int[] previous = new int[vertexSize];
      final RangeMinimumQuery rmq = new RangeMinimumQuery(vertexSize, INF);
      final boolean[] used = new boolean[vertexSize];
      Arrays.fill(previous, -1);
      Arrays.fill(distances, INF);
      distances[start] = 0;
      rmq.set(start, 0);
      int i = 0;
      for (; i < vertexSize * vertexSize; i++) {
        final int v = rmq.minIndex();
        if (v == -1 || v == end) {
          break;
        }
        used[v] = true;
        rmq.set(v, INF);
        for (final int index : adjacencyMatrix.indexes(v)) {
          final int neighbor = adjacencyMatrix.vertx[index];
          final int distance = adjacencyMatrix.distance[index];
          if (!used[neighbor] && distances[neighbor] > distances[v] + distance) {
            distances[neighbor] = distances[v] + distance;
            rmq.set(neighbor, distances[neighbor]);
            previous[neighbor] = v;
          }
        }
      }
      final StackInt stack = new StackInt(i);
      int cursor = end;
      while (previous[cursor] >= 0) {
        stack.push(cursor);
        cursor = previous[cursor];
      }
      return stack;
    }

  }

  private static final IGraph<Object> EMPTY_REF = new IGraph<>(MGraph.create(0));

  public static <V> IGraph<V> create(@Copy final MGraph<V> graph) {
    return new IGraph<>(graph);
  }

  @SuppressWarnings("unchecked")
  public static <V> IGraph<V> empty() {
    return (IGraph<V>) EMPTY_REF;
  }

  @SafeVarargs
  public static <V> IGraph<V> of(final GraphEdge<V>... edges) {
    return new IGraph<>(MGraph.of(edges));
  }

  private final IList<GraphEdge<V>> edgeList;

  private final IList<V> vertexList;

  private final IMap<V, Integer> idMap;

  private final AdjacencyMatrix adjacencyMatrix;

  @Unsafe
  private IGraph(@Copy final MGraph<V> mGraph) {
    //create vertx
    final ISet<V> vertxSet = mGraph.vertex();
    final int vertxSize = vertxSet.size();
    final Object[] tmpVertxList = new Object[vertxSize];
    final MMap<V, Integer> tmpIdMap = MMap.create(vertxSize, vertxSize);
    int i = 0;
    for (final V vertx : vertxSet) {
      tmpVertxList[i] = vertx;
      tmpIdMap.add(vertx, i);
      i++;
    }
    this.idMap = tmpIdMap.destroy();
    this.vertexList = IList.wrap(tmpVertxList);
    //create edges
    final MSet<GraphEdge<V>> edgeSet = mGraph.edges();
    final int edgeSize = edgeSet.size();
    final Object[] tmpEdgeList = new Object[edgeSize];
    adjacencyMatrix = new AdjacencyMatrix(vertxSize, edgeSize);
    i = 0;
    for (final GraphEdge<V> edge : edgeSet) {
      tmpEdgeList[i] = edge;
      adjacencyMatrix.add(idMap.get(edge.start()), idMap.get(edge.end()), edge.distance());
      i++;
    }
    this.edgeList = IList.wrap(tmpEdgeList);
  }

  public IList<GraphEdge<V>> edges() {
    return edgeList;
  }

  @Override
  public boolean equals(final Object obj) {
    return Equals.check(this, obj, f -> new Object[] { f.edgeList });
  }

  @Override
  public int hashCode() {
    return edgeList.hashCode();
  }

  public IList<V> path(final V start, final V end) {
    final int startId = idMap.get(start);
    final int endId = idMap.get(end);
    final StackInt pathIds = Dijkstra.path(startId, endId, adjacencyMatrix);
    if (pathIds.empty()) {
      return IList.empty();
    }
    final Object[] result = new Object[pathIds.size() + 1];
    result[0] = start;
    int i = 1;
    while (!pathIds.empty()) {
      result[i++] = vertexList.get(pathIds.pop());
    }
    return IList.wrap(result);
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder(256);
    builder.append("graph:\n");
    for (final GraphEdge<V> edge : edgeList) {
      builder.append("  ").append(edge).append("\n");
    }
    return builder.toString();
  }

  public IList<V> vertex() {
    return vertexList;
  }

}