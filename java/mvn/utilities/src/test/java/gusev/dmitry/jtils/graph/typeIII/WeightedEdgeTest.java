package gusev.dmitry.jtils.graph.typeIII;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Automated tests for weighted edge class.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 14.11.2014)
*/

public class WeightedEdgeTest {

    @Test (expected = IllegalArgumentException.class)
    public void testConstructorException1() {
        new WeightedEdge(-1, 0, 0);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testConstructorException2() {
        new WeightedEdge(0, -1, 0);
    }

    @Test
    @SuppressWarnings({"ObjectEquality", "ConstantConditions"})
    public void testEqualsAndHashCode() {
        WeightedEdge edge1 = new WeightedEdge(0, 0, 0);
        WeightedEdge edge2 = new WeightedEdge(1, 1, 1);
        WeightedEdge edge3 = new WeightedEdge(0, 0, 0);
        @SuppressWarnings("UnnecessaryLocalVariable")
        WeightedEdge edge4 = edge1;

        assertTrue("#1 equals() and hashCode()", edge1 == edge4);
        assertTrue("#2 equals() and hashCode()", edge1.equals(edge4));
        assertTrue("#3 equals() and hashCode()", edge1.hashCode() == edge4.hashCode());

        assertFalse("#4 equals() and hashCode()", edge1 == edge2);
        assertFalse("#5 equals() and hashCode()", edge1.equals(edge2));
        assertFalse("#6 equals() and hashCode()", edge1.hashCode() == edge2.hashCode());

        assertFalse("#7 equals() and hashCode()", edge1 == edge3);
        assertTrue("#8 equals() and hashCode()", edge1.equals(edge3));
        assertTrue("#9 equals() and hashCode()", edge1.hashCode() == edge3.hashCode());
    }

}