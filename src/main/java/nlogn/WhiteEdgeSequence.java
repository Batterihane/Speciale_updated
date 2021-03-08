package nlogn;

public class WhiteEdgeSequence {
    private final GraphEdge firstEdge;
    private final WhiteEdgeSequence previousEdge;

    public WhiteEdgeSequence(GraphEdge firstEdge, WhiteEdgeSequence previousEdge){
        this.firstEdge = firstEdge;
        this.previousEdge = previousEdge;
    }

    public GraphEdge getFirstEdge() {
        return firstEdge;
    }

    public WhiteEdgeSequence getPreviousEdge() {
        return previousEdge;
    }
}
