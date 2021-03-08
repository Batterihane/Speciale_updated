package Utilities;

import Utilities.DataObjects.*;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

import java.util.BitSet;

/**
 * Created by Thomas on 04-03-2016.
 */
public class ConstantTimeLCA {

    private PhylogenyNode[] headsOfRuns;
    private CompleteBinaryTreeLCA completeBinaryTreeLCAFinder;

    public static void main(String[] args) {
        ForesterNewickParser foresterNewickParser = new ForesterNewickParser();
        Phylogeny tree = PhylogenyGenerator.generateRandomTree(30, true);
        foresterNewickParser.displayPhylogeny(tree);
        ConstantTimeLCA lcaFinder = new ConstantTimeLCA(tree);
        PhylogenyNode lca = lcaFinder.getLCA(tree.getNode("50"), tree.getNode("40"));
        System.out.println(lca.getName());
    }

    public ConstantTimeLCA(Phylogeny tree){
        preprocessTree(tree);
    }

    private void preprocessTree(Phylogeny tree) {
        addLCANodeDataAndDFSNumberingsAndLSBIndex(tree);
        addMaxHeightSubtreeNodesAndHeadsOfRuns(tree);
        completeBinaryTreeLCAFinder = createCompleteBinaryTreeLCAAndAddMappings(tree);
        addBitNumbers(tree);
    }

    public PhylogenyNode getLCA(PhylogenyNode node1, PhylogenyNode node2){
        // Find h(I(z)):
        LCANodeData node1Data = getLCANodeDataFromNode(node1);
        LCANodeData node2Data = getLCANodeDataFromNode(node2);

        PhylogenyNode node1CompleteBinaryTreeNode = node1Data.getCompleteBinaryTreeNode();
        PhylogenyNode node2CompleteBinaryTreeNode = node2Data.getCompleteBinaryTreeNode();
        PhylogenyNode completeBinaryTreeLCA = completeBinaryTreeLCAFinder.getLCA(node1CompleteBinaryTreeNode, node2CompleteBinaryTreeNode); // b
        int cbtLCAHeight = ((CompleteBinaryTreeLCANodeData) completeBinaryTreeLCA.getNodeData().getReference()).getHeight(); // h(b)
        int node1BitNumber = node1Data.getBitNumber();
        int node2BitNumber = node2Data.getBitNumber();
        int k = node1BitNumber & node2BitNumber;
        k = k >> cbtLCAHeight-1;
        k = k << cbtLCAHeight-1;
        int lcaMaxHeightSubtreeNodeHeight = BitSet.valueOf(new long[] { k }).nextSetBit(0) + 1; // h(I(z))
//        System.out.println(lcaMaxHeightSubtreeNodeHeight);

        // Find z:
        PhylogenyNode node1ToLCARunEnteringNode = findNodeToLCARunEnteringNode(node1, lcaMaxHeightSubtreeNodeHeight);
        PhylogenyNode node2ToLCARunEnteringNode = findNodeToLCARunEnteringNode(node2, lcaMaxHeightSubtreeNodeHeight);

        int n1tlcarenDfsNumber = getLCANodeDataFromNode(node1ToLCARunEnteringNode).getDfsNumber();
        int n2tlcarenDfsNumber = getLCANodeDataFromNode(node2ToLCARunEnteringNode).getDfsNumber();

        PhylogenyNode z = n1tlcarenDfsNumber < n2tlcarenDfsNumber ? node1ToLCARunEnteringNode : node2ToLCARunEnteringNode;

        return z;
    }

    private PhylogenyNode findNodeToLCARunEnteringNode(PhylogenyNode node, int lcaMaxHeightSubtreeNodeHeight){ // x, j=h(I(z))
        PhylogenyNode nodeToLCARunEnteringNode;
        LCANodeData nodeData = getLCANodeDataFromNode(node);
        PhylogenyNode nodeMaxHeightSubtreeNode = nodeData.getMaxHeightSubtreeNode(); // I(x)
        int nmhsnHeight = getLCANodeDataFromNode(nodeMaxHeightSubtreeNode).getLeastSignificant1BitIndex(); // h(I(x))
        if(nmhsnHeight == lcaMaxHeightSubtreeNodeHeight) { // if h(I(x)) == h(I(z))
            nodeToLCARunEnteringNode = node;
        }
        else {
            int nodeBitNumber = nodeData.getBitNumber();
            int h_i_w = (int)Math.floor(Math.log(((int) Math.pow(2, lcaMaxHeightSubtreeNodeHeight - 1) - 1) & nodeBitNumber)/Math.log(2)) + 1; // h(I(w))
            int i_x_dfsNumber = getLCANodeDataFromNode(nodeMaxHeightSubtreeNode).getDfsNumber();
            int i_w_dfsNumber = i_x_dfsNumber >> h_i_w - 1;
            i_w_dfsNumber |= 1;
            i_w_dfsNumber = i_w_dfsNumber << h_i_w - 1;
            PhylogenyNode w = headsOfRuns[i_w_dfsNumber];
            nodeToLCARunEnteringNode = w.getParent();
        }
        return nodeToLCARunEnteringNode;
    }

    private void addLCANodeDataAndDFSNumberingsAndLSBIndex(Phylogeny tree) {
        PhylogenyNodeIterator iterator = tree.iteratorPreorder();
        for (int i = 1 ; iterator.hasNext() ; i++){
            PhylogenyNode currentNode = iterator.next();
            LCANodeData nodeData = new LCANodeData();
            nodeData.setDfsNumber(i);
            int leastSignificant1BitIndex = BitSet.valueOf(new long[] { i }).nextSetBit(0); //TODO: avoid BitSet
            nodeData.setLeastSignificant1BitIndex(leastSignificant1BitIndex+1);
            ((NodeDataReference)currentNode.getNodeData().getReference()).setLcaNodeData(nodeData);
        }
    }

    private void addMaxHeightSubtreeNodesAndHeadsOfRuns(Phylogeny tree) {
        headsOfRuns = new PhylogenyNode[tree.getNodeCount()+1];
        PhylogenyNodeIterator iterator = tree.iteratorPostorder();
        while (iterator.hasNext()){
            PhylogenyNode currentNode = iterator.next();
            LCANodeData currentNodeData = getLCANodeDataFromNode(currentNode);
            PhylogenyNode maxHeightSubtreeNode;

            if(currentNode.isExternal()) {
                maxHeightSubtreeNode = currentNode;
            }
            else{
                int lsbIndex = currentNodeData.getLeastSignificant1BitIndex();
                PhylogenyNode child1 = currentNode.getChildNode1();
                PhylogenyNode child2 = currentNode.getChildNode2();
                LCANodeData child1NodeData = getLCANodeDataFromNode(child1);
                LCANodeData child2NodeData = getLCANodeDataFromNode(child2);
//                int child1LSBIndex = child1NodeData.getLeastSignificant1BitIndex(); // According to Gusfield??
//                int child2LSBIndex = child2NodeData.getLeastSignificant1BitIndex();
                PhylogenyNode child1MaxHeightSubtreeNode = child1NodeData.getMaxHeightSubtreeNode();
                PhylogenyNode child2MaxHeightSubtreeNode = child2NodeData.getMaxHeightSubtreeNode();
                int child1LSBIndex = getLCANodeDataFromNode(child1MaxHeightSubtreeNode).getLeastSignificant1BitIndex();
                int child2LSBIndex = getLCANodeDataFromNode(child2MaxHeightSubtreeNode).getLeastSignificant1BitIndex();

                if(lsbIndex > child1LSBIndex && lsbIndex > child2LSBIndex){
                    maxHeightSubtreeNode = currentNode;
                }
                else if(child1LSBIndex > child2LSBIndex) maxHeightSubtreeNode = child1MaxHeightSubtreeNode;
                else maxHeightSubtreeNode = child2MaxHeightSubtreeNode;

                // set heads of runs
                if(child1MaxHeightSubtreeNode != maxHeightSubtreeNode){
                    int child1MaxHeightSubtreeNodeNumber = getLCANodeDataFromNode(child1MaxHeightSubtreeNode).getDfsNumber();
                    headsOfRuns[child1MaxHeightSubtreeNodeNumber] = child1;
                }
                if(child2MaxHeightSubtreeNode != maxHeightSubtreeNode){
                    int child2MaxHeightSubtreeNodeNumber = getLCANodeDataFromNode(child2MaxHeightSubtreeNode).getDfsNumber();
                    headsOfRuns[child2MaxHeightSubtreeNodeNumber] = child2;
                }
                if(currentNode.isRoot()){
                    int maxHeightSubtreeNodeNumber = getLCANodeDataFromNode(maxHeightSubtreeNode).getDfsNumber();
                    headsOfRuns[maxHeightSubtreeNodeNumber] = currentNode;
                }
            }

            currentNodeData.setMaxHeightSubtreeNode(maxHeightSubtreeNode);
        }
    }

    private CompleteBinaryTreeLCA createCompleteBinaryTreeLCAAndAddMappings(Phylogeny tree){
        int nodeCount = tree.getNodeCount();
        CompleteBinaryTreeLCA completeBinaryTreeLCA = new CompleteBinaryTreeLCA((int) (Math.ceil(Math.log(nodeCount) / Math.log(2))) - 1);

        PhylogenyNodeIterator iterator = tree.iteratorPreorder();
        while (iterator.hasNext()){
            PhylogenyNode currentNode = iterator.next();
            LCANodeData currentNodeData = getLCANodeDataFromNode(currentNode);
            PhylogenyNode maxHeightSubtreeNode = currentNodeData.getMaxHeightSubtreeNode();
            int maxHeightSubtreeNodeNumber = getLCANodeDataFromNode(maxHeightSubtreeNode).getDfsNumber();
            PhylogenyNode completeBinaryTreeNode = completeBinaryTreeLCA.getNodeFromPathNumber(maxHeightSubtreeNodeNumber);
            currentNodeData.setCompleteBinaryTreeNode(completeBinaryTreeNode);
        }
        return completeBinaryTreeLCA;
    }

    private void addBitNumbers(Phylogeny tree){
        PhylogenyNodeIterator iterator = tree.iteratorPreorder();
        while (iterator.hasNext()){
            PhylogenyNode currentNode = iterator.next();
            int parentBitNumber;
            if(currentNode.isRoot()) parentBitNumber = 0;
            else {
                PhylogenyNode parent = currentNode.getParent();
                parentBitNumber = getLCANodeDataFromNode(parent).getBitNumber();
            }
            LCANodeData currentNodeData = getLCANodeDataFromNode(currentNode);
            PhylogenyNode maxHeightSubtreeNode = currentNodeData.getMaxHeightSubtreeNode();
            int heightOfMaxHeightSubtreeNode = getLCANodeDataFromNode(maxHeightSubtreeNode).getLeastSignificant1BitIndex();
            int bitNumber = parentBitNumber | (int)Math.pow(2, heightOfMaxHeightSubtreeNode-1); // set bit heightOfMaxHeightSubtreeNode to 1
            currentNodeData.setBitNumber(bitNumber);
        }
    }

    private LCANodeData getLCANodeDataFromNode(PhylogenyNode node){
        return ((NodeDataReference) node.getNodeData().getReference()).getLcaNodeData();
    }

}
