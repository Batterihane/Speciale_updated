package nlogn;

import Utilities.DataObjects.GraphNodeData;
import Utilities.DataObjects.NodeDataReference;
import org.forester.phylogeny.PhylogenyNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 15-03-2016.
 */
public class Graph {
    private List<PhylogenyNode> leftSet; // top-down order
    private List<PhylogenyNode> rightSet; // top-down order
    private List<GraphEdge> edges;
    private GraphEdge lastAddedEdge;
    private int nsav;

    public Graph(List<PhylogenyNode> rightSet) {
        leftSet = new ArrayList<>();
        this.rightSet = rightSet;
        edges = new ArrayList<>();

        for (PhylogenyNode node : rightSet){
            ((NodeDataReference) node.getNodeData().getReference()).setGraphNodeData(new GraphNodeData());
        }
    }

    public List<PhylogenyNode> getLeftSet() {
        return leftSet;
    }

    public List<PhylogenyNode> getRightSet() {
        return rightSet;
    }

    public List<GraphEdge> getEdges() {
        return edges;
    }

    public int getNsav() {
        return nsav;
    }

    private void addNodeToLeftSet(PhylogenyNode node){
        leftSet.add(node);
        ((NodeDataReference) node.getNodeData().getReference()).setGraphNodeData(new GraphNodeData());
    }

    public void addEdge(GraphEdge edge){ // Should be added consecutively w.r.t. the centroid path of T1
        if(leftSet.size() == 0 || edge.getLeft() != leftSet.get(leftSet.size()-1)){
            addNodeToLeftSet(edge.getLeft());
        }
        edges.add(edge);

        GraphNodeData leftNodeGraphNodeData = getGraphNodeData(edge.getLeft());
        leftNodeGraphNodeData.incrementDegree();

        if(leftNodeGraphNodeData.getDegree() > 2){
            if(!getGraphNodeData(edge.getRight()).hasNonSingletonEdge()){
                nsav++;
            }
        }
        else if(leftNodeGraphNodeData.getDegree() == 2){
            GraphNodeData lastAddedEdgeRightNodeGraphNodeData = getGraphNodeData(lastAddedEdge.getRight());
            if(!lastAddedEdgeRightNodeGraphNodeData.hasNonSingletonEdge()){
                nsav++;
                lastAddedEdgeRightNodeGraphNodeData.setHasNonSingletonEdge(true);
            }
            GraphNodeData rightNodeGraphNodeData = getGraphNodeData(edge.getRight());
            if(!rightNodeGraphNodeData.hasNonSingletonEdge()){
                nsav++;
                rightNodeGraphNodeData.setHasNonSingletonEdge(true);
            }
        }


        lastAddedEdge = edge;
    }

    private GraphNodeData getGraphNodeData(PhylogenyNode node){
        return ((NodeDataReference) node.getNodeData().getReference()).getGraphNodeData();
    }
}
