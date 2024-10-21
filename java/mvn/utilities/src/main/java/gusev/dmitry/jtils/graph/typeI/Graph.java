package gusev.dmitry.jtils.graph.typeI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Undirected graph.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 12.11.2014)
*/

public class Graph extends Digraph {

    @Override
    public boolean isAdjacent(int u, int v) {
        return (this.adjacencyList[u].contains(v) && this.adjacencyList[v].contains(u));
    }

    @Override
    public void insertEdge(int v, int w) { // inserts undirected edge
        if (this.vertices[v] && this.vertices[w] && !this.adjacencyList[v].contains(w) && !this.adjacencyList[w].contains(v)) {
            this.adjacencyList[v].add(w);
            this.adjacencyList[w].add(v);
        }
    }

    /*
    @Override
    public void eraseEdge(int v, int w) {
        if (this.isAdjacent(v, w) && this.vertices[v] && this.vertices[w]) {
            // there are two methods: remove(Object e) and remove(int index) - we need first and use wrapper class!
            this.adjacencyList[v].remove(new Integer(w));
            this.adjacencyList[w].remove(new Integer(v));
        } else { // illegal edge removal -> throw exception
            throw new GraphException("Illegal edge removal!");
        }
    }
    */

    /** just for test */
    public static void main(String[] args) {
        Log log = LogFactory.getLog(Graph.class);
        log.info("Graph MAIN starting...");

        Graph graph = new Graph();
        // vertices
        graph.insertVertex(0);
        graph.insertVertex(1);
        graph.insertVertex(2);
        graph.insertVertex(3);
        graph.insertVertex(4);
        // edges
        graph.insertEdge(0, 1);
        graph.insertEdge(0, 2);
        graph.insertEdge(0, 4);
        graph.insertEdge(1, 2);
        graph.insertEdge(2, 4);

        System.out.println("->\n" + graph);

        graph.eraseVertex(2);
        //graph.eraseEdge(2, 0);
        //System.out.println("->\n" + graph);
        //graph.eraseEdge(2, 1);
        System.out.println("->\n" + graph);
    }

}