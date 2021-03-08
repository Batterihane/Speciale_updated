package Utilities;

import Utilities.DataObjects.MASTNodeData;
import Utilities.DataObjects.NodeDataReference;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Created by Thomas on 29-02-2016.
 */
public class SubtreeProcessor {
    private int[] depths;
    private int treeSize;
    private ConstantTimeLCA lca;
    private int maxDepth;
    Stack<Integer>[] buckets;

    public static void main(String[] args) {
        ForesterNewickParser foresterNewickParser = new ForesterNewickParser();
        Phylogeny tree = PhylogenyGenerator.generateRandomTree(10, true);
        ArrayList<PhylogenyNode> nodes = new ArrayList<>();
        PhylogenyNode firstExternalNode = tree.getFirstExternalNode();
        nodes.add(firstExternalNode);
        nodes.add(firstExternalNode.getNextExternalNode());
        foresterNewickParser.displayPhylogeny(tree);
        SubtreeProcessor subtreeProcessor = new SubtreeProcessor(tree);
        Phylogeny phylogeny = subtreeProcessor.induceSubtree(nodes);
        foresterNewickParser.displayPhylogeny(phylogeny);
        foresterNewickParser.displayPhylogeny(tree);
        System.out.println("done");
    }

    public SubtreeProcessor(Phylogeny tree){
        assignIdsToNodes(tree);
        computeDepths(tree);
        buckets = new Stack[maxDepth+1];
        lca = new ConstantTimeLCA(tree);
    }

    private void assignIdsToNodes(Phylogeny tree){
        PhylogenyNodeIterator iterator = tree.iteratorPreorder();
        for(int i = 0 ; iterator.hasNext() ; i++){
            PhylogenyNode currentNode = iterator.next();
            getMastNodeDataFromNode(currentNode).setId(i);
        }
    }

    private void computeDepths(Phylogeny tree) {
        treeSize = tree.getNodeCount();
        depths = new int[treeSize];
        PhylogenyNodeIterator iterator = tree.iteratorPreorder();
        while (iterator.hasNext()){
            PhylogenyNode currentNode = iterator.next();
            PhylogenyNode parent = currentNode.getParent();
            if(parent == null) continue;

            int depth = depths[getMastNodeDataFromNode(parent).getId()] + 1;
            depths[getMastNodeDataFromNode(currentNode).getId()] = depth;
            if(depth > maxDepth) maxDepth = depth;
        }
    }

    public Phylogeny induceSubtree(List<PhylogenyNode> nodes){
        PhylogenyNode[] subtreeNodes = computeSubtreeNodes(nodes);
        IntegerPair[] leftRightIndexes = computeInitialLeftRightIndexes(subtreeNodes.length);
        updateLeftRightIndexes(subtreeNodes, leftRightIndexes);

        Phylogeny result = computeSubtree(subtreeNodes, leftRightIndexes);

        return result;
    }

    private void emptyBuckets(List<Integer> filledBuckets){
        for (int bucketIndex : filledBuckets){
            buckets[bucketIndex] = null;
        }
    }

    private Phylogeny computeSubtree(PhylogenyNode[] subtreeNodes, IntegerPair[] leftRightIndexes) {
        Phylogeny result = new Phylogeny();
        PhylogenyNode[] newSubtreeNodes = new PhylogenyNode[subtreeNodes.length];
        for (int i = 0; i < subtreeNodes.length; i++) {
            PhylogenyNode currentNode = subtreeNodes[i];
            PhylogenyNode newNode = new PhylogenyNode();
            newNode.setName(currentNode.getName());

            // Add node data
            NodeDataReference nodeDataReference = new NodeDataReference();
            MASTNodeData newNodeMastNodeData = new MASTNodeData();
            nodeDataReference.setMastNodeData(newNodeMastNodeData);
            newNode.getNodeData().addReference(nodeDataReference);

            newNodeMastNodeData.setT2Node(currentNode);

            // add reference to si node
            getMastNodeDataFromNode(currentNode).setSiNode(newNode);

            newSubtreeNodes[i] = newNode;
        }
        for (int i = 0; i < subtreeNodes.length; i++) {
            int leftIndex = leftRightIndexes[i].getLeft();
            int rightIndex = leftRightIndexes[i].getRight();
            int leftDepth = -1, rightDepth = -1;
            if(leftIndex != -1){
                PhylogenyNode leftNode = subtreeNodes[leftIndex];
                leftDepth = depths[getMastNodeDataFromNode(leftNode).getId()];
            }
            if(rightIndex != -1){
                PhylogenyNode rightNode = subtreeNodes[rightIndex];
                rightDepth = depths[getMastNodeDataFromNode(rightNode).getId()];
            }

            PhylogenyNode currentNode = newSubtreeNodes[i];
            if(leftDepth > rightDepth){
                PhylogenyNode parent = newSubtreeNodes[leftIndex];
                parent.setChild2(currentNode);
            }
            else if(rightDepth != -1){
                PhylogenyNode parent = newSubtreeNodes[rightIndex];
                parent.setChild1(currentNode);
            }
            else {
                result.setRoot(currentNode);
            }
        }
        return result;
    }

    private MASTNodeData getMastNodeDataFromNode(PhylogenyNode currentNode) {
        return ((NodeDataReference)currentNode.getNodeData().getReference()).getMastNodeData();
    }

    private void updateLeftRightIndexesOld(List<Integer> filledBuckets, IntegerPair[] leftRightIndexes) {
        for (int i = filledBuckets.size()-1; i >= 0; i--) {
            Stack<Integer> currentBucket = buckets[filledBuckets.get(i)];
            while (!currentBucket.isEmpty()){
                int currentIndex = currentBucket.pop();
                IntegerPair currentIntegerPair = leftRightIndexes[currentIndex];
                int precedingIndex = currentIntegerPair.getLeft();
                int followingIndex = currentIntegerPair.getRight();
                if(precedingIndex != -1) leftRightIndexes[precedingIndex].setRight(currentIntegerPair.getRight());
                if(followingIndex != -1) leftRightIndexes[followingIndex].setLeft(currentIntegerPair.getLeft());
            }
        }
    }

    private void updateLeftRightIndexes(PhylogenyNode[] subtreeNodes, IntegerPair[] leftRightIndexes) {
        Stack<Integer> nextNodesToProcess = new Stack<>();
        nextNodesToProcess.push(0);
        while (!nextNodesToProcess.empty()){
            int currentIndex = nextNodesToProcess.peek();
            if(currentIndex == -1) return;
            IntegerPair currentIntegerPair = leftRightIndexes[currentIndex];
            int precedingIndex = currentIntegerPair.getLeft();
            int followingIndex = currentIntegerPair.getRight();
            int currentNodeDepth = depths[getMastNodeDataFromNode(subtreeNodes[currentIndex]).getId()];
            int followingNodeDepth = followingIndex == -1 ? -1 : depths[getMastNodeDataFromNode(subtreeNodes[followingIndex]).getId()];
            if(currentNodeDepth > followingNodeDepth){
                nextNodesToProcess.pop();
                if(precedingIndex != -1) leftRightIndexes[precedingIndex].setRight(currentIntegerPair.getRight());
                if(followingIndex != -1) leftRightIndexes[followingIndex].setLeft(currentIntegerPair.getLeft());

                if(nextNodesToProcess.empty())
                    nextNodesToProcess.push(followingIndex);
            }
            else
                nextNodesToProcess.push(followingIndex);
        }
    }

    private IntegerPair[] computeInitialLeftRightIndexes(int length) {
        IntegerPair[] linkedSubtreeNodes = new IntegerPair[length];
        linkedSubtreeNodes[0] = new IntegerPair(-1, 1);
        for (int i = 1; i < length-1; i++) {
            linkedSubtreeNodes[i] = new IntegerPair(i-1, i+1);
        }
        linkedSubtreeNodes[length-1] = new IntegerPair(length-2, -1);
        return linkedSubtreeNodes;
    }

    private PhylogenyNode[] computeSubtreeNodes(List<PhylogenyNode> nodes) {
        int subtreeSize = nodes.size() * 2 - 1;
        PhylogenyNode[] subtreeNodes = new PhylogenyNode[subtreeSize];

        for (int i = 0; i < nodes.size()-1; i++) {
            subtreeNodes[i*2] = nodes.get(i);
            subtreeNodes[i*2+1] = lca.getLCA(nodes.get(i), nodes.get(i+1));
        }
        subtreeNodes[(nodes.size()-1)*2] = nodes.get(nodes.size()-1);
        return subtreeNodes;
    }

    private List<Integer> fillNodeBuckets(PhylogenyNode[] subtreeNodes){
        List<Integer> filledBuckets = new ArrayList<>();
        for (int i = 0; i < subtreeNodes.length; i++) {
            PhylogenyNode node = subtreeNodes[i];
            int depth = depths[getMastNodeDataFromNode(node).getId()];
            Stack<Integer> bucket = buckets[depth];
            if(bucket == null){
                bucket = new Stack<>();
                buckets[depth] = bucket;
                filledBuckets.add(depth);
            }
            bucket.push(i);
        }
        Collections.sort(filledBuckets);
        return filledBuckets;
    }

    private class IntegerPair {
        private int left;
        private int right;

        public IntegerPair(int left, int right){
            this.left = left;
            this.right = right;
        }

        public int getLeft() {
            return left;
        }

        public void setLeft(int left) {
            this.left = left;
        }

        public int getRight() {
            return right;
        }

        public void setRight(int right) {
            this.right = right;
        }
    }

}
