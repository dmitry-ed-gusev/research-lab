package gusev.dmitry.jtils.graph.typeI.interfaces;

import gusev.dmitry.jtils.graph.typeI.WeightedVertex;

/**
 * Interface for weighted digraph implementation.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 13.11.2014)
*/

public interface WeightedDigraphInteface {

    /** Tests whether current weighted digraph is empty. Returns true if so, false if not. */
    public boolean isEmpty();

    /** Returns the number of distinct vertices in the current weighted digraph. */
    public int size();

    /** Retrieves sum of the weights of all of the edges of the current weighted digraph. */
    public int totalWeight();

    /** Returns whether v is joined to w by a weighted edge. Returns true if so, false if not. */
    public boolean isAdjacent(WeightedVertex v, WeightedVertex w);

    /**
     * Inserts vertex into weighted digraph. Inserts a new vertex if that vertex is not already present and raises
     * exception if no vertex is inserted, since it is already a vertex of the current weighted digraph.
    */
    public void insertVertex(WeightedVertex v);

    /**
     * Inserts edge from v to w. Constructs weighted edge from v to w, if no such edge is already present and
     * throws an exception if otherwise.
     * Precondition: v, w are vertices in the current weighted digraph.
    */
    public void insertEdge(WeightedVertex v, WeightedVertex w);

    /**
     * Removes vertex from current weighted digraph if present, along with all incident edges. Raises an
     * exception if that vertex is not present in the current weighted digraph.
    */
    public void eraseVertex(WeightedVertex v);

    /**
     * Removes weighted edge from v to w if currently present in weighted digraph and eliminates the weight of that edge.
     * Precondition: v, w are vertices in current weighted digraph.
    */
    public void eraseEdge(WeightedVertex v, WeightedVertex w);

}