package gusev.dmitry.jtils.graph.typeIII;

import org.apache.commons.lang3.StringUtils;

/**
 * Weighted edge for weighted directed graph. Vertices for current edge should be greater or equals
 * to zero (otherwise runtime exceprion will be thrown.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 14.11.2014)
*/

public class WeightedEdge {

    private String name;
    private int    start;
    private int    end;
    private int    weight;

    /***/
    public WeightedEdge(String name, int start, int end, int weight) {
        if (StringUtils.isBlank(name) || start < 0 || end < 0) {
            throw new IllegalArgumentException(String.format("Invalid name [%s] or start [%s]/end [%s] vertex!", name, start, end));
        }
        this.name   = name;
        this.start  = start;
        this.end    = end;
        this.weight = weight;
    }

    /***/
    public WeightedEdge(int start, int end, int weight) {
        this(start + "->" + end, start, end, weight);
    }

    public String getName() {
        return name;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WeightedEdge edge = (WeightedEdge) o;

        if (end != edge.end) return false;
        if (start != edge.start) return false;
        if (weight != edge.weight) return false;
        if (!name.equals(edge.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + start;
        result = 31 * result + end;
        result = 31 * result + weight;
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s->%s (%s)", start, end, weight);
    }

}