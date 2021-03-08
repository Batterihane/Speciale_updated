package nlogn;

import org.forester.phylogeny.PhylogenyNode;

/**
 * Created by Thomas on 15-03-2016.
 */
public class GraphEdge {
    private PhylogenyNode left;
    private PhylogenyNode right;
    private int whiteWeight;
    private int greenWeight;
    private int redWeight;
    private PhylogenyNode mapNode;

    public GraphEdge(PhylogenyNode left, PhylogenyNode right){
        this.left = left;
        this.right = right;
    }

    public PhylogenyNode getLeft() {
        return left;
    }

    public void setLeft(PhylogenyNode left) {
        this.left = left;
    }

    public PhylogenyNode getRight() {
        return right;
    }

    public void setRight(PhylogenyNode right) {
        this.right = right;
    }

    public int getWhiteWeight() {
        return whiteWeight;
    }

    public void setWhiteWeight(int whiteWeight) {
        this.whiteWeight = whiteWeight;
    }

    public int getGreenWeight() {
        return greenWeight;
    }

    public void setGreenWeight(int greenWeight) {
        this.greenWeight = greenWeight;
    }

    public int getRedWeight() {
        return redWeight;
    }

    public void setRedWeight(int redWeight) {
        this.redWeight = redWeight;
    }

    public PhylogenyNode getMapNode() {
        return mapNode;
    }

    public void setMapNode(PhylogenyNode mapNode) {
        this.mapNode = mapNode;
    }

    @Override
    public String toString() {
        return "(" + left.getId() + ", " + right.getName() + ")";
    }
}
