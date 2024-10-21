package gusev.dmitry.jtils.graph;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 28.10.2014)
*/

public class _TMP {

    private int     number;
    private int     weight;
    private String  color    = "white";
    private _TMP previous = null;
    private int     distance = 0;

    private int     earlyStartT  = 0;
    private int     earlyFinishT = 0;

    public _TMP(int number, int weight) {
        this.number = number;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("number", number)
                .append("weight", weight)
                .append("color", color)
                .append("previous", previous)
                .append("distance", distance)
                .toString();
    }

    /***/
    public static void main(String[] args) {
        Log log = LogFactory.getLog(_TMP.class);
        log.info("Starting...");

        /*
        Map<Integer, List<Node>> graph1 = new HashMap<>();
        Node startNode = new Node(1);
        graph1.put(1, Arrays.asList(new Node(2), new Node(4)));
        graph1.put(2, Arrays.asList(new Node(5)));
        graph1.put(3, Arrays.asList(new Node(6), new Node(5)));
        graph1.put(4, Arrays.asList(new Node(2)));
        graph1.put(5, Arrays.asList(new Node(4)));
        graph1.put(6, Arrays.asList(new Node(6)));
        */

        //
        Map<Integer, List<_TMP>> graphAdjList = new HashMap<>();
        List<_TMP> emptyAdjList = Collections.emptyList();
        _TMP firstTMP = new _TMP(1, 0);
        graphAdjList.put(0, Arrays.asList(new _TMP(1, 2), new _TMP(2, 6)));
        graphAdjList.put(1, Arrays.asList(new _TMP(3, 5), new _TMP(2, 3)));
        graphAdjList.put(2, Arrays.asList(new _TMP(3, 8), new _TMP(4, 7)));
        graphAdjList.put(3, Arrays.asList(new _TMP(5, 10), new _TMP(4, 0)));
        graphAdjList.put(4, Arrays.asList(new _TMP(5, 6)));
        graphAdjList.put(5, emptyAdjList);

        //
        int[][] graphAdjMatrix =
                {
                        //        0  1  2  3  4  5  <-
                        /* 0 */ { 0, 1, 1, 0, 0, 0 },
                        /* 1 */ { 0, 0, 1, 1, 0, 0 },
                        /* 2 */ { 0, 0, 0, 1, 1, 0 },
                        /* 3 */ { 0, 0, 0, 0, 1, 1 },
                        /* 4 */ { 0, 0, 0, 0, 0, 1 },
                        /* 5 */ { 0, 0, 0, 0, 0, 0 }
                };

        int[][] graphAdjMatrixWeighted =
                {
                        //        0  1  2  3   4    5  <-
                        /* 0 */ { 0, 2, 6, 0,  0,   0 },
                        /* 1 */ { 0, 0, 3, 5,  0,   0 },
                        /* 2 */ { 0, 0, 0, 8,  7,   0 },
                        /* 3 */ { 0, 0, 0, 0, -1,  10 },
                        /* 4 */ { 0, 0, 0, 0,  0,   6 },
                        /* 5 */ { 0, 0, 0, 0,  0,   0 }
                };

        int[][] graph2 =
                {
                        {0, 2, 0, 0, 0, 0},
                        {0, 0, 3, 1, 4, 0},
                        {0, 0, 0, 0, 6, 0},
                        {0, 0, 0, 0, 7, 0},
                        {0, 0, 0, 0, 0, 5},
                        {0, 0, 0, 0, 0, 0}
                };

        int[][] graph3 =
                {
                        {0, 7,  2,  0, 0, 0},
                        {0, 0, -1, -1, 0, 0},
                        {0, 0,  0,  6, 1, 0},
                        {0, 0,  0,  0, 4, 1},
                        {0, 0,  0,  0, 0, 3},
                        {0, 0,  0,  0, 0, 0}
                };

        int[][] graph4 =
                {       /*        1  2  3  4   5  6   7   8  9 10  11*/
                        /*  1 */ {0, 2, 0, 0,  0, 0,  0,  0, 0, 0,  0},
                        /*  2 */ {0, 0, 5, 6,  3, 0,  0,  0, 0, 0,  0},
                        /*  3 */ {0, 0, 0, 0, -1, 7,  0,  0, 0, 0,  0},
                        /*  4 */ {0, 0, 0, 0,  0, 0,  0,  8, 0, 0,  0},
                        /*  5 */ {0, 0, 0, 0,  0, 0,  5,  0, 0, 0,  0},
                        /*  6 */ {0, 0, 0, 0,  0, 0,  8,  0, 0, 0,  8},
                        /*  7 */ {0, 0, 0, 0,  0, 0,  0, -1, 0, 0,  7},
                        /*  8 */ {0, 0, 0, 0,  0, 0,  0,  0, 4, 0,  0},
                        /*  9 */ {0, 0, 0, 0,  0, 0,  0,  0, 0, 4, 18},
                        /* 10 */ {0, 0, 0, 0,  0, 0,  0,  0, 0, 0,  5},
                        /* 11 */ {0, 0, 0, 0,  0, 0,  0,  0, 0, 0,  0}
                };

        // Depth-first search, DFS (поиск в глубину) - used for search all paths
        //GraphProcessor.graphDepthFirstSearch(graphAdjMatrix, new boolean[graphAdjMatrix.length], 6, 2);

        //System.out.println("-> " + GraphProcessor.graphDFSAllPaths(graphAdjMatrixWeighted));
        System.out.println("-> " + GraphProcessor.graphDFSAllPaths(graph4, 1));


        // Breadth-first search, BFS (поиск в ширину) - used for searck the shortest path
        /*
        Queue<Node> queue = new LinkedList<>();
        queue.add(firstNode);
        Scanner scanner = new Scanner(System.in);
        while (!queue.isEmpty()) {

            //System.out.println("-> " + queue);
            //scanner.nextLine();

            Node tmpNode = queue.poll(); // get node from queue head

            for (Node node : graphRazu.get(tmpNode.number)) {
                if ("white".equals(node.color)) {
                    node.color    = "gray";
                    node.distance = tmpNode.distance + 1;
                    node.previous = tmpNode;
                    queue.add(node);
                }
            }

            tmpNode.color = "black";
        }
        */

    }

}