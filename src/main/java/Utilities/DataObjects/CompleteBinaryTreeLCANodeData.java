package Utilities.DataObjects;

import org.forester.phylogeny.data.Reference;

import java.util.BitSet;

/**
 * Created by Thomas on 08-03-2016.
 */
public class CompleteBinaryTreeLCANodeData extends Reference {
    private int dfsNumber;
    private int pathNumber;
    private int subtreeNodeCount;
    private int height;

    public CompleteBinaryTreeLCANodeData() {
        super("");
    }

    public int getSubtreeNodeCount() {
        return subtreeNodeCount;
    }

    public void setSubtreeNodeCount(int subtreeNodeCount) {
        this.subtreeNodeCount = subtreeNodeCount;
    }

    public int getDfsNumber() {
        return dfsNumber;
    }

    public void setDfsNumber(int dfsNumber) {
        this.dfsNumber = dfsNumber;
    }

    public int getPathNumber() {
        return pathNumber;
    }

    public void setPathNumberAndHeight(int pathNumber)
    {
        this.pathNumber = pathNumber;
        height = BitSet.valueOf(new long[] { pathNumber }).nextSetBit(0) + 1; //TODO: avoid BitSet
    }

    public int getHeight() {
        return height;
    }
}
