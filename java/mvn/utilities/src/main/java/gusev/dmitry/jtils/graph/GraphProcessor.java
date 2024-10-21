package gusev.dmitry.jtils.graph;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Module for processing graphs models.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 29.10.2014)
*/

public class GraphProcessor {

    @SuppressWarnings("ConstantNamingConvention")
    private static final Log log = LogFactory.getLog(GraphProcessor.class);

    /**
     * Method for depth-first search in a graph.
     * @param graphMatrix int[][] graph adjacent matrix (square!)
     * @param visited boolean[] list of visited nodes
     * @param n int size of graph adjacent matrix
     * @param startVertex int start vertex number
    */
    // todo: purpose of this method???
    public static void graphDFS(int[][] graphMatrix, boolean[] visited, int n, int startVertex) {
        visited[startVertex] = true; // mark current node as visited
        System.out.println("-> " + Arrays.toString(visited));
        // iterate over all nodes adjacent to startVertex (that are not visited)
        for (int i = 0; i < n; i++) {
            if (graphMatrix[startVertex][i] == 1 && !visited[i]) {
                graphDFS(graphMatrix, visited, n, i); // repeat algorithm for i-th node
            }
        }
    }

    /**
     * Method for searching all paths in a graph by depth-first search method. method is private because it
     * is a part of implementation - use method GraphProcessor.graphDFSAllPaths() for searching of all paths in
     * a given graph (this is a facade to current method). For more details read javadoc for
     * method GraphProcessor.graphDFSAllPaths().
     * @param graphMatrix int[][] graph adjacent matrix (square!)
     * @param visited boolean[] list of visited nodes
     * @param n int size of graph adjacent matrix (square!)
     * @param startVertex int start vertex number
     * @param finishVertex int finish vertex number
    */
    private static void _graphDFSAllPaths(int[][] graphMatrix, boolean[] visited, int[] weights, int n,
                                          int startVertex, int finishVertex, List<Pair<List<Integer>, Integer>> pathsList) {

        if (startVertex == finishVertex) { // we finished (reached finish vertex)
            List<Integer> path   = new ArrayList<>(); // found path
            Integer       weight = 0;
            // generating path
            for (int i = 0; i < visited.length; i++) {
                if (visited[i]) {
                    path.add(i);
                }
                weight += weights[i];
            } // end of FOR
            path.add(finishVertex);
            pathsList.add(new ImmutablePair<>(path, weight));
            return;
        }

        visited[startVertex] = true; // mark current node as visited
        // iterate over all nodes adjacent to startVertex (that are not visited)
        for (int i = 0; i < n; i++) {
            // if matrix[i,j] = 0 - no link, otherwise link is present (<> 0)
            if (graphMatrix[startVertex][i] != 0 && !visited[i]) {
                weights[i] = (graphMatrix[startVertex][i] > 0 ? graphMatrix[startVertex][i] : 0);
                // repeat algorithm for i-th node (recursive call)
                _graphDFSAllPaths(graphMatrix, visited, weights, n, i, finishVertex, pathsList);
            }
        }
        visited[startVertex] = false;
        weights[startVertex] = 0;
    }

    /**
     * Method searches for all paths in a given graph using depth-first search algorithm. Graph should be
     * represented by square adjaceny matrix (otherwise - runtime exception!).
     * Preconditions for this method:
     *  - graph should be represented by square adjaceny matrix, otherwise runtime exception will be thrown.
     *  - adjaceny matrix can be:
     *      * weighted: [0] - no way between two vertices, [-1] - dependency (unweighted way between vertices),
     *        [value greater than zero (0)] - weighted way between vertices
     *      * unweighted: [0] - no way between vertices, [value not equals to zero (0)] - way between vertices exists
     *    if this rules will be broken, method may return wrong results.
    */
    public static List<Pair<List<Integer>, Integer>> graphDFSAllPaths(int[][] graphMatrix, int offset) {
        List<Pair<List<Integer>, Integer>> result = new ArrayList<>();

        GraphProcessor._graphDFSAllPaths(graphMatrix, new boolean[graphMatrix.length],
                new int[graphMatrix.length], graphMatrix.length, 0, graphMatrix.length - 1, result);

        if (offset > 0) {
            for (Pair<List<Integer>, Integer> pair : result) {
                List<Integer> path = pair.getLeft();
                for (int i = 0; i < path.size(); i++) {
                    path.set(i, path.get(i) + offset);
                }
            }
        }
        return result;
    }

    /***/
    //public static List<Pair<List<Integer>, Integer>> graphDFSCriticalPath(int[][] graphMatrix, int offset) {}

}