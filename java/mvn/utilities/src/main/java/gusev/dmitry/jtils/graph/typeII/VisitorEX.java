package gusev.dmitry.jtils.graph.typeII;

/**
 * A graph visitor interface that can throw an exception during a visit callback.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 13.11.2014)
*/

interface VisitorEX<T, E extends Exception> {
  /**
   * Called by the graph traversal methods when a vertex is first visited.
   *
   * @param g -
   *          the graph
   * @param v -
   *          the vertex being visited.
   * @throws E
   *           exception for any error
   */
  public void visit(WDigraph<T> g, DigraphVertex<T> v) throws E;
}