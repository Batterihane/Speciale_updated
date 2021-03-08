package Utilities.DataObjects;

import org.forester.phylogeny.data.Reference;

/**
 * Created by Nikolaj on 10-03-2016.
 */
public class NodeDataReference extends Reference {
    private LCANodeData lcaNodeData;
    private CompleteBinaryTreeLCANodeData completeBinaryTreeLCANodeData;
    private MASTNodeData mastNodeData;
    private GraphNodeData graphNodeData;

    public NodeDataReference() {
        super("");
    }


    public LCANodeData getLcaNodeData() {
        return lcaNodeData;
    }

    public void setLcaNodeData(LCANodeData lcaNodeData) {
        this.lcaNodeData = lcaNodeData;
    }

    public CompleteBinaryTreeLCANodeData getCompleteBinaryTreeLCANodeData() {
        return completeBinaryTreeLCANodeData;
    }

    public void setCompleteBinaryTreeLCANodeData(CompleteBinaryTreeLCANodeData completeBinaryTreeLCANodeData) {
        this.completeBinaryTreeLCANodeData = completeBinaryTreeLCANodeData;
    }

    public MASTNodeData getMastNodeData() {
        return mastNodeData;
    }

    public void setMastNodeData(MASTNodeData mastNodeData) {
        this.mastNodeData = mastNodeData;
    }

    public GraphNodeData getGraphNodeData() {
        return graphNodeData;
    }

    public void setGraphNodeData(GraphNodeData graphNodeData) {
        this.graphNodeData = graphNodeData;
    }
}
