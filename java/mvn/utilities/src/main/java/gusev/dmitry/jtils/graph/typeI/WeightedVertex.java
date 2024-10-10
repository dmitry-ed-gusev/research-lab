package gusev.dmitry.jtils.graph.typeI;

/**
 * Weighted graph vertex.
 * @author Gusev Dmitry (gusevd)
 * @version 1.0 (DATE: 13.11.2014)
*/

public class WeightedVertex {

    private int dest;
    private int weight;

    public WeightedVertex(int dest, int weight) {
        this.dest = dest;
        this.weight = weight;
    }

    public int getVertex() {
        return dest;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WeightedVertex that = (WeightedVertex) o;

        if (dest != that.dest) return false;
        if (weight != that.weight) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = dest;
        result = 31 * result + weight;
        return result;
    }

    @Override
    public String toString() {
        return String.format("[WV -> d:%s, w:%s]", dest, weight);
    }

}