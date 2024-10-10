package gusev.dmitry.jtils.graph.typeII;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * A directed graph data structure.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 13.11.2014)
*/

public class WDigraph<T> {
  /** Color used to mark unvisited nodes */
  public static final int VISIT_COLOR_WHITE = 1;

  /** Color used to mark nodes as they are first visited in DFS order */
  public static final int VISIT_COLOR_GREY = 2;

  /** Color used to mark nodes after descendants are completely visited */
  public static final int VISIT_COLOR_BLACK = 3;

  /** Vector<DigraphVertex> of graph verticies */
  private List<DigraphVertex<T>> verticies;

  /** Vector<DigraphEdge> of edges in the graph */
  private List<DigraphEdge<T>> edges;

  /** The vertex identified as the root of the graph */
  private DigraphVertex<T> rootVertex;

  /**
   * Construct a new graph without any vertices or edges
   */
  public WDigraph() {
    verticies = new ArrayList<DigraphVertex<T>>();
    edges = new ArrayList<DigraphEdge<T>>();
  }

  /**
   * Are there any verticies in the graph
   *
   * @return true if there are no verticies in the graph
   */
  public boolean isEmpty() {
    return verticies.size() == 0;
  }

  /**
   * Add a vertex to the graph
   *
   * @param v
   *          the DigraphVertex to add
   * @return true if the vertex was added, false if it was already in the graph.
   */
  public boolean addVertex(DigraphVertex<T> v) {
    boolean added = false;
    if (verticies.contains(v) == false) {
      added = verticies.add(v);
    }
    return added;
  }

  /**
   * Get the vertex count.
   *
   * @return the number of verticies in the graph.
   */
  public int size() {
    return verticies.size();
  }

  /**
   * Get the root vertex
   *
   * @return the root vertex if one is set, null if no vertex has been set as
   *         the root.
   */
  public DigraphVertex<T> getRootVertex() {
    return rootVertex;
  }

  /**
   * Set a root vertex. If root does no exist in the graph it is added.
   *
   * @param root -
   *          the vertex to set as the root and optionally add if it does not
   *          exist in the graph.
   */
  public void setRootVertex(DigraphVertex<T> root) {
    this.rootVertex = root;
    if (verticies.contains(root) == false)
      this.addVertex(root);
  }

  /**
   * Get the given DigraphVertex.
   *
   * @param n
   *          the index [0, size()-1] of the DigraphVertex to access
   * @return the nth DigraphVertex
   */
  public DigraphVertex<T> getVertex(int n) {
    return verticies.get(n);
  }

  /**
   * Get the graph verticies
   *
   * @return the graph verticies
   */
  public List<DigraphVertex<T>> getVerticies() {
    return this.verticies;
  }

  /**
   * Insert a directed, weighted DigraphEdge<T> into the graph.
   *
   * @param from -
   *          the DigraphEdge<T> starting vertex
   * @param to -
   *          the DigraphEdge<T> ending vertex
   * @param cost -
   *          the DigraphEdge<T> weight/cost
   * @return true if the DigraphEdge<T> was added, false if from already has this DigraphEdge<T>
   * @throws IllegalArgumentException
   *           if from/to are not verticies in the graph
   */
  public boolean addEdge(DigraphVertex<T> from, DigraphVertex<T> to, int cost) throws IllegalArgumentException {
    if (verticies.contains(from) == false)
      throw new IllegalArgumentException("from is not in graph");
    if (verticies.contains(to) == false)
      throw new IllegalArgumentException("to is not in graph");

    DigraphEdge<T> e = new DigraphEdge<T>(from, to, cost);
    if (from.findEdge(to) != null)
      return false;
    else {
      from.addEdge(e);
      to.addEdge(e);
      edges.add(e);
      return true;
    }
  }

  /**
   * Insert a bidirectional DigraphEdge<T> in the graph
   *
   * @param from -
   *          the DigraphEdge<T> starting vertex
   * @param to -
   *          the DigraphEdge<T> ending vertex
   * @param cost -
   *          the DigraphEdge<T> weight/cost
   * @return true if edges between both nodes were added, false otherwise
   * @throws IllegalArgumentException
   *           if from/to are not verticies in the graph
   */
  public boolean insertBiEdge(DigraphVertex<T> from, DigraphVertex<T> to, int cost)
      throws IllegalArgumentException {
    return addEdge(from, to, cost) && addEdge(to, from, cost);
  }

  /**
   * Get the graph edges
   *
   * @return the graph edges
   */
  public List<DigraphEdge<T>> getEdges() {
    return this.edges;
  }

  /**
   * Remove a vertex from the graph
   *
   * @param v
   *          the DigraphVertex to remove
   * @return true if the DigraphVertex was removed
   */
  public boolean removeVertex(DigraphVertex<T> v) {
    if (!verticies.contains(v))
      return false;

    verticies.remove(v);
    if (v == rootVertex)
      rootVertex = null;

    // Remove the edges associated with v
    for (int n = 0; n < v.getOutgoingEdgeCount(); n++) {
      DigraphEdge<T> e = v.getOutgoingEdge(n);
      v.remove(e);
      DigraphVertex<T> to = e.getTo();
      to.remove(e);
      edges.remove(e);
    }
    for (int n = 0; n < v.getIncomingEdgeCount(); n++) {
      DigraphEdge<T> e = v.getIncomingEdge(n);
      v.remove(e);
      DigraphVertex<T> predecessor = e.getFrom();
      predecessor.remove(e);
    }
    return true;
  }

  /**
   * Remove an DigraphEdge<T> from the graph
   *
   * @param from -
   *          the DigraphEdge<T> starting vertex
   * @param to -
   *          the DigraphEdge<T> ending vertex
   * @return true if the DigraphEdge<T> exists, false otherwise
   */
  public boolean removeEdge(DigraphVertex<T> from, DigraphVertex<T> to) {
    DigraphEdge<T> e = from.findEdge(to);
    if (e == null)
      return false;
    else {
      from.remove(e);
      to.remove(e);
      edges.remove(e);
      return true;
    }
  }

  /**
   * Clear the mark state of all verticies in the graph by calling clearMark()
   * on all verticies.
   *
   * @see DigraphVertex#clearMark()
   */
  public void clearMark() {
    for (DigraphVertex<T> w : verticies)
      w.clearMark();
  }

  /**
   * Clear the mark state of all edges in the graph by calling clearMark() on
   * all edges.
   */
  public void clearEdges() {
    for (DigraphEdge<T> e : edges)
      e.clearMark();
  }

  /**
   * Perform a depth first serach using recursion.
   *
   * @param v -
   *          the DigraphVertex to start the search from
   * @param visitor -
   *          the vistor to inform prior to
   * @see Visitor#visit(WDigraph, DigraphVertex)
   */
  public void depthFirstSearch(DigraphVertex<T> v, final Visitor<T> visitor) {
    VisitorEX<T, RuntimeException> wrapper = new VisitorEX<T, RuntimeException>() {
      public void visit(WDigraph<T> g, DigraphVertex<T> v) throws RuntimeException {
        if (visitor != null)
          visitor.visit(g, v);
      }
    };
    this.depthFirstSearch(v, wrapper);
  }

  /**
   * Perform a depth first serach using recursion. The search may be cut short
   * if the visitor throws an exception.
   *
   * @param <E>
   *
   * @param v -
   *          the DigraphVertex to start the search from
   * @param visitor -
   *          the vistor to inform prior to
   * @see Visitor#visit(WDigraph, DigraphVertex)
   * @throws E
   *           if visitor.visit throws an exception
   */
  public <E extends Exception> void depthFirstSearch(DigraphVertex<T> v, VisitorEX<T, E> visitor) throws E {
    if (visitor != null)
      visitor.visit(this, v);
    v.visit();
    for (int i = 0; i < v.getOutgoingEdgeCount(); i++) {
      DigraphEdge<T> e = v.getOutgoingEdge(i);
      if (!e.getTo().visited()) {
        depthFirstSearch(e.getTo(), visitor);
      }
    }
  }

  /**
   * Perform a breadth first search of this graph, starting at v.
   *
   * @param v -
   *          the search starting point
   * @param visitor -
   *          the vistor whose vist method is called prior to visting a vertex.
   */
  public void breadthFirstSearch(DigraphVertex<T> v, final Visitor<T> visitor) {
    VisitorEX<T, RuntimeException> wrapper = new VisitorEX<T, RuntimeException>() {
      public void visit(WDigraph<T> g, DigraphVertex<T> v) throws RuntimeException {
        if (visitor != null)
          visitor.visit(g, v);
      }
    };
    this.breadthFirstSearch(v, wrapper);
  }

  /**
   * Perform a breadth first search of this graph, starting at v. The vist may
   * be cut short if visitor throws an exception during a vist callback.
   *
   * @param <E>
   *
   * @param v -
   *          the search starting point
   * @param visitor -
   *          the vistor whose vist method is called prior to visting a vertex.
   * @throws E
   *           if vistor.visit throws an exception
   */
  public <E extends Exception> void breadthFirstSearch(DigraphVertex<T> v, VisitorEX<T, E> visitor)
      throws E {
    LinkedList<DigraphVertex<T>> q = new LinkedList<DigraphVertex<T>>();

    q.add(v);
    if (visitor != null)
      visitor.visit(this, v);
    v.visit();
    while (q.isEmpty() == false) {
      v = q.removeFirst();
      for (int i = 0; i < v.getOutgoingEdgeCount(); i++) {
        DigraphEdge<T> e = v.getOutgoingEdge(i);
        DigraphVertex<T> to = e.getTo();
        if (!to.visited()) {
          q.add(to);
          if (visitor != null)
            visitor.visit(this, to);
          to.visit();
        }
      }
    }
  }

  /**
   * Find the spanning tree using a DFS starting from v.
   *
   * @param v -
   *          the vertex to start the search from
   * @param visitor -
   *          visitor invoked after each vertex is visited and an edge is added
   *          to the tree.
   */
  public void dfsSpanningTree(DigraphVertex<T> v, DFSVisitor<T> visitor) {
    v.visit();
    if (visitor != null)
      visitor.visit(this, v);

    for (int i = 0; i < v.getOutgoingEdgeCount(); i++) {
      DigraphEdge<T> e = v.getOutgoingEdge(i);
      if (!e.getTo().visited()) {
        if (visitor != null)
          visitor.visit(this, v, e);
        e.mark();
        dfsSpanningTree(e.getTo(), visitor);
      }
    }
  }

  /**
   * Search the verticies for one with name.
   *
   * @param name -
   *          the vertex name
   * @return the first vertex with a matching name, null if no matches are found
   */
  public DigraphVertex<T> findVertexByName(String name) {
    DigraphVertex<T> match = null;
    for (DigraphVertex<T> v : verticies) {
      if (name.equals(v.getName())) {
        match = v;
        break;
      }
    }
    return match;
  }

  /**
   * Search the verticies for one with data.
   *
   * @param data -
   *          the vertex data to match
   * @param compare -
   *          the comparator to perform the match
   * @return the first vertex with a matching data, null if no matches are found
   */
  public DigraphVertex<T> findVertexByData(T data, Comparator<T> compare) {
    DigraphVertex<T> match = null;
    for (DigraphVertex<T> v : verticies) {
      if (compare.compare(data, v.getData()) == 0) {
        match = v;
        break;
      }
    }
    return match;
  }

  /**
   * Search the graph for cycles. In order to detect cycles, we use a modified
   * depth first search called a colored DFS. All nodes are initially marked
   * white. When a node is encountered, it is marked grey, and when its
   * descendants are completely visited, it is marked black. If a grey node is
   * ever encountered, then there is a cycle.
   *
   * @return the edges that form cycles in the graph. The array will be empty if
   *         there are no cycles.
   */
  public DigraphEdge<T>[] findCycles() {
    ArrayList<DigraphEdge<T>> cycleEdges = new ArrayList<DigraphEdge<T>>();
    // Mark all verticies as white
    for (int n = 0; n < verticies.size(); n++) {
      DigraphVertex<T> v = getVertex(n);
      v.setMarkState(VISIT_COLOR_WHITE);
    }
    for (int n = 0; n < verticies.size(); n++) {
      DigraphVertex<T> v = getVertex(n);
      visit(v, cycleEdges);
    }

    DigraphEdge<T>[] cycles = new DigraphEdge[cycleEdges.size()];
    cycleEdges.toArray(cycles);
    return cycles;
  }

  private void visit(DigraphVertex<T> v, ArrayList<DigraphEdge<T>> cycleEdges) {
    v.setMarkState(VISIT_COLOR_GREY);
    int count = v.getOutgoingEdgeCount();
    for (int n = 0; n < count; n++) {
      DigraphEdge<T> e = v.getOutgoingEdge(n);
      DigraphVertex<T> u = e.getTo();
      if (u.getMarkState() == VISIT_COLOR_GREY) {
        // A cycle DigraphEdge<T>
        cycleEdges.add(e);
      } else if (u.getMarkState() == VISIT_COLOR_WHITE) {
        visit(u, cycleEdges);
      }
    }
    v.setMarkState(VISIT_COLOR_BLACK);
  }

  public String toString() {
    StringBuffer tmp = new StringBuffer("WDigraph[");
    for (DigraphVertex<T> v : verticies)
      tmp.append(v);
    tmp.append(']');
    return tmp.toString();
  }

}