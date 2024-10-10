package gusev.dmitry.jtils.graph.typeII;

/**
 * A graph visitor interface.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 13.11.2014)
 */

interface Visitor<T> {
  /**
   * Called by the graph traversal methods when a vertex is first visited.
   *
   * @param g -
   *          the graph
   * @param v -
   *          the vertex being visited.
   */
  public void visit(WDigraph<T> g, DigraphVertex<T> v);
}
