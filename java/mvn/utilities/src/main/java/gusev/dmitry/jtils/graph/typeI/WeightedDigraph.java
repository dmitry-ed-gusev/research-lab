package gusev.dmitry.jtils.graph.typeI;

import gusev.dmitry.jtils.graph.typeI.interfaces.WeightedDigraphInteface;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

/**
 * Weighted digraph (directed graph) implementation.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 13.11.2014)
*/

public class WeightedDigraph implements WeightedDigraphInteface {

    private static final int GRAPH_SIZE = 100; // max number of vertices allowed in any weighted digraph

    // size of the current digraph
    private int       currentSize;
    // total weight of all of the edges of the current weighted graph
    private int       totalWeight;
    // array holding the weighted vertices of the current weighted digraph
    private boolean                      weightedVertices[] = new boolean[GRAPH_SIZE];
    // components of this array indicate whether the corresponding vertex has already been
    // visited (if true) in some traversal of that digraph (vertex must be true in vertices array!)
    private boolean[]                    isVisited     = new boolean[GRAPH_SIZE];
    // adjacency list for current weighted digraph - description of weighted digraph
    private LinkedList<WeightedVertex>[] adjacencyList = new LinkedList[GRAPH_SIZE];
    // set holds vertices in the current weighted digraph
    private Set<Integer>                 vertexSet     = new TreeSet<>();

    /***/
    public WeightedDigraph() {
        this.currentSize = 0;
        this.totalWeight = 0;
        for (int index =0; index < GRAPH_SIZE; index++) {
            this.weightedVertices[index] = false;
            this.isVisited[index]        = false;
            this.adjacencyList[index]    = new LinkedList<>();
        }
    }

    @Override
    public boolean isEmpty() {
        return this.currentSize == 0;
    }

    @Override
    public int size() {
        return this.currentSize;
    }

    @Override
    public int totalWeight() {
        return this.totalWeight;
    }

    @Override
    public void insertVertex(WeightedVertex v) {
        if (!this.weightedVertices[v.getVertex()]) { // vertex is not in present digraph
            this.currentSize++;
            this.weightedVertices[v.getVertex()] = true;
            this.vertexSet.add(v.getVertex());
        } else if (this.currentSize < GRAPH_SIZE && this.weightedVertices[v.getVertex()]) { // vertex is in current digraph -> exception
            throw new WeightedDigraphException("Vertex is already in weighted digraph!");
        } else { // overflow condition
            throw new WeightedDigraphException("Overflow - weighted digraph is full!");
        }
    }

    @Override
    public void insertEdge(WeightedVertex v, WeightedVertex w) {
        if (this.weightedVertices[v.getVertex()] && this.weightedVertices[w.getVertex()]
                && !this.adjacencyList[v.getVertex()].contains(w)) {
            this.adjacencyList[v.getVertex()].add(w);
            this.totalWeight += w.getWeight();
        } else { // error condition
            throw new WeightedDigraphException("Illegal attempt to join weighted edge!");
        }
    }

    @Override
    public boolean isAdjacent(WeightedVertex v, WeightedVertex w) {
        return (this.adjacencyList[v.getVertex()].contains(w) || this.adjacencyList[w.getVertex()].contains(v));
    }

    @Override
    public void eraseEdge(WeightedVertex v, WeightedVertex w) {
        if (this.isAdjacent(v, w) && this.weightedVertices[v.getVertex()] && this.weightedVertices[w.getVertex()]) {
            this.adjacencyList[v.getVertex()].remove(w);
            this.totalWeight -= w.getWeight();
        } else { // error condition
            throw new WeightedDigraphException("Edge removal aborted!");
        }
    }

    @Override
    public void eraseVertex(WeightedVertex v) {
        if (this.weightedVertices[v.getVertex()]) { // if v is a vertex of current digraph
            for (int index = 0; index < GRAPH_SIZE; index++) { // remove edges
                if (this.weightedVertices[index] && this.adjacencyList[index].contains(v)) {
                    for (WeightedVertex w : this.adjacencyList[index]) {
                        if (this.isAdjacent(v, w)) {
                            this.adjacencyList[v.getVertex()].remove(w);
                            this.adjacencyList[w.getVertex()].remove(v);
                            this.totalWeight -= w.getWeight();
                        }
                    }
                }
            } // end of FOR loop
            this.weightedVertices[v.getVertex()] = false; // remove vertex from array
            this.currentSize--;       // reduce size of digraph
        } else { // v is not a vertex -> throw exception
            throw new WeightedDigraphException("Parameter not a vertex of current digraph!");
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(this.getClass().getSimpleName().toUpperCase());
        result.append("\nVertices: ").append(this.vertexSet).append("\n");
        // digraph edges
        result.append("Edges: \n");
        for (int index = 0; index < GRAPH_SIZE; index++) {
            if (this.weightedVertices[index]) {
                result.append(index).append(" -> ").append(this.adjacencyList[index]).append("\n");
            }
        }
        // total weight of graph
        result.append("Total weight is ").append(this.totalWeight).append("\n");
        // empty or not?
        result.append("Current ").append(this.getClass().getSimpleName().toLowerCase())
                .append(" is ").append(this.isEmpty() ? "EMPTY" : "NOT EMPTY").append("\n");
        // vertices count
        result.append("Vertices count: ").append(this.size());

        return result.toString();
    }

    /** just for test */
    public static void main(String[] args) {
        Log log = LogFactory.getLog(WeightedDigraph.class);
        log.info("Digraph MAIN starting...");

        WeightedDigraph wDigraph = new WeightedDigraph();
        WeightedVertex a0_0 = new WeightedVertex(0 ,0);
        WeightedVertex a0_2 = new WeightedVertex(0, 2);
        wDigraph.insertVertex(a0_0);
        wDigraph.insertVertex(a0_2);

        WeightedVertex a1 = new WeightedVertex(1, 2);
        wDigraph.insertVertex(a1);
        //wDigraph.insertEdge(a0, a1);
        WeightedVertex a2 = new WeightedVertex(2, 4);
        wDigraph.insertVertex(a2);
        wDigraph.insertEdge(a1, a2);

        //wDigraph.insertEdge(a2, a3);
        WeightedVertex a4 = new WeightedVertex(1, 4);
        WeightedVertex a5 = new WeightedVertex(4, 2);
        wDigraph.insertVertex(a5);
        //wDigraph.insertEdge(a5, a0);

        System.out.println("->\n" + wDigraph);

        //wDigraph.eraseVertex(a0);
        //System.out.println("->\n" + wDigraph);

    }

}