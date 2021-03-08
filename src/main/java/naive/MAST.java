package naive;

import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;

import java.util.*;

/**
 * Created by Thomas on 01-03-2016.
 */
public class MAST {

    public static void main(String[] args) {
    }

    public Phylogeny getMAST(Phylogeny tree1, Phylogeny tree2){
        Phylogeny result = new Phylogeny();

        List<String> leaves = new ArrayList<>();
        List<PhylogenyNode> tree1Leaves = tree1.getExternalNodes();
        for (PhylogenyNode leaf : tree1Leaves) {
            leaves.add(leaf.getName());
        }

        int mastSize = 0;
        for (int i = 0; i < (int)Math.pow(2, leaves.size()) - 1; i++) {
            BitSet leavesToRemoveIndexes = BitSet.valueOf(new long[] { i });
            List<String> leavesToRemove = bitSetToLeaves(leavesToRemoveIndexes, leaves);
            Phylogeny subtree1 = getSubtree(tree1, leavesToRemove);
            Phylogeny subtree2 = getSubtree(tree2, leavesToRemove);
            if(treeCompare(subtree1, subtree2) && leaves.size() - leavesToRemove.size() > mastSize){
                mastSize = leaves.size() - leavesToRemove.size();
                result = subtree1;
            }
        }

        return result;
    }

    private boolean treeCompare(Phylogeny tree1, Phylogeny tree2){
        return treeCompareRecursive(tree1.getRoot(), tree2.getRoot());
    }

    private boolean treeCompareRecursive(PhylogenyNode node1, PhylogenyNode node2){
        if(node1.isExternal()) return node2.isExternal() && node1.getName().equals(node2.getName());
        else {
            if(node2.isExternal()) return false;

            PhylogenyNode node1Child1 = node1.getChildNode1();
            PhylogenyNode node1Child2 = node1.getChildNode2();
            PhylogenyNode node2Child1 = node2.getChildNode1();
            PhylogenyNode node2Child2 = node2.getChildNode2();
            return (treeCompareRecursive(node1Child1, node2Child1) && treeCompareRecursive(node1Child2, node2Child2)) ||
                    (treeCompareRecursive(node1Child1, node2Child2) && treeCompareRecursive(node1Child2, node2Child1));
        }
    }

    private List<String> bitSetToLeaves(BitSet indexes, List<String> leaves){
        List<String> result = new ArrayList<>();
        for (int i = 0; i < leaves.size(); i++) {
            if(indexes.get(i)) result.add(leaves.get(i));
        }
        return result;
    }

    private Phylogeny getSubtree(Phylogeny tree, List<String> leavesToRemove){
        Phylogeny result = copyTree(tree);
        for (PhylogenyNode leaf : result.getExternalNodes()){
            if(leavesToRemove.contains(leaf.getName())){
                PhylogenyNode parent = leaf.getParent();
                if(parent.isRoot()){
                    PhylogenyNode sibling = getSibling(leaf);
                    sibling.setParent(null);
                    result.setRoot(sibling);
                }
                else {
                    PhylogenyNode grandParent = parent.getParent();
                    PhylogenyNode sibling = getSibling(leaf);

                    if(parent.isFirstChildNode()) grandParent.setChild1(sibling);
                    else grandParent.setChild2(sibling);
                }
            }
        }
        return copyTree(result);
    }

    private PhylogenyNode getSibling(PhylogenyNode leaf) {
        PhylogenyNode parent = leaf.getParent();
        PhylogenyNode sibling;
        if(leaf.isFirstChildNode()) sibling = parent.getChildNode2();
        else sibling = parent.getChildNode1();
        return sibling;
    }

    private Phylogeny copyTree(Phylogeny tree){
        Phylogeny result = new Phylogeny();

        Stack<PhylogenyNodePair> remainingNodes = new Stack<>();
        PhylogenyNode root = tree.getRoot();
        PhylogenyNode newRoot = new PhylogenyNode();
        PhylogenyNodePair rootPair = new PhylogenyNodePair(root, newRoot);
        remainingNodes.push(rootPair);

        while(!remainingNodes.isEmpty()){
            PhylogenyNodePair nodePair = remainingNodes.pop();
            PhylogenyNode oldNode = nodePair.firstNode;
            PhylogenyNode newNode = nodePair.secondNode;
            if(oldNode.isExternal()){
                newNode.setName(oldNode.getName());
                continue;
            }
            PhylogenyNode newChild1 = new PhylogenyNode();
            PhylogenyNode newChild2 = new PhylogenyNode();
            newNode.setChild1(newChild1);
            newNode.setChild2(newChild2);
            remainingNodes.push(new PhylogenyNodePair(oldNode.getChildNode1(), newChild1));
            remainingNodes.push(new PhylogenyNodePair(oldNode.getChildNode2(), newChild2));
        }
        result.setRoot(newRoot);
        return result;
    }

    private class PhylogenyNodePair {
        private PhylogenyNode firstNode;
        private PhylogenyNode secondNode;

        public PhylogenyNodePair(PhylogenyNode firstNode, PhylogenyNode secondNode) {
            this.firstNode = firstNode;
            this.secondNode = secondNode;
        }

        public PhylogenyNode getFirstNode() {
            return firstNode;
        }

        public void setFirstNode(PhylogenyNode firstNode) {
            this.firstNode = firstNode;
        }

        public PhylogenyNode getSecondNode() {
            return secondNode;
        }

        public void setSecondNode(PhylogenyNode secondNode) {
            this.secondNode = secondNode;
        }
    }
}
