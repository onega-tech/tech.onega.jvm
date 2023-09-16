package tech.onega.jvm.std.struct.graph;

import org.testng.annotations.Test;
import tech.onega.jvm.std.struct.set.MSet;
import tech.onega.jvm.std.validate.Check;

public class MGraphTest {

  @Test
  public void testAddEdge() {
    final var graph = MGraph.<Integer>create();
    Check.isTrue(graph.edges().isEmpty());
    Check.isTrue(graph.vertex().isEmpty());
    final var edge = GraphEdge.<Integer>of(1, 2);
    graph.addEdge(edge);
    Check.isTrue(graph.edges().contains(edge));
    Check.isTrue(graph.vertex().contains(edge.start()));
    Check.isTrue(graph.vertex().contains(edge.end()));
    Check.isFalse(graph.edges().isEmpty());
    Check.isFalse(graph.vertex().isEmpty());
    Check.equals(graph.edges().size(), 1);
    Check.equals(graph.vertex().size(), 2);
    Check.equals(graph.edges(), MSet.of(edge));
    Check.equals(graph.vertex(), MSet.of(edge.start(), edge.end()));
  }

  @Test
  public void testClear() {
    final var graph = MGraph.<Integer>create();
    graph.addEdge(1, 2);
    Check.isFalse(graph.edges().isEmpty());
    Check.isFalse(graph.vertex().isEmpty());
    graph.clear();
    Check.isTrue(graph.edges().isEmpty());
    Check.isTrue(graph.vertex().isEmpty());
  }

  @Test
  public void testEquals() {
    Check.isTrue(MGraph.of(GraphEdge.of(1, 2)).equals(MGraph.of(GraphEdge.of(1, 2))));
  }

  @Test
  public void testHashCode() {
    Check.isTrue(MGraph.of(GraphEdge.of(1, 2)).hashCode() == MGraph.of(GraphEdge.of(1, 2)).hashCode());
  }

  @Test
  public void testRemoveEdge() {
    final var graph = MGraph.<Integer>create();
    graph
      .addEdge(1, 2)
      .addEdge(1, 3, 0)
      .addEdge(1, 4)
      .addEdge(2, 3);
    Check.equals(graph.vertex(), MSet.of(1, 2, 3, 4));
    Check.equals(graph.edges(),
      MSet.of(GraphEdge.of(1, 2), GraphEdge.of(1, 3), GraphEdge.of(1, 4), GraphEdge.of(2, 3)));
    Check.equals(graph.vertxSize(), 4);
    Check.equals(graph.edgesSize(), 4);
    graph.removeEdge(1, 2);
    Check.equals(graph.edges(), MSet.of(GraphEdge.of(1, 3), GraphEdge.of(1, 4), GraphEdge.of(2, 3)));
    Check.equals(graph.vertex().sort(Integer::compare), MSet.of(1, 2, 3, 4));
    Check.equals(graph.edgesSize(), 3);
    Check.equals(graph.vertxSize(), 4);
  }

  @Test
  public void testRemoveVertx() {
    Check.equals(
      MGraph.of(GraphEdge.of(1, 2), GraphEdge.of(1, 3), GraphEdge.of(2, 3)).removeVertx(1),
      MGraph.of(GraphEdge.of(2, 3)));
  }

}
