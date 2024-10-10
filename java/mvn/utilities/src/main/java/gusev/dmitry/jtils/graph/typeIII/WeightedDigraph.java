package gusev.dmitry.jtils.graph.typeIII;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Weighted directed graph simple implementation.
 * Some notes about this implementation:
 *  - all vertices of the graph should be reachable (you can add just edge with two vertices, not standalone vertex!)
 *  - vertices numbers must go successively (one-by-one) and start with zero, otherwise method getAdjacenyWeightedMatrix()
 *    will throw an IllegalStateException
 *  - current digraph is weighted and can contain only weighted edges, but if there is an edge without weight, weight for
 *    that edge should be set to value less or equals to zero (used for adjaceny matrix implementation)
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 17.11.2014)
*/

public class WeightedDigraph {

    @SuppressWarnings("ConstantNamingConvention")
    private static final Log log = LogFactory.getLog(WeightedDigraph.class);

    // Graph - list of weighted edges. Package-private access level - for tests.
    Set<WeightedEdge>           edges     = new HashSet<>();
    // All graph vertices with count (implementation detail -  for internal useage). Package-private access level is
    // necessary for tests. SortedMap interface used for sorting keys - we need quick access to first/last keys in a map.
    SortedMap<Integer, Integer> vertices  = new TreeMap<>();

    /***/
    public WeightedDigraph(Set<WeightedEdge> edges) {
        log.debug("WeightedDigraph.constructor() working.");
        if (edges == null) {
            throw new IllegalArgumentException("List of weighted edges is NULL!");
        }
        edges.removeAll(Collections.singleton(null)); // remove all null-elements from collection
        this.edges.addAll(edges);
        // build vertices map and check edges list
        for (WeightedEdge edge : this.edges) {
            this.addVertex(edge.getStart());
            this.addVertex(edge.getEnd());
        }
    }

    /**
     * Package-private access level - for tests.
    */
    void addVertex(int vertex) {
        if (vertex < 0) {
            throw new IllegalArgumentException(String.format("Trying to add invalid vertex [%s]!", vertex));
        }
        // add vertex normally
        if (this.vertices.containsKey(vertex)) {
            this.vertices.put(vertex, this.vertices.get(vertex) + 1);
        } else {
            this.vertices.put(vertex, 1);
        }
    }

    /**
     * Remove vertex from internal vertex list - decremet counter for vertex and phisically
     * remove vertex from map if counter equals to zero (after decrementing). If map doesn't contain
     * vertex - nothing happens.
     * Package-private access level - for tests.
    */
    void removeVertex(int vertex) {
        if (vertex < 0) {
            throw new IllegalArgumentException(String.format("Trying to remove invalid vertex [%s]!", vertex));
        }
        // remove vertex normally
        if (this.vertices.containsKey(vertex)) {
            int counter = this.vertices.get(vertex);
            if (counter == 1) { // remove vertex from list (one link remains)
                this.vertices.remove(vertex);
            } else { // decrements links counter
                this.vertices.put(vertex, counter - 1);
            }
        }
    }

    /***/
    public void addEdge(WeightedEdge edge) {
        log.debug(String.format("WeightedDigraph.addEdge() -> adding edge [%s].", edge));
        if (edge == null) {
            throw new IllegalArgumentException("Trying to add null edge!");
        }
        this.edges.add(edge);
        this.addVertex(edge.getStart());
        this.addVertex(edge.getEnd());
    }

    /***/
    public void removeEdge(WeightedEdge edge) {
        log.debug(String.format("WeightedDigraph.removeEdge() -> removing edge [%s].", edge));
        if (edge == null) {
            throw  new IllegalArgumentException("Trying to remove null edge!");
        }
        this.edges.remove(edge);
        this.removeVertex(edge.getStart());
        this.removeVertex(edge.getEnd());
    }

    /***/
    public boolean isEmpty() {
        return this.edges.isEmpty();
    }

    /**
     * Method generates weighted adjaceny matrix for current wighted digraph. For empty graph matrix is an empty
     * 2-D array. Before generating method checks consistency - all vertices should go successively, started with
     * number zero (0), otherwise runtime exception will be thrown.
    */
    public int[][] getAdjacenyWeightedMatrix() {
        log.debug("WeightedDigraph.getAdjacenyWeightedMatrix() working.");
        int[][] result = new int[this.vertices.size()][this.vertices.size()];
        // build matrix if current graph isn't empty
        if (!this.isEmpty()) {
            // check graph state - vertices numbers should go successively, otherwise - throw exception
            if (this.vertices.firstKey() != 0 || this.vertices.size() - 1 != this.vertices.lastKey()) {
                throw new IllegalStateException(String.format("Vertices don't start with zero or don't go successively -> [%s]!", this.vertices.keySet()));
            }
            // weithed digraph state is OK - creating weighted adjaceny matrix
            for (WeightedEdge edge : edges) {
                result[edge.getStart()][edge.getEnd()] = (edge.getWeight() > 0 ? edge.getWeight() : -1);
            }
        }
        return result;
    }

    /***/
    public int[][] getAdjacenyUnweightedMatrix() {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

}