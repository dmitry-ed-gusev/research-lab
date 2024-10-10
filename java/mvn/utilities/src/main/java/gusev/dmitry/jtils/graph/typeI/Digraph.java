package gusev.dmitry.jtils.graph.typeI;

import gusev.dmitry.jtils.graph.typeI.interfaces.DigraphInterface;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.LinkedList;

/**
 * Dirgraph (directed graph) implementation.
 * @author Gusev Dmitry (Dmitry)
 * @version 1.0 (DATE: 04.11.2014)
*/

public class Digraph implements DigraphInterface {

    private static final int GRAPH_SIZE = 100; // max number of vertices allowed in any digraph

    // size of the current digraph
    private int          currentSize;
    // if any component of the vertices array is true, that signifies that the
    // subscript of that component is an actual vertex of the current digraph
    boolean[]    vertices      = new boolean[GRAPH_SIZE];
    // components of this array indicate whether the corresponding vertex has already been
    // visited (if true) in some traversal of that digraph (vertex must be true in vertices array!)
    private boolean[]    isVisited     = new boolean[GRAPH_SIZE];
    // adjacency list for current digraph - description of digraph
    LinkedList[] adjacencyList = new LinkedList[GRAPH_SIZE];

    /***/
    public Digraph() {
        this.currentSize = 0;
        // init internal data structures
        for (int index = 0; index < GRAPH_SIZE; index++) {
            this.vertices[index]      = false;
            this.isVisited[index]     = false;
            this.adjacencyList[index] = new LinkedList<Integer>();
        }
    }

    @Override
    public boolean isEmpty() {
        return currentSize == 0;
    }

    @Override
    public int size() {
        return currentSize;
    }

    @Override
    public boolean isAdjacent(int u, int v) {
        return this.adjacencyList[u].contains(v) || this.adjacencyList[v].contains(u);
    }

    @Override
    public void insertEdge(int v, int w) {
        // v, w are vertices of the current digraph and w is not currently joined to v by an edge
        if (this.vertices[v] && this.vertices[w] && !this.adjacencyList[v].contains(w)) {
            this.adjacencyList[v].add(w);
        } else { // any other condition
            throw new GraphException("Illegal attempt to join edges!");
        }
    }

    @Override
    public void insertVertex(int index) {
        if (!this.vertices[index]) { // vertex is not in present digraph
            this.currentSize++;
            this.vertices[index] = true;
        } else if (this.currentSize < GRAPH_SIZE && vertices[index]) { // vertex is already in digraph -> throw execption
            throw new GraphException(String.format("Vertex [%s] is already in digraph!", index));
        } else { // overflow case
            throw new GraphException("Overflow - digraph is already full!");
        }
    }

    @Override
    public void eraseVertex(int v) {
        if (this.vertices[v]) { // if v is a vertex of current digraph
            for (int w = 0; w < GRAPH_SIZE; w++) { // remove edges
                if (this.vertices[w] && /*this.adjacencyList[v].contains(w)*/ this.isAdjacent(v, w)) {
                    this.eraseEdge(v, w);
                }
            } // end of FOR loop
            this.vertices[v] = false; // remove vertex from array
            this.currentSize--;       // reduce size of digraph
        } else { // v is not a vertex -> throw exception
            throw new GraphException("Parameter not a vertex of current digraph!");
        }
    }

    @Override
    public void eraseEdge(int v, int w) {
        // if there is an edge from v to w, and each of v, w is a vertex in the current digraph
        if (this.isAdjacent(v, w) && this.vertices[v] && this.vertices[w]) {
            // there are two methods: remove(Object e) and remove(int index) - we need first and use wrapper class!
            this.adjacencyList[v].remove(new Integer(w));
            this.adjacencyList[w].remove(new Integer(v));
        } else {
            throw new GraphException("Illegal edge removal!");
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(this.getClass().getSimpleName().toUpperCase()).append("\nVertices: ");
        // digraph vertices
        for (int index = 0; index < GRAPH_SIZE; index++) {
            if (this.vertices[index]) {
                result.append(index).append(" ");
            }
        }
        result.append("\n");
        // digraph edges
        result.append("Edges: \n");
        for (int index = 0; index < GRAPH_SIZE; index++) {
            if (this.vertices[index]) {
                result.append(index).append(" -> ").append(this.adjacencyList[index]).append("\n");
            }
        }
        // empty or not?
        result.append("Current ").append(this.getClass().getSimpleName().toLowerCase())
                .append(" is ").append(this.isEmpty() ? "EMPTY" : "NOT EMPTY").append("\n");
        // vertices count
        result.append("Vertices count: ").append(this.size());

        return result.toString();
    }

    /** just for test */
    public static void main(String[] args) {
        Log log = LogFactory.getLog(Digraph.class);
        log.info("Digraph MAIN starting...");

        Digraph graph = new Digraph();
        // vertices
        graph.insertVertex(0);
        graph.insertVertex(1);
        graph.insertVertex(2);
        graph.insertVertex(3);
        graph.insertVertex(4);
        // edges
        graph.insertEdge(0, 1);
        graph.insertEdge(0, 4);
        graph.insertEdge(1, 2);
        graph.insertEdge(2, 0);
        graph.insertEdge(2, 2);
        graph.insertEdge(4, 0);

        System.out.println("->\n" + graph);

        graph.eraseVertex(0);
        System.out.println("->\n" + graph);
    }

}