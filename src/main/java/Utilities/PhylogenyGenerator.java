package Utilities;

import n_squared.MAST;
import org.forester.archaeopteryx.Archaeopteryx;
import org.forester.archaeopteryx.MainFrame;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

import java.util.*;

public class PhylogenyGenerator {

    public static void main(String[] args) {
        Phylogeny tree = generatePerfectTree(10, true);
        Archaeopteryx.createApplication(tree);
    }

    public static Phylogeny generateRandomTree(int size, boolean randomNames){
        Random random = new Random();

        List<PhylogenyNode> nodes = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            PhylogenyNode newNode = new PhylogenyNode();
            newNode.setName(i + "");
            nodes.add(newNode);
        }

        while(nodes.size() > 1){
            int i = random.nextInt(nodes.size());
            PhylogenyNode node1 = nodes.get(i);
            nodes.remove(i);
            int j = random.nextInt(nodes.size());
            PhylogenyNode node2 = nodes.get(j);
            nodes.remove(j);
            PhylogenyNode newNode = new PhylogenyNode();
            newNode.setChild1(node1);
            newNode.setChild2(node2);
            nodes.add(newNode);
            newNode.setName(newNode.getId() + "");
        }

        Phylogeny tree = new Phylogeny();
        tree.setRoot(nodes.get(0));

        if(!randomNames){
            renameTreeLeavesLeftToRight(tree);
        }
        return tree;
    }

    public static Pair<Phylogeny, Phylogeny> generateIdenticalRandomTrees(int size, boolean randomNames){
        Random random = new Random();

        List<PhylogenyNode> t1Nodes = new ArrayList<>();
        List<PhylogenyNode> t2Nodes = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            PhylogenyNode t1NewNode = new PhylogenyNode();
            PhylogenyNode t2NewNode = new PhylogenyNode();
            t1NewNode.setName(i + "");
            t2NewNode.setName(i + "");
            t1Nodes.add(t1NewNode);
            t2Nodes.add(t2NewNode);
        }

        while(t1Nodes.size() > 1){
            int i = random.nextInt(t1Nodes.size());
            PhylogenyNode t1Child1 = t1Nodes.get(i);
            PhylogenyNode t2Child1 = t2Nodes.get(i);
            t1Nodes.remove(i);
            t2Nodes.remove(i);
            int j = random.nextInt(t1Nodes.size());
            PhylogenyNode t1Child2 = t1Nodes.get(j);
            PhylogenyNode t2Child2 = t2Nodes.get(j);
            t1Nodes.remove(j);
            t2Nodes.remove(j);
            PhylogenyNode t1NewNode = new PhylogenyNode();
            PhylogenyNode t2NewNode = new PhylogenyNode();
            t1NewNode.setChild1(t1Child1);
            t1NewNode.setChild2(t1Child2);
            t2NewNode.setChild1(t2Child1);
            t2NewNode.setChild2(t2Child2);
            t1Nodes.add(t1NewNode);
            t2Nodes.add(t2NewNode);
            t1NewNode.setName(t1NewNode.getId() + "");
            t2NewNode.setName(t2NewNode.getId() + "");
        }

        Phylogeny tree1 = new Phylogeny();
        Phylogeny tree2 = new Phylogeny();
        tree1.setRoot(t1Nodes.get(0));
        tree2.setRoot(t2Nodes.get(0));

        if(!randomNames){
            renameTreeLeavesLeftToRight(tree1);
            renameTreeLeavesLeftToRight(tree2);
        }
        return new Pair<>(tree1, tree2);
    }

    public static Phylogeny generatePerfectTree(int size, boolean randomNames){
        Phylogeny tree = new Phylogeny();
        int currentTreeSize = 0;
        Queue<PhylogenyNode> currentLeaves = new LinkedList<>();

        PhylogenyNode root = new PhylogenyNode();
        tree.setRoot(root);
        currentLeaves.add(root);
        currentTreeSize++;

        while (currentTreeSize < size){
            PhylogenyNode currentNode = currentLeaves.poll();
            PhylogenyNode child1 = new PhylogenyNode();
            PhylogenyNode child2 = new PhylogenyNode();
            currentNode.setChild1(child1);
            currentNode.setChild2(child2);
            currentLeaves.add(child1);
            currentLeaves.add(child2);
            currentTreeSize++;
        }

        if(randomNames)
            renameTreeLeavesRandomly(tree);
        else
            renameTreeLeavesLeftToRight(tree);

        return tree;
    }

    private static void renameTreeLeavesLeftToRight(Phylogeny tree){
        PhylogenyNodeIterator iterator = tree.iteratorPreorder();
        int i = 0;
        while (iterator.hasNext()){
            PhylogenyNode currentNode = iterator.next();
            if(currentNode.isExternal()){
                currentNode.setName(i + "");
                i++;
            }
        }
    }

    public static void renameTreeLeavesRightToLeft(Phylogeny tree){
        List<PhylogenyNode> leaves = tree.getExternalNodes();
        int j = 0;
        for (int i = leaves.size()-1; i >= 0; i--) {
            PhylogenyNode currentLeaf = leaves.get(i);
            currentLeaf.setName(j + "");
            j++;
        }
    }

    public static void renameTreeLeavesRandomly(Phylogeny tree){
        Random random = new Random();
        List<PhylogenyNode> leaves = new ArrayList();
        PhylogenyNodeIterator iterator = tree.iteratorPostorder();

        while(iterator.hasNext()) {
            PhylogenyNode node = iterator.next();
            if(node.isExternal()) {
                leaves.add(node);
            }
        }
        int i = 0;
        while (!leaves.isEmpty()) {
            int leafIndex = random.nextInt(leaves.size());
            PhylogenyNode currentLeaf = leaves.get(leafIndex);
            currentLeaf.setName(i + "");
            leaves.remove(leafIndex);
            i++;
        }
    }

    public static Phylogeny generateBaseCaseTree(int size, boolean randomNames){
        Random random = new Random();

        List<PhylogenyNode> nodes = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            PhylogenyNode newNode = new PhylogenyNode();
            newNode.setName(i + "");
            nodes.add(newNode);
        }

        Phylogeny tree = new Phylogeny();
        PhylogenyNode root = new PhylogenyNode();
        tree.setRoot(root);
        PhylogenyNode currentNode = root;
        while (nodes.size() > 2){
            int i = random.nextInt(nodes.size());
            PhylogenyNode leaf = nodes.get(i);
            nodes.remove(i);
            currentNode.setChild1(leaf);
            PhylogenyNode newInternalNode = new PhylogenyNode();
            currentNode.setChild2(newInternalNode);
            currentNode = newInternalNode;
        }
        currentNode.setChild1(nodes.get(0));
        currentNode.setChild2(nodes.get(1));

        if(!randomNames){
            renameTreeLeavesLeftToRight(tree);
        }
        return tree;
    }

    public static Phylogeny generateTreeExampleA() {
        Phylogeny tree = new Phylogeny();
        PhylogenyNode root = new PhylogenyNode();

        PhylogenyNode[] leaves = new PhylogenyNode[9];

        for (int i = 1; i < leaves.length; i++) {
            PhylogenyNode leaf = new PhylogenyNode();
            leaf.setName(i+"");
            leaves[i] = leaf;
        }

        PhylogenyNode NodeB = new PhylogenyNode();
        PhylogenyNode NodeC = new PhylogenyNode();
        PhylogenyNode NodeD = new PhylogenyNode();
        PhylogenyNode NodeE = new PhylogenyNode();
        PhylogenyNode NodeF = new PhylogenyNode();
        PhylogenyNode NodeG = new PhylogenyNode();

        NodeC.setChild1(leaves[1]);
        NodeC.setChild2(leaves[2]);
        NodeD.setChild1(leaves[3]);
        NodeD.setChild2(leaves[4]);
        NodeF.setChild1(leaves[5]);
        NodeF.setChild2(leaves[6]);
        NodeG.setChild1(leaves[7]);
        NodeG.setChild2(leaves[8]);

        NodeB.setChild1(NodeC);
        NodeB.setChild2(NodeD);
        NodeE.setChild1(NodeF);
        NodeE.setChild2(NodeG);

        root.setChild1(NodeB);
        root.setChild2(NodeE);
        tree.setRoot(root);

        return tree;
    }

    public static Phylogeny generateTreeExampleH() {
        Phylogeny tree = new Phylogeny();
        PhylogenyNode root = new PhylogenyNode();

        PhylogenyNode[] leaves = new PhylogenyNode[9];

        for (int i = 1; i < leaves.length; i++) {
            PhylogenyNode leaf = new PhylogenyNode();
            leaf.setName(i+"");
            leaves[i] = leaf;
        }

        PhylogenyNode NodeI = new PhylogenyNode();
        PhylogenyNode NodeJ = new PhylogenyNode();
        PhylogenyNode NodeL = new PhylogenyNode();
        PhylogenyNode NodeM = new PhylogenyNode();
        PhylogenyNode NodeN = new PhylogenyNode();
        PhylogenyNode NodeF = new PhylogenyNode();

        NodeJ.setChild1(leaves[1]);
        NodeJ.setChild2(leaves[8]);
        NodeL.setChild1(leaves[5]);
        NodeL.setChild2(leaves[6]);
        NodeN.setChild1(leaves[2]);
        NodeN.setChild2(leaves[7]);
        NodeF.setChild1(leaves[3]);
        NodeF.setChild2(leaves[4]);

        NodeI.setChild1(NodeJ);
        NodeI.setChild2(NodeL);
        NodeM.setChild1(NodeN);
        NodeM.setChild2(NodeF);

        root.setChild1(NodeI);
        root.setChild2(NodeM);
        tree.setRoot(root);

        return tree;
    }
}
