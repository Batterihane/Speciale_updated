package Utilities.DataObjects;

import org.forester.phylogeny.data.Reference;

/**
 * Created by Thomas on 05-04-2016.
 */
public class GraphNodeData extends Reference {
    private int degree = 0; // for nodes in L(x)
    private boolean hasNonSingletonEdge = false; // for nodes in R(x)
    private int index; // for nodes in R(x)

    public int getDegree() {
        return degree;
    }

    public void incrementDegree() {
        this.degree++;
    }

    public boolean hasNonSingletonEdge() {
        return hasNonSingletonEdge;
    }

    public void setHasNonSingletonEdge(boolean hasNonSingletonEdge) {
        this.hasNonSingletonEdge = hasNonSingletonEdge;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public GraphNodeData() {
        super("");
    }
}
