package gusev.dmitry.jtils.graph.typeII;

/**
 * A spanning tree visitor callback interface
 * @see WDigraph#dfsSpanningTree(DigraphVertex, DFSVisitor)
 *
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 13.11.2014)
*/

interface DFSVisitor<T> {
  /**
   * Called by the graph traversal methods when a vertex is first visited.
   *
   * @param g -
   *          the graph
   * @param v -
   *          the vertex being visited.
   */
  public void visit(WDigraph<T> g, DigraphVertex<T> v);

  /**
   * Used dfsSpanningTree to notify the visitor of each outgoing edge to an
   * unvisited vertex.
   *
   * @param g -
   *          the graph
   * @param v -
   *          the vertex being visited
   * @param e -
   *          the outgoing edge from v
   */
  public void visit(WDigraph<T> g, DigraphVertex<T> v, DigraphEdge<T> e);
}