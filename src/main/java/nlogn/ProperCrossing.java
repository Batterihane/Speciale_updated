package nlogn;

public class ProperCrossing {
    private GraphEdge greenEdge;
    private GraphEdge redEdge;
    int weight;

    public ProperCrossing(GraphEdge greenEdge, GraphEdge redEdge) {
        this.greenEdge = greenEdge;
        this.redEdge = redEdge;

        int greenWeight = greenEdge == null ? 0 : greenEdge.getGreenWeight();
        int redWeight = redEdge == null ? 0 : redEdge.getRedWeight();
        weight = greenWeight + redWeight;
    }

    public GraphEdge getGreenEdge() {
        return greenEdge;
    }

    public GraphEdge getRedEdge() {
        return redEdge;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        String greenEdgeString = greenEdge == null ? "" : greenEdge.toString();
        String redEdgeString = redEdge == null ? "" : redEdge.toString();
        return greenEdgeString + ", " + redEdgeString;
    }
}
