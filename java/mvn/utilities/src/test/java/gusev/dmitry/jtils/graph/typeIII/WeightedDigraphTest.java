package gusev.dmitry.jtils.graph.typeIII;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.*;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Automated tests for WeightedDigraph class.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 14.11.2014)
*/

public class WeightedDigraphTest {

    private static final Log log = LogFactory.getLog(WeightedDigraphTest.class);

    //
    private Set<WeightedEdge>     emptyEdgesSet;
    private WeightedDigraph       emptyGraph;
    //
    private Set<WeightedEdge>     nonEmptyEdgesSetWithNulls;
    private WeightedDigraph       nonEmptyGraphWithNulls;
    // two edges sets - for removeEdge() method test
    private Set<WeightedEdge>     nonEmptyEdgesSet1;
    private Set<WeightedEdge>     nonEmptyEdgesSet2;
    private WeightedDigraph       nonEmptyGraph;
    // two vertices sets - for removeEdge() test
    private Set<Integer>          verticesSet1; // without vertex #0
    private Set<Integer>          verticesSet2; // with vertex #0
    // two vertices maps - for removeEdge() test
    private Map<Integer, Integer> verticesMap1;
    private Map<Integer, Integer> verticesMap2;
    // weighted edges for testing method removeEdge()
    private WeightedEdge          testEdge1;
    private WeightedEdge          testEdge2;

    @BeforeClass // before all tests
    public static void setUp() {
        log.info("setting up [BEFORE ALL]");
    }

    @AfterClass // after all tests
    public static void tearDown() {
        log.info("tearing down [AFTER ALL]");
    }

    @Before
    public void beforeEveryTest() {

        testEdge1 = new WeightedEdge(0, 1, 2);
        testEdge2 = new WeightedEdge(0, 2, 6);

        // empty edges set and graph
        emptyEdgesSet = Collections.emptySet();
        emptyGraph = new WeightedDigraph(emptyEdgesSet);

        // non empty edges set without nulls - first variant, without edge (0, 1, 2)
        nonEmptyEdgesSet1 = new HashSet<>();
        nonEmptyEdgesSet1.add(testEdge2);
        nonEmptyEdgesSet1.add(new WeightedEdge(1, 2, 3));
        nonEmptyEdgesSet1.add(new WeightedEdge(1, 3, 5));
        nonEmptyEdgesSet1.add(new WeightedEdge(2, 3, 8));
        nonEmptyEdgesSet1.add(new WeightedEdge(2, 4, 7));
        nonEmptyEdgesSet1.add(new WeightedEdge(3, 4, -1));
        nonEmptyEdgesSet1.add(new WeightedEdge(3, 5, 10));
        nonEmptyEdgesSet1.add(new WeightedEdge(4, 5, 6));
        // non empty edges set without nulls - second variant, with edge (0, 1, 2)
        nonEmptyEdgesSet2 = new HashSet<>();
        nonEmptyEdgesSet2.add(testEdge1);
        nonEmptyEdgesSet2.addAll(nonEmptyEdgesSet1);
        nonEmptyGraph = new WeightedDigraph(nonEmptyEdgesSet2);

        // edges set with nulls
        nonEmptyEdgesSetWithNulls = new HashSet<>();
        nonEmptyEdgesSetWithNulls.add(null);
        nonEmptyEdgesSetWithNulls.addAll(nonEmptyEdgesSet2);
        nonEmptyEdgesSetWithNulls.add(null);
        nonEmptyEdgesSetWithNulls.add(null);
        nonEmptyGraphWithNulls = new WeightedDigraph(nonEmptyEdgesSetWithNulls);

        // vertices sets (variants #1 and #2)
        verticesSet1 = new HashSet<>();
        verticesSet2 = new HashSet<>();
        // variant #1 initialization
        verticesSet1.add(1);
        verticesSet1.add(2);
        verticesSet1.add(3);
        verticesSet1.add(4);
        verticesSet1.add(5);
        // variant #2 initialization
        verticesSet2.add(0);
        verticesSet2.addAll(verticesSet1);

        // vertices map variant #1 initialization
        verticesMap1 = new TreeMap<>();
        verticesMap1.put(1, 2);
        verticesMap1.put(2, 3);
        verticesMap1.put(3, 4);
        verticesMap1.put(4, 3);
        verticesMap1.put(5, 2);
        // vertices map variant #2 initialization
        verticesMap2 = new TreeMap<>();
        verticesMap2.put(0, 2);
        verticesMap2.put(1, 3);
        verticesMap2.put(2, 4);
        verticesMap2.put(3, 4);
        verticesMap2.put(4, 3);
        verticesMap2.put(5, 2);
    }

    @After
    public void afterEveryTest() {
        //log.info("after every test");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testConstructorException() {
        new WeightedDigraph(null);
    }

    @Test
    public void testEmptyGraphConstructor() {
        assertTrue("[edges of empty graph]", emptyGraph.edges.isEmpty());
        assertTrue("[verticesSet of empty graph]", emptyGraph.vertices.isEmpty());
    }

    @Test
    public void testGraphConstructorWithNulls() {
        assertFalse("[#1 nonempty graph with nulls test]", nonEmptyGraphWithNulls.isEmpty());
        assertFalse("[#2 nonempty graph with nulls test]", nonEmptyGraphWithNulls.edges.isEmpty());
        assertFalse("[#3 nonempty graph with nulls test]", nonEmptyGraphWithNulls.vertices.isEmpty());
        assertEquals("[vertices set for non empty graph with nulls]", verticesSet2, nonEmptyGraphWithNulls.vertices.keySet());
        assertEquals("[vertices map for non empty graph with nulls]", verticesMap2, nonEmptyGraphWithNulls.vertices);
    }

    @Test
    public void testGraphConstructorWithoutNulls() {
        assertFalse("[#1 nonempty graph without nulls test]", nonEmptyGraph.isEmpty());
        assertFalse("[#2 nonempty graph without nulls test]", nonEmptyGraph.edges.isEmpty());
        assertFalse("[#3 nonempty graph without nulls test]", nonEmptyGraph.vertices.isEmpty());
        assertEquals("[vertices set for non empty graph without nulls]", verticesSet2, nonEmptyGraph.vertices.keySet());
        assertEquals("[vertices map for non empty graph without nulls]", verticesMap2, nonEmptyGraph.vertices);
    }

    @Test
    public void testConstructor() {
        assertEquals("#1 constructors test", nonEmptyGraph.edges, nonEmptyGraphWithNulls.edges);
        assertEquals("#2 constructors test", nonEmptyGraph.vertices, nonEmptyGraphWithNulls.vertices);
    }

    @Test
    public void testIsEmpty() {
        assertTrue("[#1 isEmpty graph test]", emptyGraph.isEmpty());
        assertFalse("[#2 isEmpty graph test]", nonEmptyGraphWithNulls.isEmpty());
        assertFalse("[#3 isEmpty graph test]", nonEmptyGraph.isEmpty());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testAddVertexException() {
        emptyGraph.addVertex(-1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testRemoveVertexException() {
        emptyGraph.removeVertex(-1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testAddEdgeException() {
        emptyGraph.addEdge(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testRemoveEdgeException() {
        emptyGraph.removeEdge(null);
    }

    @Test
    public void testAddEdge1() {
        WeightedEdge edge = new WeightedEdge(0, 5, 0);
        emptyGraph.addEdge(edge);
        // emptyness test
        assertFalse("#1 addEdge() test", emptyGraph.isEmpty());
        assertFalse("#2 addEdge() test", emptyGraph.edges.isEmpty());
        assertFalse("#3 addEdge() test", emptyGraph.vertices.isEmpty());
        // edges
        Set<WeightedEdge> set = new HashSet<>();
        set.add(edge);
        assertEquals("#4 addEdge() test", set, emptyGraph.edges);
        // vertices
        Map<Integer, Integer> map = new HashMap<>();
        map.put(0, 1);
        map.put(5, 1);
        assertEquals("#5 addEdge() test", map, emptyGraph.vertices);
    }

    @Test
    public void testAddEdge2() {
        // todo:  add one edge twice
    }

    @Test
    public void testRemoveEdge1() {
        nonEmptyGraph.removeEdge(testEdge1);
        assertEquals("#1.1 removeEdge() test", nonEmptyEdgesSet1, nonEmptyGraph.edges);
        assertEquals("#1.2 removeEdge() test", 1, (int) nonEmptyGraph.vertices.get(0));
        assertEquals("#1.3 removeEdge() test", verticesSet2, nonEmptyGraph.vertices.keySet());
    }

    @Test
    public void testRemoveEdge2() {
        nonEmptyGraph.removeEdge(testEdge1);
        nonEmptyGraph.removeEdge(testEdge2);
        assertEquals("#2.1 removeEdge() test", verticesMap1, nonEmptyGraph.vertices);
        assertEquals("#2.2 removeEdge() test", verticesSet1, nonEmptyGraph.vertices.keySet());
    }

    @Test
    public void testRemoveEdge3() {
        // todo: remove wrong edge (not contained in graph)
    }

    @Test
    public void testAddRemoveEdge() {
        emptyGraph.addEdge(testEdge1);
        emptyGraph.removeEdge(testEdge1);
        assertTrue("#1 add/remove edge complex test", emptyGraph.isEmpty());
        assertEquals("#2 add/remove edge complex test", emptyEdgesSet, emptyGraph.edges);
        assertTrue("#3 add/remove edge complex test", emptyGraph.vertices.isEmpty());
        assertTrue("#4 add/remove edge complex test", emptyGraph.edges.isEmpty());
    }

    @Test (expected = IllegalStateException.class)
    public void testGetAdjacenyMatrixException() {
        nonEmptyGraph.removeEdge(testEdge1);
        nonEmptyGraph.removeEdge(testEdge2);
        nonEmptyGraph.getAdjacenyWeightedMatrix();
    }

    @Test
    public void testGetAdjacenyMatrix() {
        // adjaceny matrix for test weighted digraph
        int[][] matrix = {
                //       0  1  2  3   4   5
                /* 0 */ {0, 2, 6, 0,  0,  0},
                /* 1 */ {0, 0, 3, 5,  0,  0},
                /* 2 */ {0, 0, 0, 8,  7,  0},
                /* 3 */ {0, 0, 0, 0, -1, 10},
                /* 4 */ {0, 0, 0, 0,  0,  6},
                /* 5 */ {0, 0, 0, 0,  0,  0}
        };
        assertTrue("#1 adjaceny matrix test", Arrays.deepEquals(matrix, nonEmptyGraph.getAdjacenyWeightedMatrix()));
        // remove one edge and test again - should be generated different matrix
        nonEmptyGraph.removeEdge(testEdge1);
        assertFalse("#2 adjaceny matrix test", Arrays.deepEquals(matrix, nonEmptyGraph.getAdjacenyWeightedMatrix()));
    }

}