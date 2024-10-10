package gusev.dmitry.jtils.graph.typeI.interfaces;

/**
 * Digraph (directed graph) entity interface (common methods).
 * @author Gusev Dmitry (Dmitry)
 * @version 1.0 (DATE: 04.11.2014)
*/

public interface DigraphInterface {

    /** Tests whether current digraph is empty. Returns true if so, false if not. */
    public boolean isEmpty();

    /** Returns the number of distinct vertices in the current digraph. */
    public int size();

    /** Returns whether u is joined to v by an edge. returns true if so, false if not. */
    //public boolean isAdjacent(Object v, Object w);
    public boolean isAdjacent(int u, int v);

    /**
     * Inserts edge from v to w. Constructs from v to w, if no such edge is already present and
     * throws an exception otherwise.
     * Precondition: v, w are vertices in the current digraph.
    */
    //public void insertEdge(Object v, Object w);
    public void insertEdge(int v, int w);

    /**
     * Inserts vertex into digraph. Inserts a new vertex if that vertex is not already present and raises
     * exception if no vertex is inserted, since it is already a vertex of the current digraph.
    */
    //public void insertVertex(Object v);
    public void insertVertex(int index);

    /**
     * Removes vertex from current digraph if present, along with all incident edges. Raises an
     * exception if that vertex is not in the present digraph.
    */
    //public void eraseVertex(Object v);
    public void eraseVertex(int v);

    /**
     * Removes edge from v to w if currently present in digraph.
     * Precondition: v, w are vertices in current digraph.
    */
    //public void eraseEdge(Object v, Object w);
    public void eraseEdge(int v, int w);

    /** Outputs specifications of the current digraph. */
    //public void output();

}