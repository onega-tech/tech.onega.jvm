package tech.onega.jvm.std.struct.graph;

import org.testng.annotations.Test;
import tech.onega.jvm.std.struct.list.IList;
import tech.onega.jvm.std.validate.Check;

public class IGraphTest {

  @Test
  public void testEdges() {
    Check.equals(
      IGraph.of(GraphEdge.of(1, 2), GraphEdge.of(2, 3)).edges(),
      IList.of(GraphEdge.of(1, 2), GraphEdge.of(2, 3)));
  }

  @Test
  public void testEquals() {
    Check.isTrue(IGraph.of(GraphEdge.of(1, 2)).equals(IGraph.of(GraphEdge.of(1, 2))));
  }

  @Test
  public void testHashCode() {
    Check.isTrue(IGraph.of(GraphEdge.of(1, 2)).hashCode() == IGraph.of(GraphEdge.of(1, 2)).hashCode());
  }

  @Test
  public void testPath() {
    /**
     *      A
     *     / \
     *    B   D
     *     \   \
     *      C - E
     *
     *   A -> B -> C -> E
     *   A -> D -> E
     */
    final var graph = MGraph.<String>of(
      GraphEdge.of("A", "B"),
      GraphEdge.of("B", "C"),
      GraphEdge.of("C", "E"),
      GraphEdge.of("A", "D"),
      GraphEdge.of("D", "E"))
      .toIGraph();
    Check.equals(graph.path("A", "A"), IList.empty());
    Check.equals(graph.path("A", "B"), IList.of("A", "B"));
    Check.equals(graph.path("A", "C"), IList.of("A", "B", "C"));
    Check.equals(graph.path("A", "D"), IList.of("A", "D"));
    Check.equals(graph.path("B", "C"), IList.of("B", "C"));
    Check.equals(graph.path("A", "E"), IList.of("A", "D", "E"));
    Check.equals(graph.path("D", "B"), IList.empty());
    Check.equals(graph.path("B", "E"), IList.of("B", "C", "E"));
  }

  @Test
  public void testVertex() {
    Check.equals(
      IGraph.of(GraphEdge.of(1, 2), GraphEdge.of(2, 3)).vertex(),
      IList.of(1, 2, 3));
  }

}
