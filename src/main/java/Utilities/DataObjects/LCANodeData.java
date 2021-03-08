package Utilities.DataObjects;

import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.data.Reference;

/**
 * Created by Nikolaj on 10-03-2016.
 */
public class LCANodeData extends Reference {
    private int dfsNumber;
    private int leastSignificant1BitIndex; //h(v), counting from 1
    private PhylogenyNode maxHeightSubtreeNode; //I(v)
    private int bitNumber; //A(v)
    private PhylogenyNode completeBinaryTreeNode;

    public LCANodeData() {
        super("");
    }

    public int getBitNumber() {
        return bitNumber;
    }

    public void setBitNumber(int bitNumber) {
        this.bitNumber = bitNumber;
    }

    public PhylogenyNode getMaxHeightSubtreeNode() {
        return maxHeightSubtreeNode;
    }

    public void setMaxHeightSubtreeNode(PhylogenyNode maxHeightSubtreeNode) {
        this.maxHeightSubtreeNode = maxHeightSubtreeNode;
    }

    public int getDfsNumber() {
        return dfsNumber;
    }

    public void setDfsNumber(int dfsNumber) {
        this.dfsNumber = dfsNumber;
    }

    public int getLeastSignificant1BitIndex() {
        return leastSignificant1BitIndex;
    }

    public void setLeastSignificant1BitIndex(int leastSignificant1BitIndex) {
        this.leastSignificant1BitIndex = leastSignificant1BitIndex;
    }

    public PhylogenyNode getCompleteBinaryTreeNode() {
        return completeBinaryTreeNode;
    }

    public void setCompleteBinaryTreeNode(PhylogenyNode completeBinaryTreeNode) {
        this.completeBinaryTreeNode = completeBinaryTreeNode;
    }
}