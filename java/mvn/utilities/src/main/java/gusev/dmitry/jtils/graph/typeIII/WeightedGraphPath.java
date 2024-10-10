package gusev.dmitry.jtils.graph.typeIII;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Weighted path in a weughted digraph implementation. Path doesn't allow cycles.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 18.11.2014)
*/

public class WeightedGraphPath {

    @SuppressWarnings("ConstantNamingConvention")
    private static final Log log = LogFactory.getLog(WeightedGraphPath.class);

    private SortedSet<Integer> path   = new TreeSet<>();
    private int                weight = 0;

    /***/
    public WeightedGraphPath(SortedSet<Integer> path, int weight) {
        log.debug("WeightedGraphPath.constructor() working.");
        if (path == null) {
            throw new IllegalArgumentException("List of vertices in a path is NULL!");
        }
        path.removeAll(Collections.singleton(null)); // remove all nulls from vertices set (path)
        this.path.addAll(path);
        this.weight = weight;
    }

    public SortedSet<Integer> getPath() {
        return Collections.unmodifiableSortedSet(this.path);
    }

    public int getWeight() {
        return weight;
    }
}