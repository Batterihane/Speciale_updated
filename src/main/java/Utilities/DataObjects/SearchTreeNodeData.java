package Utilities.DataObjects;

import nlogn.GraphEdge;
import nlogn.AgreementMatching;
import nlogn.ProperCrossing;
import org.forester.phylogeny.data.Reference;

import java.util.List;

/**
 * Created by Thomas on 05-04-2016.
 */
public class SearchTreeNodeData extends Reference {
    private String name;
    private int index;
    private int lowIndex; // lowest index in subtree
    private int maxIndex; // largest index in subtree
    private GraphEdge g;
    private AgreementMatching maxAncestorM; // used when processing white edges
    private ProperCrossing maxAncestorX; // used when processing white edges
    private ProperCrossing maxAncestorY; // used when processing white edges
    private GraphEdge maxAncestorG; // used when processing white and red edges
    private ProperCrossing maxAncestorGR; // used when processing white edges
    private GraphEdge heaviesAddedGreenEdge; // used when processing green edges
    private ProperCrossing x;
    private AgreementMatching m;
    private ProperCrossing y;
    private GraphEdge r;

    public GraphEdge getG() {
        return g;
    }

    public void setG(GraphEdge g) {
        this.g = g;
//        String gString = g == null ? "" : g.toString();
//        System.out.println(name + ": g changed to " + gString);
    }

    public ProperCrossing getX() {
        return x;
    }

    public void setX(ProperCrossing x) {
        this.x = x;
//        String xString = x == null ? "" : x.toString();
//        System.out.println(name + ": x changed to " + xString);
    }

    public AgreementMatching getM() {
        return m;
    }

    public void setM(AgreementMatching m) {
        this.m = m;
//        String mString = m == null ? "" : m.toString();
//        System.out.println(name + ": m changed to " + mString);
    }

    public ProperCrossing getY() {
        return y;
    }

    public void setY(ProperCrossing y) {
        this.y = y;
//        String yString = y == null ? "" : y.toString();
//        System.out.println(name + ": y changed to " + yString);
    }

    public GraphEdge getR() {
        return r;
    }

    public void setR(GraphEdge r) {
        this.r = r;
//        String rString = r == null ? "" : r.toString();
//        System.out.println(name + ": r changed to " + rString);
    }

    public GraphEdge getMaxAncestorG() {
        return maxAncestorG;
    }

    public void setMaxAncestorG(GraphEdge maxAncestorG) {
        this.maxAncestorG = maxAncestorG;
    }

    public AgreementMatching getMaxAncestorM() {
        return maxAncestorM;
    }

    public void setMaxAncestorM(AgreementMatching maxAncestorM) {
        this.maxAncestorM = maxAncestorM;
    }

    public ProperCrossing getMaxAncestorX() {
        return maxAncestorX;
    }

    public void setMaxAncestorX(ProperCrossing maxAncestorX) {
        this.maxAncestorX = maxAncestorX;
    }

    public ProperCrossing getMaxAncestorY() {
        return maxAncestorY;
    }

    public void setMaxAncestorY(ProperCrossing maxAncestorY) {
        this.maxAncestorY = maxAncestorY;
    }

    public ProperCrossing getMaxAncestorGR() {
        return maxAncestorGR;
    }

    public void setMaxAncestorGR(ProperCrossing maxAncestorGR) {
        this.maxAncestorGR = maxAncestorGR;
    }

    public GraphEdge getHeaviesAddedGreenEdge() {
        return heaviesAddedGreenEdge;
    }

    public void setHeaviesAddedGreenEdge(GraphEdge heaviesAddedGreenEdge) {
        this.heaviesAddedGreenEdge = heaviesAddedGreenEdge;
    }

    public int getIndex() {
        return index;
    }

    public int getLowIndex() {
        return lowIndex;
    }

    public int getMaxIndex() {
        return maxIndex;
    }

    public SearchTreeNodeData(int index, int lowIndex, int maxIndex) {
        super("");
        this.index = index;
        this.lowIndex = lowIndex;
        this.maxIndex = maxIndex;

        if(index == lowIndex)
            name = index + " leaf";
        else name = index + "";
    }
}
