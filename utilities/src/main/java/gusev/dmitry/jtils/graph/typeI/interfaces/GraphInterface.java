package gusev.dmitry.jtils.graph.typeI.interfaces;

/**
 * @author Gusev Dmitry (Dmitry)
 * @version 1.0 (DATE: 04.11.2014)
*/

public interface GraphInterface extends DigraphInterface {

    /**
     * Inserts edge connecting v and w.
     * Precondition: v, w are vertices in undirected graph.
    */
    public void insertEdge(Object v, Object w);

    /**
     * Removes edge from v to w, if currently present in graph.
     * Precondition: v, w are vertices in current undirected graph.
    */
    public void eraseEdge(Object v, Object w);

}