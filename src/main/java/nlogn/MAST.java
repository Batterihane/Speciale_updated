package nlogn;

import Utilities.*;
import Utilities.DataObjects.GraphNodeData;
import Utilities.DataObjects.MASTNodeData;
import Utilities.DataObjects.NodeDataReference;
import Utilities.DataObjects.SearchTreeNodeData;
import org.forester.archaeopteryx.Archaeopteryx;
import org.forester.archaeopteryx.MainFrame;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MAST {

    public static void main(String[] args) {
        ForesterNewickParser parser = new ForesterNewickParser();
        Phylogeny tree1 = parser.parseNewickFileSingleTree("trees\\random\\aaaa.new");
        Phylogeny tree2 = parser.parseNewickFileSingleTree("trees\\random\\bbbb.new");

//        Phylogeny tree1 = PhylogenyGenerator.generateBaseCaseTree(10, false);
//        Phylogeny tree2 = PhylogenyGenerator.generateBaseCaseTree(10, false);
        MAST mast = new MAST();
        Archaeopteryx.createApplication(tree1);
        Archaeopteryx.createApplication(tree2);
        Archaeopteryx.createApplication(mast.getMAST(tree1, tree2, false).getTree());
    }

    public TreeAndSizePair getMAST(Phylogeny tree1, Phylogeny tree2, boolean recursive){
        AgreementMatching lwam;
        if(recursive)
            lwam = getLWAMRecursive(tree1, tree2);
        else
            lwam = getLWAMIterative(tree1, tree2);
        AgreementMatching[][] lwams = getMASTNodeDataFromNode(tree2.getRoot()).getSubtreeLWAM().getRight();
        TreeAndSizePair mast = createMASTFromMatching(lwam, lwams);
        updateParentReferences(mast.getTree());
        return mast;
    }

    private AgreementMatching getLWAMRecursive(Phylogeny tree1, Phylogeny tree2){
        addNodeDataReferences(tree1);
        addNodeDataReferences(tree2);

        tree1.recalculateNumberOfExternalDescendants(true);
        tree2.recalculateNumberOfExternalDescendants(true);
        int numberOfLeaves = tree1.getRoot().getNumberOfExternalNodes();

        // simple base case
        if(numberOfLeaves == 1){
            MASTNodeData tree2RootData = getMASTNodeDataFromNode(tree2.getRoot());
            AgreementMatching lwam = new AgreementMatching(new ProperCrossing(new GraphEdge(tree1.getRoot(), tree2.getRoot()), null), null, 1);
            tree2RootData.setSubtreeLWAM(new Pair<>(lwam, null));
            tree2RootData.setSubtreeMASTSize(1);
            return lwam;
        }
        // base case for test
//        if(numberOfLeaves == 2){
//            PhylogenyNode tree2LeftChild = tree2.getRoot().getChildNode1();
//            MASTNodeData tree2LeftChildData = getMASTNodeDataFromNode(tree2LeftChild);
//            Phylogeny tree = createTreeWithOneNode(tree2LeftChild.getName());
//            tree2LeftChildData.setSubtreeLWAM(tree);
//            tree2LeftChildData.setSubtreeMASTSize(1);
//
//            PhylogenyNode tree2RightChild = tree2.getRoot().getChildNode2();
//            MASTNodeData tree2RightChildData = getMASTNodeDataFromNode(tree2RightChild);
//            tree = createTreeWithOneNode(tree2RightChild.getName());
//            tree2RightChildData.setSubtreeLWAM(tree);
//            getMASTNodeDataFromNode(tree2.getRoot().getChildNode2()).setSubtreeMASTSize(1);
//
//            MASTNodeData tree2RootData = getMASTNodeDataFromNode(tree2.getRoot());
//            tree2RootData.setSubtreeLWAM(tree2);
//            tree2RootData.setSubtreeMASTSize(2);
//
//            return new TreeAndSizePair(tree1, 2);
//        }

        setTwins(tree1, tree2);
        List<PhylogenyNode> tree1Decomposition = computeFirstDecomposition(tree1);
        List<List<PhylogenyNode>> tree2Decomposition = computeSecondDecompositionAndAddLWAMsToLeaves(tree2);

        // lis base case
//        if(tree1Decomposition.size() == numberOfLeaves && tree2Decomposition.size() == 1){
//            return baseCaseModified(tree1, tree2);
//        }

//        long time = System.nanoTime();
        List<Phylogeny> siSubtrees = induceSubtrees(tree1Decomposition, tree1, tree2);

        computeMiSiLWAMs(tree1Decomposition, siSubtrees);


        // lwams[i,j] = LWAM(T1(x),T2(y)) where x is the i'th node of pi and y is the j'th node of X
        AgreementMatching[][] lwams = new AgreementMatching[tree1Decomposition.size()][tree2Decomposition.size()];
        AgreementMatching lwam = createGraphsAndComputeLWAM(tree1Decomposition, tree2Decomposition, siSubtrees, lwams);

        return lwam;
    }

    private AgreementMatching getLWAMIterative(Phylogeny tree1, Phylogeny tree2){

        Stack<Pair<Phylogeny, Phylogeny>> treePairsForInit = new Stack<>();
        Stack<DataForCalculatingLWAM> dataForCalculatingLWAMStack = new Stack<>();
        treePairsForInit.push(new Pair<>(tree1, tree2));
        AgreementMatching baseCaseLwam = null;
        while (!treePairsForInit.empty()) {
            Pair<Phylogeny, Phylogeny> treePair = treePairsForInit.pop();
            Phylogeny t1 = treePair.getLeft();
            Phylogeny t2 = treePair.getRight();

            addNodeDataReferences(t1);
            addNodeDataReferences(t2);

            t1.recalculateNumberOfExternalDescendants(true);
            t2.recalculateNumberOfExternalDescendants(true);
            int numberOfLeaves = t1.getRoot().getNumberOfExternalNodes();

            // simple base case
            if (numberOfLeaves == 1) {
                MASTNodeData tree2RootData = getMASTNodeDataFromNode(t2.getRoot());
                baseCaseLwam = new AgreementMatching(new ProperCrossing(new GraphEdge(t1.getRoot(), t2.getRoot()), null), null, 1);
                tree2RootData.setSubtreeLWAM(new Pair<>(baseCaseLwam, null));
                tree2RootData.setSubtreeMASTSize(1);
                continue;
            }

            // base case size 2
//        if(numberOfLeaves == 2){
//            PhylogenyNode tree2LeftChild = tree2.getRoot().getChildNode1();
//            MASTNodeData tree2LeftChildData = getMASTNodeDataFromNode(tree2LeftChild);
//            Phylogeny tree = createTreeWithOneNode(tree2LeftChild.getName());
//            tree2LeftChildData.setSubtreeLWAM(tree);
//            tree2LeftChildData.setSubtreeMASTSize(1);
//
//            PhylogenyNode tree2RightChild = tree2.getRoot().getChildNode2();
//            MASTNodeData tree2RightChildData = getMASTNodeDataFromNode(tree2RightChild);
//            tree = createTreeWithOneNode(tree2RightChild.getName());
//            tree2RightChildData.setSubtreeLWAM(tree);
//            getMASTNodeDataFromNode(tree2.getRoot().getChildNode2()).setSubtreeMASTSize(1);
//
//            MASTNodeData tree2RootData = getMASTNodeDataFromNode(tree2.getRoot());
//            tree2RootData.setSubtreeLWAM(tree2);
//            tree2RootData.setSubtreeMASTSize(2);
//
//            return new TreeAndSizePair(tree1, 2);
//        }

            setTwins(t1, t2);
            List<PhylogenyNode> tree1Decomposition = computeFirstDecomposition(t1);
            List<List<PhylogenyNode>> tree2Decomposition = computeSecondDecompositionAndAddLWAMsToLeaves(t2);

            // lis base case
//        if(tree1Decomposition.size() == numberOfLeaves && tree2Decomposition.size() == 1){
//            return baseCaseModified(tree1, tree2);
//        }

            List<Phylogeny> siSubtrees = induceSubtrees(tree1Decomposition, t1, t2);
            addMiAndSiTreePairsToStack(tree1Decomposition, siSubtrees, treePairsForInit);
            dataForCalculatingLWAMStack.push(new DataForCalculatingLWAM(tree1Decomposition, tree2Decomposition, siSubtrees));
        }

        AgreementMatching resultingLWAM = null;
        if(dataForCalculatingLWAMStack.empty())
            return baseCaseLwam;

        while (!dataForCalculatingLWAMStack.empty()){
            DataForCalculatingLWAM dataForCalculatingLWAM = dataForCalculatingLWAMStack.pop();
            List<PhylogenyNode> tree1Decomposition = dataForCalculatingLWAM.getTree1Decomposition();
            List<List<PhylogenyNode>> tree2Decomposition = dataForCalculatingLWAM.getTree2Decomposition();
            List<Phylogeny> siSubtrees = dataForCalculatingLWAM.getSiSubtrees();

            // lwams[i,j] = LWAM(T1(x),T2(y)) where x is the i'th node of pi and y is the j'th node of X
            AgreementMatching[][] lwams = new AgreementMatching[tree1Decomposition.size()][tree2Decomposition.size()];
            resultingLWAM = createGraphsAndComputeLWAM(tree1Decomposition, tree2Decomposition, siSubtrees, lwams);
        }

        return resultingLWAM;
    }

    // Initial setup
    public void addNodeDataReferences(Phylogeny tree){
        if(tree.getRoot().getNodeData().getReferences() != null) return; // Node data has already been added.

        PhylogenyNodeIterator iterator = tree.iteratorPostorder();
        while (iterator.hasNext()){
            PhylogenyNode currentNode = iterator.next();
            NodeDataReference nodeDataReference = new NodeDataReference();
            nodeDataReference.setMastNodeData(new MASTNodeData());
            currentNode.getNodeData().addReference(nodeDataReference);
        }
    }
    public void setTwins(Phylogeny tree1, Phylogeny tree2){
        List<PhylogenyNode> tree2Leaves = tree2.getExternalNodes();
        PhylogenyNodeIterator iterator = tree1.iteratorPreorder();
        while (iterator.hasNext()) {
            PhylogenyNode currentNode = iterator.next();
            if (currentNode.isExternal()) {
                MASTNodeData mastNodeData = getMASTNodeDataFromNode(currentNode);

                if(mastNodeData.getTwin() != null) return; // Twins have already been set.

                int name = Integer.parseInt(currentNode.getName());
                PhylogenyNode twin = tree2Leaves.get(name);
                mastNodeData.setTwin(twin);

                MASTNodeData twinMastNodeData = getMASTNodeDataFromNode(twin);
                twinMastNodeData.setTwin(currentNode);
            }
        }
    }

    // LIS case
    private Phylogeny baseCase(Phylogeny tree1, Phylogeny tree2) {
        PhylogenyNode[] tree1LeavesTopDown = getLeavesTopDown(tree1);
        PhylogenyNode[] tree2LeavesTopDown = getLeavesTopDown(tree2);

        // set LIS numbers
        for (int i = 0; i < tree1LeavesTopDown.length; i++) {
            PhylogenyNode currentNode = tree1LeavesTopDown[i];
            MASTNodeData mastNodeData = getMASTNodeDataFromNode(currentNode);
            mastNodeData.setLisNumber(i);
            MASTNodeData twinMastNodeData = getMASTNodeDataFromNode(mastNodeData.getTwin());
            twinMastNodeData.setLisNumber(i);
        }

        int[] numbers = getLisNumbersFromLeaves(tree2LeavesTopDown);
        int[] lis = LongestIncreasingSubsequence.findLIS(numbers);

        int i = 0;
        Phylogeny tree = new Phylogeny();
        PhylogenyNode currentBottomMostNode = new PhylogenyNode();
        tree.setRoot(currentBottomMostNode);

        boolean hasFoundAdditionalLeaf = false;
        PhylogenyNode lastLeaf = new PhylogenyNode();
        for (PhylogenyNode currentLeaf : tree2LeavesTopDown){
            int currentLeafLisNumber = getMASTNodeDataFromNode(currentLeaf).getLisNumber();
            if(currentLeafLisNumber == lis[i]){
                if(i == lis.length-1){
                    lastLeaf.setName(currentLeaf.getName());
                }
                else {
                    PhylogenyNode newLeaf = new PhylogenyNode();
                    newLeaf.setName(currentLeaf.getName());
                    currentBottomMostNode.setChild1(newLeaf);
                    PhylogenyNode newNode = new PhylogenyNode();
                    currentBottomMostNode.setChild2(newNode);
                    currentBottomMostNode = newNode;

                    i++;
                }
            }
            else if(i == lis.length-1 && !hasFoundAdditionalLeaf && currentLeafLisNumber > lis[lis.length-2]){
                PhylogenyNode additionalLeaf = new PhylogenyNode();
                additionalLeaf.setName(currentLeaf.getName());
                currentBottomMostNode.setChild1(additionalLeaf);
                currentBottomMostNode = additionalLeaf;
                hasFoundAdditionalLeaf = true;
            }
        }
        currentBottomMostNode.getParent().setChild2(lastLeaf);

        return tree;
    }
    private Phylogeny baseCaseModifiedOld(Phylogeny tree1, Phylogeny tree2) {
        PhylogenyNode[] tree1LeavesBottomUp = getLeavesBottomUp(tree1);
        PhylogenyNode[] tree2LeavesBottomUp = getLeavesBottomUp(tree2);

        // set LIS numbers
        for (int i = 0; i < tree1LeavesBottomUp.length; i++) {
            PhylogenyNode currentNode = tree1LeavesBottomUp[i];
            MASTNodeData mastNodeData = getMASTNodeDataFromNode(currentNode);
            mastNodeData.setLisNumber(i);
            MASTNodeData twinMastNodeData = getMASTNodeDataFromNode(mastNodeData.getTwin());
            twinMastNodeData.setLisNumber(i);
        }

        int[] numbers = getLisNumbersFromLeaves(tree2LeavesBottomUp);
        LongestIncreasingSubsequence lisFinder = new LongestIncreasingSubsequence();
        int[] lis = lisFinder.findLISModified(numbers);
        int[] lisLengths = lisFinder.getLisLengths();

        PhylogenyNodeIterator t2Iterator = tree2.iteratorLevelOrder();
        int j = numbers.length - 1;
        while (t2Iterator.hasNext()){
            PhylogenyNode currentNode = t2Iterator.next();
            MASTNodeData currentNodeMastNodeData = getMASTNodeDataFromNode(currentNode);
            if(currentNode.isExternal()){
                currentNodeMastNodeData.setSubtreeMASTSize(1);
            }
            else {
                currentNodeMastNodeData.setSubtreeMASTSize(lisLengths[j]);
                j--;
            }
        }

        Phylogeny tree = createTreeFromLIS(lis, tree2LeavesBottomUp);

        return tree;
    }
    public Phylogeny getMASTUsingMLIS(Phylogeny tree1, Phylogeny tree2) {
        addNodeDataReferences(tree1);
        addNodeDataReferences(tree2);
        tree1.recalculateNumberOfExternalDescendants(true);
        tree2.recalculateNumberOfExternalDescendants(true);
        setTwins(tree1, tree2);

        PhylogenyNode[] tree1LeavesTopDown = getLeavesTopDown(tree1);
        PhylogenyNode[] tree2LeavesTopDown = getLeavesTopDown(tree2);

        // set LIS numbers
        for (int i = 0; i < tree1LeavesTopDown.length; i++) {
            PhylogenyNode currentNode = tree1LeavesTopDown[i];
            MASTNodeData mastNodeData = getMASTNodeDataFromNode(currentNode);
            mastNodeData.setLisNumber(i);
            MASTNodeData twinMastNodeData = getMASTNodeDataFromNode(mastNodeData.getTwin());
            twinMastNodeData.setLisNumber(i);
        }

        int[] numbers = getLisNumbersFromLeaves(tree2LeavesTopDown);
        LongestIncreasingSubsequence lisFinder = new LongestIncreasingSubsequence();
        int[] lis = lisFinder.findLISModified(numbers);

//        int[] lisLengths = lisFinder.getLisLengths();
//        PhylogenyNodeIterator t2Iterator = tree2.iteratorLevelOrder();
//        int j = numbers.length - 1;
//        while (t2Iterator.hasNext()){
//            PhylogenyNode currentNode = t2Iterator.next();
//            MASTNodeData currentNodeMastNodeData = getMASTNodeDataFromNode(currentNode);
//            if(currentNode.isExternal()){
//                currentNodeMastNodeData.setSubtreeMASTSize(1);
//            }
//            else {
//                currentNodeMastNodeData.setSubtreeMASTSize(lisLengths[j]);
//                j--;
//            }
//        }

        Phylogeny tree = createTreeFromLIS(lis, tree2LeavesTopDown);

        return tree;
    }
    private Phylogeny createTreeFromLIS(int[] lis, PhylogenyNode[] tree2LeavesTopDown) {
        int i = 0;
        Phylogeny tree = new Phylogeny();
        PhylogenyNode currentBottomMostNode = new PhylogenyNode();
        tree.setRoot(currentBottomMostNode);

        for (PhylogenyNode currentLeaf : tree2LeavesTopDown){
            int currentLeafLisNumber = getMASTNodeDataFromNode(currentLeaf).getLisNumber();
            if(currentLeafLisNumber == lis[i]){
                if(i == lis.length-1){
                    currentBottomMostNode.setName(currentLeaf.getName());
                }
                else {
                    PhylogenyNode newLeaf = new PhylogenyNode();
                    newLeaf.setName(currentLeaf.getName());
                    currentBottomMostNode.setChild1(newLeaf);
                    PhylogenyNode newNode = new PhylogenyNode();
                    currentBottomMostNode.setChild2(newNode);
                    currentBottomMostNode = newNode;

                    i++;
                }
            }
        }
        return tree;
    }
    private int[] getLisNumbersFromLeaves(PhylogenyNode[] tree2LeavesTopDown) {
        int[] numbers = new int[tree2LeavesTopDown.length];
        for (int i = 0 ; i < tree2LeavesTopDown.length ; i++){
            PhylogenyNode currentLeaf = tree2LeavesTopDown[i];
            MASTNodeData mastNodeData = getMASTNodeDataFromNode(currentLeaf);
            numbers[i] = mastNodeData.getLisNumber();
        }

//        PhylogenyNode secondLastLeaf = tree2LeavesTopDown[tree2LeavesTopDown.length-2];
//        PhylogenyNode lastLeaf = tree2LeavesTopDown[tree2LeavesTopDown.length-1];
//        int secondLastLisNumber = getMASTNodeDataFromNode(secondLastLeaf).getLisNumber();
//        int lastLisNumber = getMASTNodeDataFromNode(lastLeaf).getLisNumber();
//        if(secondLastLisNumber < lastLisNumber){
//            numbers[tree2LeavesTopDown.length-2] = secondLastLisNumber;
//            numbers[tree2LeavesTopDown.length-1] = lastLisNumber;
//        }
//        else {
//            numbers[tree2LeavesTopDown.length-2] = lastLisNumber;
//            numbers[tree2LeavesTopDown.length-1] = secondLastLisNumber;
//        }
        return numbers;
    }
    private PhylogenyNode[] getLeavesTopDown(Phylogeny tree) {
        int numberOfLeaves = tree.getRoot().getNumberOfExternalNodes();
        PhylogenyNode[] treeLeavesTopDown = new PhylogenyNode[numberOfLeaves];
        PhylogenyNodeIterator iteratorLevelOrder = tree.iteratorLevelOrder();
        int i = 0;
        while (iteratorLevelOrder.hasNext()){
            PhylogenyNode currentNode = iteratorLevelOrder.next();
            if(currentNode.isExternal()){
                treeLeavesTopDown[i] = currentNode;
                i++;
            }
        }
        return treeLeavesTopDown;
    }
    private PhylogenyNode[] getLeavesBottomUp(Phylogeny tree) {
        int numberOfLeaves = tree.getRoot().getNumberOfExternalNodes();
        PhylogenyNode[] treeLeavesBottomUp = new PhylogenyNode[numberOfLeaves];
        PhylogenyNodeIterator iteratorLevelOrder = tree.iteratorLevelOrder();
        int i = numberOfLeaves-1;
        while (iteratorLevelOrder.hasNext()){
            PhylogenyNode currentNode = iteratorLevelOrder.next();
            if(currentNode.isExternal()){
                treeLeavesBottomUp[i] = currentNode;
                i--;
            }
        }
        return treeLeavesBottomUp;
    }

    // Decompositions
    public List<PhylogenyNode> computeFirstDecomposition(Phylogeny tree){
        List<PhylogenyNode> result = new ArrayList<>();

        PhylogenyNode currentNode = tree.getRoot();
        while (!currentNode.isExternal()){
            result.add(currentNode);
            PhylogenyNode firstChild = currentNode.getChildNode1();
            PhylogenyNode secondChild = currentNode.getChildNode2();
            currentNode = firstChild.getNumberOfExternalNodes() > secondChild.getNumberOfExternalNodes()
                    ? firstChild : secondChild;
        }
        result.add(currentNode);

        return result;
    }
    public List<List<PhylogenyNode>> computeSecondDecompositionAndAddLWAMsToLeaves(Phylogeny tree){
        List<List<PhylogenyNode>> result = new ArrayList<>();
        Stack<PhylogenyNode> remainingStartNodes = new Stack<>();
        PhylogenyNode root = tree.getRoot();
        if(root.isExternal()) return result;
        remainingStartNodes.add(root);

        while (!remainingStartNodes.isEmpty()){
            PhylogenyNode firstNode = remainingStartNodes.pop();
            List<PhylogenyNode> newPath = new ArrayList<>();
            PhylogenyNode currentNode = firstNode;

            while (!currentNode.isExternal()){
                newPath.add(currentNode);
                currentNode.setLink(firstNode);
                PhylogenyNode firstChild = currentNode.getChildNode1();
                PhylogenyNode secondChild = currentNode.getChildNode2();
                if(firstChild.getNumberOfExternalNodes() > secondChild.getNumberOfExternalNodes()){
                    currentNode = firstChild;
                    if(!secondChild.isExternal()) remainingStartNodes.push(secondChild);
                    else {
                        MASTNodeData secondChildData = getMASTNodeDataFromNode(secondChild);
                        AgreementMatching lwam = new AgreementMatching(new ProperCrossing(new GraphEdge(secondChildData.getTwin(), secondChild), null), null, 1);
                        secondChildData.setSubtreeLWAM(new Pair<>(lwam, null));
                        secondChildData.setSubtreeMASTSize(1);
                    }
                }
                else {
                    currentNode = secondChild;
                    if(!firstChild.isExternal()) remainingStartNodes.push(firstChild);
                    else {
                        MASTNodeData firstChildData = getMASTNodeDataFromNode(firstChild);
                        AgreementMatching lwam = new AgreementMatching(new ProperCrossing(new GraphEdge(firstChildData.getTwin(), firstChild), null), null, 1);
                        firstChildData.setSubtreeLWAM(new Pair<>(lwam, null));
                        firstChildData.setSubtreeMASTSize(1);
                    }
                }
            }
            newPath.add(currentNode);
            currentNode.setLink(firstNode);
            result.add(newPath);
        }

        return result;
    }

    // Induce Si subtrees
    public List<Phylogeny> induceSubtrees(List<PhylogenyNode> t1CentroidPath, Phylogeny tree1, Phylogeny tree2){
        updateMiNumbers(t1CentroidPath);

        // Set T2 leaf numbers
//        PhylogenyNodeIterator tree2Iterator = tree2.iteratorPreorder();
//        int i = 0;
//        while (tree2Iterator.hasNext()){
//            PhylogenyNode currentNode = tree2Iterator.next();
//            if(currentNode.isExternal()){
//                getMASTNodeDataFromNode(currentNode).setLeafNumber(i);
//                i++;
//            }
//        }

        PhylogenyNode[] sortedTree1Leaves = sortTree1LeavesNew(tree2);

        List<PhylogenyNode>[] sortedSiLeaves = new List[t1CentroidPath.size()];
        for (PhylogenyNode leaf : sortedTree1Leaves){
            MASTNodeData mastNodeData = getMASTNodeDataFromNode(leaf);
            int miNumber = mastNodeData.getMiNumber();
            if (miNumber == 0) continue;
            if(sortedSiLeaves[miNumber] == null) sortedSiLeaves[miNumber] = new ArrayList<>();
            sortedSiLeaves[miNumber].add(mastNodeData.getTwin());
        }

        List<Phylogeny> result = new ArrayList<>();
        SubtreeProcessor subtreeProcessor = new SubtreeProcessor(tree2);

        for (List<PhylogenyNode> siLeaves : sortedSiLeaves){
            if(siLeaves == null) continue;
            result.add(subtreeProcessor.induceSubtree(siLeaves));
        }

        return result;
    }
    public void updateMiNumbers(List<PhylogenyNode> centroidPath){
        for (int i = 0; i < centroidPath.size() - 1; i++)
        {
            PhylogenyNode currentNode = centroidPath.get(i);
            PhylogenyNode firstChild = currentNode.getChildNode1();
            PhylogenyNode secondChild = currentNode.getChildNode2();

            PhylogenyNode miRootNode = (centroidPath.get(i+1).getId() == firstChild.getId()) ? secondChild : firstChild;

            if (miRootNode.isExternal()) {
                MASTNodeData mastNodeData = ((NodeDataReference) miRootNode.getNodeData().getReference()).getMastNodeData();
                mastNodeData.setMiNumber(i+1);
            }
            else {
                for (PhylogenyNode sChild : miRootNode.getAllExternalDescendants()) {
                    MASTNodeData mastNodeData = ((NodeDataReference) sChild.getNodeData().getReference()).getMastNodeData();
                    mastNodeData.setMiNumber(i+1);
                }
            }
        }
    }
    public PhylogenyNode[] sortTree1Leaves(Phylogeny tree1){
        PhylogenyNode[] sortedTree1Leaves = new PhylogenyNode[tree1.getRoot().getNumberOfExternalNodes()];
        PhylogenyNodeIterator iterator = tree1.iteratorPreorder();
        while (iterator.hasNext()){
            PhylogenyNode currentNode = iterator.next();
            if(currentNode.isExternal()){
                int leafNumber = getMASTNodeDataFromNode(getMASTNodeDataFromNode(currentNode).getTwin()).getLeafNumber();
                sortedTree1Leaves[leafNumber] = currentNode;
            }
        }
        return sortedTree1Leaves;
    }
    public PhylogenyNode[] sortTree1LeavesNew(Phylogeny tree2){
        PhylogenyNode[] sortedTree1Leaves = new PhylogenyNode[tree2.getRoot().getNumberOfExternalNodes()];
        PhylogenyNodeIterator iterator = tree2.iteratorPreorder();
        int i = 0;
        while (iterator.hasNext()){
            PhylogenyNode currentNode = iterator.next();
            if(currentNode.isExternal()){
                sortedTree1Leaves[i] = getMASTNodeDataFromNode(currentNode).getTwin();
                i++;
            }
        }
        return sortedTree1Leaves;
    }
    private PhylogenyNode[] sortTree1LeavesAndSetTwins_old(Phylogeny tree1, Phylogeny tree2){
        List<PhylogenyNode> tree2Leaves = new ArrayList<>();
        PhylogenyNodeIterator tree2Iterator = tree2.iteratorPreorder();
        while (tree2Iterator.hasNext()){
            PhylogenyNode currentNode = tree2Iterator.next();
            if(currentNode.isExternal()) tree2Leaves.add(currentNode);
        }

        int[] tree2LeavesOrdering = getLeavesOrdering(tree2Leaves);
        PhylogenyNode[] sortedTree1Leaves = new PhylogenyNode[tree2LeavesOrdering.length];
        PhylogenyNodeIterator tree1Iterator = tree1.iteratorPreorder();
        while (tree1Iterator.hasNext()){
            PhylogenyNode currentNode = tree1Iterator.next();
            if(currentNode.isExternal()){
                int name = Integer.parseInt(currentNode.getName());
                int tree2Index = tree2LeavesOrdering[name];
                sortedTree1Leaves[tree2Index] = currentNode;

                // Set twin
                MASTNodeData mastNodeData = getMASTNodeDataFromNode(currentNode);
                mastNodeData.setTwin(tree2Leaves.get(tree2Index));
            }


        }
        return sortedTree1Leaves;
    }
    private int[] getLeavesOrdering(List<PhylogenyNode> tree2Leaves) {

        int[] treeLeavesOrdering = new int[tree2Leaves.size()];

        for (int i = 0; i < tree2Leaves.size(); i++) {
            PhylogenyNode currentNode = tree2Leaves.get(i);
            int index = Integer.parseInt(currentNode.getName());
            treeLeavesOrdering[index] = i;
        }
        return treeLeavesOrdering;
    }

    // LWAM(Mi, Si)
    private void computeMiSiLWAMs(List<PhylogenyNode> tree1Decomposition, List<Phylogeny> siSubtrees) {
        for (int i = 0; i < siSubtrees.size(); i++) {
            PhylogenyNode currentTree1DecompositionNode = tree1Decomposition.get(i);
            PhylogenyNode firstChild = currentTree1DecompositionNode.getChildNode1();
            PhylogenyNode secondChild = currentTree1DecompositionNode.getChildNode2();

            PhylogenyNode miRoot = (tree1Decomposition.get(i+1).getId() == firstChild.getId()) ? secondChild : firstChild;
            Phylogeny mi = new Phylogeny();
            mi.setRoot(miRoot);
            mi = copyMiTreeAndSetTwins(mi);

            Phylogeny si = siSubtrees.get(i);
            getLWAMRecursive(mi, si);
        }
    }
    private void addMiAndSiTreePairsToStack(List<PhylogenyNode> tree1Decomposition, List<Phylogeny> siSubtrees, Stack<Pair<Phylogeny, Phylogeny>> treePairs) {
        for (int i = 0; i < siSubtrees.size(); i++) {
            PhylogenyNode currentTree1DecompositionNode = tree1Decomposition.get(i);
            PhylogenyNode firstChild = currentTree1DecompositionNode.getChildNode1();
            PhylogenyNode secondChild = currentTree1DecompositionNode.getChildNode2();

            PhylogenyNode miRoot = (tree1Decomposition.get(i+1).getId() == firstChild.getId()) ? secondChild : firstChild;
            Phylogeny mi = new Phylogeny();
            mi.setRoot(miRoot);
            mi = copyMiTreeAndSetTwins(mi);

            Phylogeny si = siSubtrees.get(i);
            treePairs.push(new Pair<>(mi, si));
        }
    }
    private Phylogeny copyMiTreeAndSetTwins(Phylogeny tree){
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

            // Add node data
            NodeDataReference nodeDataReference = new NodeDataReference();
            MASTNodeData newNodeMastNodeData = new MASTNodeData();
            nodeDataReference.setMastNodeData(newNodeMastNodeData);
            newNode.getNodeData().addReference(nodeDataReference);

            if(oldNode.isExternal()){
                newNode.setName(oldNode.getName());

                // set twins
                PhylogenyNode siTwin = getMASTNodeDataFromNode(getMASTNodeDataFromNode(oldNode).getTwin()).getSiNode();
                newNodeMastNodeData.setTwin(siTwin);
                getMASTNodeDataFromNode(siTwin).setTwin(newNode);

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

    // Create graphs
    private AgreementMatching createGraphsAndComputeLWAM(List<PhylogenyNode> tree1Decomposition, List<List<PhylogenyNode>> tree2Decomposition, List<Phylogeny> siSubtrees, AgreementMatching[][] lwams) {
        Graph[] graphs = findAndAddGraphEdges(tree1Decomposition, tree2Decomposition, siSubtrees);
        setPathNumbers(tree1Decomposition, tree2Decomposition);

        Phylogeny searchTree = null;
        for (int i = graphs.length-1; i >= 0; i--) {
            Graph graph = graphs[i];
            setGraphEdgesWeights(graph, lwams);
            searchTree = computeLWAMsAndMastSizes(graph, lwams);
            computeLWAMs(graph, searchTree, lwams);
        }

        return lwams[0][0];
    }
    public Graph[] findAndAddGraphEdges(List<PhylogenyNode> tree1Decomposition, List<List<PhylogenyNode>> tree2Decomposition, List<Phylogeny> siSubtrees) {
        Graph[] graphs = new Graph[tree2Decomposition.size()];
        // add graphs and references to graphs
        for (int i = 0; i < tree2Decomposition.size(); i++) {
            List<PhylogenyNode> tree2CentroidPath = tree2Decomposition.get(i);
            Graph graph = new Graph(tree2CentroidPath);
            PhylogenyNode startNode = tree2CentroidPath.get(0);
            MASTNodeData startNodeData = getMASTNodeDataFromNode(startNode);
            startNodeData.setGraph(graph);
            graphs[i] = graph;
        }

        // add u_i edges to graphs
        for (int i = 0; i < siSubtrees.size(); i++) {
            Phylogeny si = siSubtrees.get(i);
            PhylogenyNode u_i = tree1Decomposition.get(i);

            PhylogenyNodeIterator siIterator = si.iteratorPreorder();
            while (siIterator.hasNext()){
                PhylogenyNode currentSiNode = siIterator.next();
                PhylogenyNode currentSiParent = currentSiNode.getParent();
                PhylogenyNode startOfSiParentCentroidPath;
                PhylogenyNode currentSiParentT2Node;
                if(currentSiParent != null){
                    currentSiParentT2Node = getMASTNodeDataFromNode(currentSiParent).getT2Node();
                    startOfSiParentCentroidPath = currentSiParentT2Node.getLink();
                }
                else {
                    startOfSiParentCentroidPath = null;
                    currentSiParentT2Node = null;
                }

                PhylogenyNode currentT2Node = getMASTNodeDataFromNode(currentSiNode).getT2Node();

                // walk up through tree2
                while (currentT2Node != null){
                    if(currentT2Node == currentSiParentT2Node) break; // parent has done it from here
                    PhylogenyNode startOfCentroidPath = currentT2Node.getLink();
                    if(startOfCentroidPath == null){ // currentT2Node is not on a path
                        currentT2Node = currentT2Node.getParent();
                        continue;
                    }
                    MASTNodeData startOfCentroidPathNodeData = getMASTNodeDataFromNode(startOfCentroidPath);
                    Graph currentGraph = startOfCentroidPathNodeData.getGraph();

                    GraphEdge newEdge = new GraphEdge(u_i, currentT2Node);
                    newEdge.setMapNode(currentSiNode);
                    currentGraph.addEdge(newEdge);
                    if(startOfCentroidPath == startOfSiParentCentroidPath) break; // parent has done it from here
                    currentT2Node = startOfCentroidPath.getParent();
                }
            }
        }

        // add u_p edges to graphs
        PhylogenyNode u_p = tree1Decomposition.get(tree1Decomposition.size()-1);
        findAndAddGraphEdgesFromLeaf(u_p);

        return graphs;
    }
    private void findAndAddGraphEdgesFromLeaf(PhylogenyNode leaf) {
        MASTNodeData mastNodeData = getMASTNodeDataFromNode(leaf);
        PhylogenyNode currentT2Node = mastNodeData.getTwin();

        while (currentT2Node != null){
            PhylogenyNode startOfCentroidPath = currentT2Node.getLink();
            if(startOfCentroidPath == null){ // if currentT2Node is not on a path
                currentT2Node = currentT2Node.getParent();
                continue;
            }
            MASTNodeData startOfCentroidPathNodeData = getMASTNodeDataFromNode(startOfCentroidPath);
            Graph currentGraph = startOfCentroidPathNodeData.getGraph();
            GraphEdge newEdge = new GraphEdge(leaf, currentT2Node);
            currentGraph.addEdge(newEdge);
            currentT2Node = startOfCentroidPath.getParent();
        }
    }
    private void setPathNumbers(List<PhylogenyNode> tree1Decomposition, List<List<PhylogenyNode>> tree2Decomposition) {
        for (int i = 0 ; i < tree1Decomposition.size() ; i++){
            PhylogenyNode node = tree1Decomposition.get(i);
            getMASTNodeDataFromNode(node).setPathNumber(i);
        }
        for (int i = 0; i < tree2Decomposition.size(); i++) {
            PhylogenyNode node = tree2Decomposition.get(i).get(0);
            getMASTNodeDataFromNode(node).setPathNumber(i);
        }
    }
    private void setGraphEdgesWeights(Graph graph, AgreementMatching[][] lwams){
        for(GraphEdge edge : graph.getEdges()){
            PhylogenyNode leftNode = edge.getLeft();
            PhylogenyNode rightNode = edge.getRight();
            PhylogenyNode mapNode = edge.getMapNode();

            if(leftNode.isExternal() || rightNode.isExternal()){
                edge.setWhiteWeight(1);
                edge.setGreenWeight(1);
                edge.setRedWeight(1);
                continue;
            }

            int whiteWeight = computeWhiteWeight(rightNode, mapNode);
            edge.setWhiteWeight(whiteWeight);

            int greenWeight = getMASTNodeDataFromNode(mapNode).getSubtreeMASTSize();
            edge.setGreenWeight(greenWeight);

            int redWeight = computeRedWeight(leftNode, rightNode, lwams);
            edge.setRedWeight(redWeight);

        }
    }
    private int computeWhiteWeight(PhylogenyNode rightNode, PhylogenyNode mapNode) {
        int whiteWeight;
        if(getMASTNodeDataFromNode(mapNode).getT2Node() != rightNode){ // map(i,j) != v_j
            whiteWeight = getMASTNodeDataFromNode(mapNode).getSubtreeMASTSize();
        }
        else {
            PhylogenyNode mapNodeFirstChild = mapNode.getChildNode1();
            PhylogenyNode mapNodeSecondChild = mapNode.getChildNode2();
            PhylogenyNode rightNodeFirstChild = rightNode.getChildNode1();

            // child is not on the same path as rightNode, i.e. root of N_j
            if(rightNodeFirstChild.getLink() != rightNode.getLink()){
                whiteWeight = getMASTNodeDataFromNode(mapNodeFirstChild).getSubtreeMASTSize();
            }
            else {
                whiteWeight = getMASTNodeDataFromNode(mapNodeSecondChild).getSubtreeMASTSize();
            }
        }
        return whiteWeight;
    }
    private int computeRedWeight(PhylogenyNode leftNode, PhylogenyNode rightNode, AgreementMatching[][] lwams) {
        PhylogenyNode rightNodeFirstChild = rightNode.getChildNode1();
        PhylogenyNode rightNodeSecondChild = rightNode.getChildNode2();
        PhylogenyNode n_jRoot;
        if(rightNodeFirstChild.getLink() == null || rightNodeFirstChild.getLink() == rightNodeFirstChild){
            n_jRoot = rightNodeFirstChild;
        }
        else n_jRoot = rightNodeSecondChild;
        if(n_jRoot.isExternal()) return 1;
        int leftNodePathNumber = getMASTNodeDataFromNode(leftNode).getPathNumber();
        int n_jRootPathNumber = getMASTNodeDataFromNode(n_jRoot).getPathNumber();
        return lwams[leftNodePathNumber][n_jRootPathNumber].getWeight();
    }

    // Compute largest weight agreement matchings
    public Phylogeny computeLWAMsAndMastSizesOld(Graph graph, AgreementMatching[][] lwams) {
        List<PhylogenyNode> leftSet = graph.getLeftSet();

        List<PhylogenyNode> rightSet = graph.getRightSet();
        double[] weights = setIndexNumbersAndGetWeights(graph, rightSet);

        Phylogeny searchTree = new WeightBalancedBinarySearchTree().constructTree(weights);

        List<GraphEdge> edges = graph.getEdges();

        int edgeIndex = edges.size()-1;
        for (int leftNodeIndex = leftSet.size()-1; leftNodeIndex >= 0; leftNodeIndex--) {
            PhylogenyNode leftNode = leftSet.get(leftNodeIndex);
            List<GraphEdge> edgesFromLeftNode = new ArrayList<>(); // bottom-up order
            GraphEdge currentEdgeToAdd;
            while (edgeIndex >= 0){
                currentEdgeToAdd = edges.get(edgeIndex);
                if(currentEdgeToAdd.getLeft() == leftNode){
                    edgesFromLeftNode.add(currentEdgeToAdd);
                    edgeIndex--;
                }
                else break;
            }

            // get search tree node and ancestors
            PhylogenyNode currentSearchTreeNode = searchTree.getRoot();
            List<PhylogenyNode> ancestors = new ArrayList<>();

            // process white edges top-down
            for (int i = edgesFromLeftNode.size()-1; i >=0; i--) {
                GraphEdge currentEdge = edgesFromLeftNode.get(i);
                PhylogenyNode rightNode = currentEdge.getRight();
                int rightNodeIndex = getGraphNodeData(rightNode).getIndex();

                int currentSearchTreeNodeMaxIndex = getSearchTreeNodeData(currentSearchTreeNode).getMaxIndex();
                if(rightNodeIndex > currentSearchTreeNodeMaxIndex){
                    while (rightNodeIndex > currentSearchTreeNodeMaxIndex){
                        currentSearchTreeNode = currentSearchTreeNode.getParent();
                        currentSearchTreeNodeMaxIndex = getSearchTreeNodeData(currentSearchTreeNode).getMaxIndex();
                        ancestors.remove(ancestors.size()-1);
                    }
                    currentSearchTreeNode = currentSearchTreeNode.getChildNode2();
                }

                while (true){
                    ancestors.add(currentSearchTreeNode);
                    if(currentSearchTreeNode.isExternal()) break;
                    SearchTreeNodeData searchTreeNodeData = getSearchTreeNodeData(currentSearchTreeNode);
                    if (rightNodeIndex < searchTreeNodeData.getIndex())
                        currentSearchTreeNode = currentSearchTreeNode.getChildNode1();
                    else currentSearchTreeNode = currentSearchTreeNode.getChildNode2();
                }
                processSingleWhiteEdgeFromNode(ancestors, currentEdge);
            }

            currentSearchTreeNode = searchTree.getRoot();
            ancestors = new ArrayList<>();

            // process red and green edges bottom-up
            for (GraphEdge currentEdge : edgesFromLeftNode) {
                PhylogenyNode rightNode = currentEdge.getRight();
                int rightNodeIndex = getGraphNodeData(rightNode).getIndex();

                int currentSearchTreeNodeLowIndex = getSearchTreeNodeData(currentSearchTreeNode).getLowIndex();
                if(rightNodeIndex < currentSearchTreeNodeLowIndex){
                    while (rightNodeIndex < currentSearchTreeNodeLowIndex){
                        currentSearchTreeNode = currentSearchTreeNode.getParent();
                        currentSearchTreeNodeLowIndex = getSearchTreeNodeData(currentSearchTreeNode).getLowIndex();
                        ancestors.remove(ancestors.size()-1);
                    }
                    currentSearchTreeNode = currentSearchTreeNode.getChildNode1();
                }

                while (true){
                    ancestors.add(currentSearchTreeNode);
                    if(currentSearchTreeNode.isExternal()) break;
                    SearchTreeNodeData searchTreeNodeData = getSearchTreeNodeData(currentSearchTreeNode);
                    if (rightNodeIndex < searchTreeNodeData.getIndex())
                        currentSearchTreeNode = currentSearchTreeNode.getChildNode1();
                    else currentSearchTreeNode = currentSearchTreeNode.getChildNode2();
                }

                processSingleRedEdgeFromNode(ancestors, currentEdge);
                processSingleGreenEdgeFromNode(ancestors, currentEdge);
            }

            // Compute LWAM(T1(u_i),N_j) = LWAM(T1(u_i),T2(x))
            AgreementMatching heaviestMatching;
            SearchTreeNodeData rootData = getSearchTreeNodeData(searchTree.getRoot());
            AgreementMatching m = rootData.getM();
            int mWeight = m == null ? 0 : m.getWeight();
            ProperCrossing x = rootData.getX();
            int xWeight = x == null ? 0 : x.getWeight();
            GraphEdge r = rootData.getR();
            int rWeight = r == null ? 0 : r.getRedWeight();
            if(mWeight > xWeight){
                if(rWeight > mWeight){
                    heaviestMatching = new AgreementMatching(new ProperCrossing(null, r), null, rWeight);
                }
                else {
                    heaviestMatching = m;
                }
            }
            else if(rWeight > xWeight){
                heaviestMatching = new AgreementMatching(new ProperCrossing(null, r), null, rWeight);
            }
            else {
                heaviestMatching = new AgreementMatching(x, null, xWeight);
            }
            int leftNodePathNumber = getMASTNodeDataFromNode(leftNode).getPathNumber();
            int rightSetPathNumber = getMASTNodeDataFromNode(rightSet.get(0)).getPathNumber();
            lwams[leftNodePathNumber][rightSetPathNumber] = heaviestMatching;
        }
        return searchTree;
    }
    public Phylogeny computeLWAMsAndMastSizes(Graph graph, AgreementMatching[][] lwams) {
        List<PhylogenyNode> leftSet = graph.getLeftSet();

        List<PhylogenyNode> rightSet = graph.getRightSet();
        double[] weights = setIndexNumbersAndGetWeights(graph, rightSet);

        Phylogeny searchTree = new WeightBalancedBinarySearchTree().constructTree(weights);

        List<GraphEdge> edges = graph.getEdges();

        int edgeIndex = edges.size()-1;
        for (int leftNodeIndex = leftSet.size()-1; leftNodeIndex >= 0; leftNodeIndex--) {
            PhylogenyNode leftNode = leftSet.get(leftNodeIndex);
            List<GraphEdge> edgesFromLeftNode = new ArrayList<>(); // bottom-up order
            GraphEdge currentEdgeToAdd;
            while (edgeIndex >= 0){
                currentEdgeToAdd = edges.get(edgeIndex);
                if(currentEdgeToAdd.getLeft() == leftNode){
                    edgesFromLeftNode.add(currentEdgeToAdd);
                    edgeIndex--;
                }
                else break;
            }

            if(edgesFromLeftNode.size() == 1){
                GraphEdge currentEdge = edgesFromLeftNode.get(0);
                List<PhylogenyNode> ancestors = findAncestors(searchTree, currentEdge);
                processSingleWhiteEdgeFromNode(ancestors, currentEdge);

                processSingleRedEdgeFromNode(ancestors, currentEdge);
                processSingleGreenEdgeFromNode(ancestors, currentEdge);
            }
            else {
                processWhiteEdgesFromNode(searchTree, edgesFromLeftNode);
                processRedAndGreenEdgesFromNode(searchTree, edgesFromLeftNode);
            }


            // Compute LWAM(T1(u_i),N_j) = LWAM(T1(u_i),T2(x))
            AgreementMatching heaviestMatching;
            SearchTreeNodeData rootData = getSearchTreeNodeData(searchTree.getRoot());
            AgreementMatching m = rootData.getM();
            int mWeight = m == null ? 0 : m.getWeight();
            ProperCrossing x = rootData.getX();
            int xWeight = x == null ? 0 : x.getWeight();
            GraphEdge r = rootData.getR();
            int rWeight = r == null ? 0 : r.getRedWeight();
            if(mWeight > xWeight){
                if(rWeight > mWeight){
                    heaviestMatching = new AgreementMatching(new ProperCrossing(null, r), null, rWeight);
                }
                else {
                    heaviestMatching = m;
                }
            }
            else if(rWeight > xWeight){
                heaviestMatching = new AgreementMatching(new ProperCrossing(null, r), null, rWeight);
            }
            else {
                heaviestMatching = new AgreementMatching(x, null, xWeight);
            }
            int leftNodePathNumber = getMASTNodeDataFromNode(leftNode).getPathNumber();
            int rightSetPathNumber = getMASTNodeDataFromNode(rightSet.get(0)).getPathNumber();
            lwams[leftNodePathNumber][rightSetPathNumber] = heaviestMatching;
        }
        return searchTree;
    }

    private List<PhylogenyNode> findAncestors(Phylogeny searchTree, GraphEdge currentEdge) {
        // get search tree node and ancestors
        PhylogenyNode currentSearchTreeNode = searchTree.getRoot();
        List<PhylogenyNode> ancestors = new ArrayList<>();

        PhylogenyNode rightNode = currentEdge.getRight();
        int rightNodeIndex = getGraphNodeData(rightNode).getIndex();

        int currentSearchTreeNodeMaxIndex = getSearchTreeNodeData(currentSearchTreeNode).getMaxIndex();
        if(rightNodeIndex > currentSearchTreeNodeMaxIndex){
            while (rightNodeIndex > currentSearchTreeNodeMaxIndex){
                currentSearchTreeNode = currentSearchTreeNode.getParent();
                currentSearchTreeNodeMaxIndex = getSearchTreeNodeData(currentSearchTreeNode).getMaxIndex();
                ancestors.remove(ancestors.size()-1);
            }
            currentSearchTreeNode = currentSearchTreeNode.getChildNode2();
        }

        while (true){
            ancestors.add(currentSearchTreeNode);
            if(currentSearchTreeNode.isExternal()) break;
            SearchTreeNodeData searchTreeNodeData = getSearchTreeNodeData(currentSearchTreeNode);
            if (rightNodeIndex < searchTreeNodeData.getIndex())
                currentSearchTreeNode = currentSearchTreeNode.getChildNode1();
            else currentSearchTreeNode = currentSearchTreeNode.getChildNode2();
        }
        return ancestors;
    }

    private void processSingleWhiteEdgeFromNode(List<PhylogenyNode> ancestors, GraphEdge whiteEdge){
        List<PhylogenyNode> rfringe = getFringe(ancestors, false);

        // find largest m, largest x and largest y
        AgreementMatching maxM = null;
        int maxMWeight = 0;
        ProperCrossing maxX = null;
        int maxXWeight = 0;
        ProperCrossing maxY = null;
        int maxYWeight = 0;
        for (PhylogenyNode currentNode : rfringe){
            SearchTreeNodeData nodeData = getSearchTreeNodeData(currentNode);
            AgreementMatching m = nodeData.getM();
            if(m != null){ // && m.getTopmostEdge().getLeft() != whiteEdge.getLeft()){
                int mWeight = m.getWeight();
                if(mWeight > maxMWeight){
                    maxM = m;
                    maxMWeight = mWeight;
                }
            }

            ProperCrossing x = nodeData.getX();
            if(x != null){ // && x.getGreenEdge().getLeft() != whiteEdge.getLeft()){
                int xWeight = x.getWeight();
                if(xWeight > maxXWeight){
                    maxX = x;
                    maxXWeight = xWeight;
                }
            }

            ProperCrossing y = nodeData.getY();
            if(y != null){ // && y.getGreenEdge().getLeft() != whiteEdge.getLeft()){
                int yWeight = y.getWeight();
                if(yWeight > maxYWeight){
                    maxY = y;
                    maxYWeight = yWeight;
                }
            }
        }

        // find largest proper crossing which is not a single green edge
        ProperCrossing maxGROrRedEdge = findLargestGRCrossingOrSingleRedEdge(ancestors);

        // find largst agreement matching with 'whiteEdge' as topmost white edge
        ProperCrossing largestProperCrossing = null;
        int largestProperCrossingWeight = 0;
        // largest proper crossing
        int maxGRWeight = maxGROrRedEdge == null ? 0 : maxGROrRedEdge.getWeight();
        if(maxXWeight > maxGRWeight){
            largestProperCrossing = maxX;
            largestProperCrossingWeight = maxXWeight;
        }
        else {
            largestProperCrossing = maxGROrRedEdge;
            largestProperCrossingWeight = maxGRWeight;
        }
        if(maxYWeight > largestProperCrossingWeight){
            largestProperCrossing = maxY;
            largestProperCrossingWeight = maxYWeight;
        }
        // largest agreement matching
        AgreementMatching largestAgreementMatching;
        if(maxMWeight > largestProperCrossingWeight){
            WhiteEdgeSequence whiteEdges = new WhiteEdgeSequence(whiteEdge, maxM.getWhiteEdges());
            int matchingWeight = maxM.getWeight() + whiteEdge.getWhiteWeight();
            largestAgreementMatching = new AgreementMatching(maxM.getProperCrossing(), whiteEdges, matchingWeight);
        }
        else if(largestProperCrossing == null) largestAgreementMatching = null;
        else {
            WhiteEdgeSequence whiteEdges = new WhiteEdgeSequence(whiteEdge, null);
            int matchingWeight = largestProperCrossingWeight + whiteEdge.getWhiteWeight();
            largestAgreementMatching = new AgreementMatching(largestProperCrossing, whiteEdges, matchingWeight);
        }

        // add agreement matching to graph
        if(largestAgreementMatching != null){
            updateM(ancestors, largestAgreementMatching);
        }
    }
    private ProperCrossing findLargestGRCrossingOrSingleRedEdge(List<PhylogenyNode> ancestors) {
        ProperCrossing maxGR = null;
        int maxGRWeight = 0;
        int currentMaxAncestorGWeight = 0;
        GraphEdge currentMaxAncestorG = null;
        for (int i = 0; i < ancestors.size()-1; i++) {
            PhylogenyNode currentAncestor = ancestors.get(i);
            SearchTreeNodeData nodeData = getSearchTreeNodeData(currentAncestor);
            GraphEdge ancestorG = nodeData.getG();
            if(ancestorG != null){
                int ancestorGWeight = ancestorG.getGreenWeight();
                if(ancestorGWeight > currentMaxAncestorGWeight){
                    currentMaxAncestorGWeight = ancestorGWeight;
                    currentMaxAncestorG = ancestorG;
                }
            }
            PhylogenyNode child = currentAncestor.getChildNode2();
            if(child != ancestors.get(i+1)){
                SearchTreeNodeData childData = getSearchTreeNodeData(child);
                GraphEdge r = childData.getR();
                if(r == null) continue;

                GraphEdge g = childData.getG();
                GraphEdge maxG;
                int maxGWeight;
                if(g == null || currentMaxAncestorGWeight > g.getGreenWeight()){
                    maxG = currentMaxAncestorG;
                    maxGWeight = currentMaxAncestorGWeight;
                }
                else {
                    maxG = g;
                    maxGWeight = g.getGreenWeight();
                }

                int gRWeight = maxGWeight + r.getRedWeight();
                if(gRWeight > maxGRWeight){
                    maxGRWeight = gRWeight;
                    maxGR = new ProperCrossing(maxG, r);
                }
            }
        }
        return maxGR;
    }
    private void updateM(List<PhylogenyNode> ancestors, AgreementMatching largestAgreementMatching) {
        int largestAgreementMatchingWeight = largestAgreementMatching.getWeight();
        for (PhylogenyNode currentNode : ancestors){
            SearchTreeNodeData currentNodeData = getSearchTreeNodeData(currentNode);
            AgreementMatching currentM = currentNodeData.getM();
            if(currentM == null || largestAgreementMatchingWeight > currentM.getWeight())
                currentNodeData.setM(largestAgreementMatching);
        }
    }

    private void processWhiteEdgesFromNode(Phylogeny searchTree, List<GraphEdge> edgesFromLeftNode) {
        // get search tree node and ancestors
        PhylogenyNode currentSearchTreeNode = searchTree.getRoot();

        // for processing white edges
        AgreementMatching currentMaxM = null;
        int currentMaxMWeight = 0;
        ProperCrossing currentMaxX = null;
        int currentMaxXWeight = 0;
        ProperCrossing currentMaxY = null;
        int currentMaxYWeight = 0;
        GraphEdge currentMaxG = null;
        int currentMaxGWeight = 0;
        ProperCrossing currentMaxGR = null;
        int currentMaxGRWeight = 0;
        AgreementMatching lwamInSubtree = null;

        // process white edges top-down
        for (int i = edgesFromLeftNode.size()-1; i >=0; i--) {
            GraphEdge currentEdge = edgesFromLeftNode.get(i);
            PhylogenyNode rightNode = currentEdge.getRight();
            int rightNodeIndex = getGraphNodeData(rightNode).getIndex();

            int currentSearchTreeNodeMaxIndex = getSearchTreeNodeData(currentSearchTreeNode).getMaxIndex();
            if(rightNodeIndex > currentSearchTreeNodeMaxIndex){

                while (rightNodeIndex > currentSearchTreeNodeMaxIndex){
                    currentSearchTreeNode = currentSearchTreeNode.getParent();
                    currentSearchTreeNodeMaxIndex = getSearchTreeNodeData(currentSearchTreeNode).getMaxIndex();

                    int lwamInSubtreeWeight = lwamInSubtree == null ? 0 : lwamInSubtree.getWeight();
                    SearchTreeNodeData searchTreeNodeData = getSearchTreeNodeData(currentSearchTreeNode);
                    AgreementMatching m = searchTreeNodeData.getM();
                    if(m == null || lwamInSubtreeWeight > m.getWeight()){
                        searchTreeNodeData.setM(lwamInSubtree);
                    }
                    else {
                        lwamInSubtree = m;
                    }
                }
                SearchTreeNodeData searchTreeNodeData = getSearchTreeNodeData(currentSearchTreeNode);
                currentMaxM = searchTreeNodeData.getMaxAncestorM();
                currentMaxMWeight = currentMaxM == null ? 0 : currentMaxM.getWeight();
                currentMaxG = searchTreeNodeData.getMaxAncestorG();
                currentMaxGWeight = currentMaxG == null ? 0 : currentMaxG.getGreenWeight();
                currentMaxX = searchTreeNodeData.getMaxAncestorX();
                currentMaxXWeight = currentMaxX == null ? 0 : currentMaxX.getWeight();
                currentMaxY = searchTreeNodeData.getMaxAncestorY();
                currentMaxYWeight = currentMaxY == null ? 0 : currentMaxY.getWeight();
                currentMaxGR = searchTreeNodeData.getMaxAncestorGR();
                currentMaxGRWeight = currentMaxGR == null ? 0 : currentMaxGR.getWeight();
            }

            while (true){
                if(currentSearchTreeNode.isExternal()) break;
                SearchTreeNodeData searchTreeNodeData = getSearchTreeNodeData(currentSearchTreeNode);

                GraphEdge currentNodeG = searchTreeNodeData.getG();
                if(currentNodeG != null){
                    int gWeight = currentNodeG.getGreenWeight();
                    if(gWeight > currentMaxGWeight){
                        currentMaxG = currentNodeG;
                        currentMaxGWeight = gWeight;
                    }
                }
                searchTreeNodeData.setMaxAncestorG(currentMaxG);

                PhylogenyNode leftChild = currentSearchTreeNode.getChildNode1();
                PhylogenyNode rightChild = currentSearchTreeNode.getChildNode2();
                if (rightNodeIndex < searchTreeNodeData.getIndex()){
                    currentSearchTreeNode = leftChild;

                    SearchTreeNodeData rightChildData = getSearchTreeNodeData(rightChild);
                    AgreementMatching rightChildM = rightChildData.getM();
                    if(rightChildM != null){
                        int mWeight = rightChildM.getWeight();
                        if(mWeight > currentMaxMWeight){
                            currentMaxM = rightChildM;
                            currentMaxMWeight = mWeight;
                        }
                    }
                    ProperCrossing rightChildX = rightChildData.getX();
                    if(rightChildX != null){
                        int xWeight = rightChildX.getWeight();
                        if(xWeight > currentMaxXWeight){
                            currentMaxX = rightChildX;
                            currentMaxXWeight = xWeight;
                        }
                    }
                    ProperCrossing rightChildY = rightChildData.getY();
                    if(rightChildY != null){
                        int yWeight = rightChildY.getWeight();
                        if(yWeight > currentMaxYWeight){
                            currentMaxY = rightChildY;
                            currentMaxYWeight = yWeight;
                        }
                    }

                    GraphEdge rightChildR = rightChildData.getR();
                    if(rightChildR != null){
                        GraphEdge rightChildG = rightChildData.getG();
                        GraphEdge maxG;
                        int maxGWeight;
                        if(rightChildG == null || currentMaxGWeight > rightChildG.getGreenWeight()){
                            maxG = currentMaxG;
                            maxGWeight = currentMaxGWeight;
                        }
                        else {
                            maxG = rightChildG;
                            maxGWeight = rightChildG.getGreenWeight();
                        }

                        int gRWeight = maxGWeight + rightChildR.getRedWeight();
                        if(gRWeight > currentMaxGRWeight){
                            currentMaxGRWeight = gRWeight;
                            currentMaxGR = new ProperCrossing(maxG, rightChildR);
                        }
                    }
                }
                else {
                    currentSearchTreeNode = rightChild;
                }
                SearchTreeNodeData newSearchTreeNodeData = getSearchTreeNodeData(currentSearchTreeNode);
                newSearchTreeNodeData.setMaxAncestorM(currentMaxM);
                newSearchTreeNodeData.setMaxAncestorX(currentMaxX);
                newSearchTreeNodeData.setMaxAncestorY(currentMaxY);
                newSearchTreeNodeData.setMaxAncestorGR(currentMaxGR);
            }
            lwamInSubtree = findLWAMWithWhiteEdgeAsTopmost(currentEdge, currentMaxM, currentMaxX, currentMaxY, currentMaxGR);

            int lwamInSubtreeWeight = lwamInSubtree == null ? 0 : lwamInSubtree.getWeight();
            SearchTreeNodeData currentSearchTreeNodeData = getSearchTreeNodeData(currentSearchTreeNode);
            AgreementMatching m = currentSearchTreeNodeData.getM();
            if(m == null || lwamInSubtreeWeight > m.getWeight()){
                currentSearchTreeNodeData.setM(lwamInSubtree);
            }
            else {
                lwamInSubtree = m;
            }
        }

        PhylogenyNode searchTreeRoot = searchTree.getRoot();

        while (currentSearchTreeNode != searchTreeRoot){
            currentSearchTreeNode = currentSearchTreeNode.getParent();

            int lwamInSubtreeWeight = lwamInSubtree == null ? 0 : lwamInSubtree.getWeight();
            SearchTreeNodeData currentSearchTreeNodeData = getSearchTreeNodeData(currentSearchTreeNode);
            AgreementMatching m = currentSearchTreeNodeData.getM();
            if(m == null || lwamInSubtreeWeight > m.getWeight()){
                currentSearchTreeNodeData.setM(lwamInSubtree);
            }
            else {
                lwamInSubtree = m;
            }
        }
    }
    private AgreementMatching findLWAMWithWhiteEdgeAsTopmost(GraphEdge whiteEdge, AgreementMatching maxM, ProperCrossing maxX, ProperCrossing maxY, ProperCrossing maxGROrRedEdge){
        int maxMWeight = maxM == null ? 0 : maxM.getWeight();
        int maxXWeight = maxX == null ? 0 : maxX.getWeight();
        int maxYWeight = maxY == null ? 0 : maxY.getWeight();

        // find largst agreement matching with 'whiteEdge' as topmost white edge
        ProperCrossing largestProperCrossing = null;
        int largestProperCrossingWeight = 0;
        // largest proper crossing
        int maxGRWeight = maxGROrRedEdge == null ? 0 : maxGROrRedEdge.getWeight();
        if(maxXWeight > maxGRWeight){
            largestProperCrossing = maxX;
            largestProperCrossingWeight = maxXWeight;
        }
        else {
            largestProperCrossing = maxGROrRedEdge;
            largestProperCrossingWeight = maxGRWeight;
        }
        if(maxYWeight > largestProperCrossingWeight){
            largestProperCrossing = maxY;
            largestProperCrossingWeight = maxYWeight;
        }
        // largest agreement matching
        AgreementMatching largestAgreementMatching;
        if(maxMWeight > largestProperCrossingWeight){
            WhiteEdgeSequence whiteEdges = new WhiteEdgeSequence(whiteEdge, maxM.getWhiteEdges());
            int matchingWeight = maxM.getWeight() + whiteEdge.getWhiteWeight();
            largestAgreementMatching = new AgreementMatching(maxM.getProperCrossing(), whiteEdges, matchingWeight);
        }
        else if(largestProperCrossing == null) largestAgreementMatching = null;
        else {
            WhiteEdgeSequence whiteEdges = new WhiteEdgeSequence(whiteEdge, null);
            int matchingWeight = largestProperCrossingWeight + whiteEdge.getWhiteWeight();
            largestAgreementMatching = new AgreementMatching(largestProperCrossing, whiteEdges, matchingWeight);
        }

        return largestAgreementMatching;
    }

    private void processSingleRedEdgeFromNode(List<PhylogenyNode> ancestors, GraphEdge redEdge){
        // update y(z) for z in ancestors
        // update g(y) for y in lfringe(z) and rfringe(z), z in ancestors
        // update r(z) for z in ancestors
        updateYAndGAndR(ancestors, redEdge);

        // remove g(z) for z in ancestors
        for (PhylogenyNode currentNode : ancestors){
            getSearchTreeNodeData(currentNode).setG(null);
        }
    }
    private void updateYAndGAndR(List<PhylogenyNode> ancestors, GraphEdge redEdge) {
        int currentMaxAncestorGWeight = 0;
        GraphEdge currentMaxAncestorG = null;
        for (int i = 0; i < ancestors.size(); i++) {
            PhylogenyNode currentNode = ancestors.get(i);
            SearchTreeNodeData currentNodeData = getSearchTreeNodeData(currentNode);
            GraphEdge g = currentNodeData.getG();
            if(g != null && g.getGreenWeight() > currentMaxAncestorGWeight){
                currentMaxAncestorG = g;
                currentMaxAncestorGWeight = g.getGreenWeight();
            }
            if(currentMaxAncestorG != null){
                // update g
                if(i != ancestors.size()-1){
                    PhylogenyNode leftChild = currentNode.getChildNode1();
                    PhylogenyNode rightChild = currentNode.getChildNode2();
                    PhylogenyNode childToUpdate;
                    if(leftChild != ancestors.get(i+1)) childToUpdate = leftChild;
                    else childToUpdate = rightChild;
                    SearchTreeNodeData childToUpdateData = getSearchTreeNodeData(childToUpdate);
                    GraphEdge childG = childToUpdateData.getG();
                    if(childG == null || currentMaxAncestorGWeight > childG.getGreenWeight())
                        childToUpdateData.setG(currentMaxAncestorG);
                }

                // update y
                GraphEdge r = currentNodeData.getR();
                if(r != null){
                    int gRWeight = currentMaxAncestorGWeight + r.getRedWeight();
                    ProperCrossing currentY = currentNodeData.getY();
                    if(currentY == null || gRWeight > currentY.getWeight())
                        currentNodeData.setY(new ProperCrossing(currentMaxAncestorG, r));
                }
            }

            // update r
            GraphEdge r = currentNodeData.getR();
            if(r == null || redEdge.getRedWeight() > r.getRedWeight())
                currentNodeData.setR(redEdge);
        }
    }

    private void processSingleGreenEdgeFromNode(List<PhylogenyNode> ancestors, GraphEdge greenEdge){
        // update g(z) for z in lfringe
        updateG(ancestors, greenEdge);

        // update x(z) for z in ancestors
        updateX(ancestors, greenEdge);
    }
    private void updateG(List<PhylogenyNode> ancestors, GraphEdge greenEdge) {
        GraphEdge currentMaxAncestorG = null;
        int currentMaxAncestorGWeight = 0;
        for (int i = 0; i < ancestors.size()-1; i++) {
            PhylogenyNode ancestorNode = ancestors.get(i);
            SearchTreeNodeData ancestorNodeData = getSearchTreeNodeData(ancestorNode);
            GraphEdge ancestorG = ancestorNodeData.getG();
            if(ancestorG != null && ancestorG.getGreenWeight() > currentMaxAncestorGWeight){
                currentMaxAncestorG = ancestorG;
                currentMaxAncestorGWeight = ancestorG.getGreenWeight();
            }
            PhylogenyNode nodeToBeUpdated = ancestorNode.getChildNode1();
            if(nodeToBeUpdated != ancestors.get(i+1)){
                SearchTreeNodeData nodeToBeUpdatedData = getSearchTreeNodeData(nodeToBeUpdated);
                GraphEdge nodeToBeUpdatedG = nodeToBeUpdatedData.getG();
                GraphEdge currentMaxG;
                if(nodeToBeUpdatedG != null && (currentMaxAncestorG == null || nodeToBeUpdatedG.getGreenWeight() > currentMaxAncestorGWeight)){
                    currentMaxG = nodeToBeUpdatedG;
                }
                else currentMaxG = currentMaxAncestorG;
                if(currentMaxG == null || greenEdge.getGreenWeight() > currentMaxG.getGreenWeight())
                    nodeToBeUpdatedData.setG(greenEdge);
                else nodeToBeUpdatedData.setG(currentMaxG);
            }


        }
    }
    private void updateX(List<PhylogenyNode> ancestors, GraphEdge greenEdge) {
        PhylogenyNode leaf = ancestors.get(ancestors.size() - 1);
        SearchTreeNodeData leafData = getSearchTreeNodeData(leaf);
        ProperCrossing leafX = leafData.getX();
        if(leafX == null || greenEdge.getGreenWeight() > leafX.getWeight())
            leafData.setX(new ProperCrossing(greenEdge, null));

        GraphEdge currentMaxLfringeR = null;
        int currentMaxLfringeRweight = 0;
        for (int i = ancestors.size()-2; i >= 0; i--) {
            PhylogenyNode currentNode = ancestors.get(i);
            PhylogenyNode leftChild = currentNode.getChildNode1();
            if(leftChild != ancestors.get(i+1)){
                SearchTreeNodeData leftChildData = getSearchTreeNodeData(leftChild);
                GraphEdge leftChildR = leftChildData.getR();
                if(leftChildR != null && leftChildR.getRedWeight() > currentMaxLfringeRweight){
                    currentMaxLfringeR = leftChildR;
                    currentMaxLfringeRweight = leftChildR.getRedWeight();
                }
            }
            SearchTreeNodeData currentNodeData = getSearchTreeNodeData(currentNode);
            ProperCrossing x = currentNodeData.getX();
            if(x == null || currentMaxLfringeRweight+greenEdge.getGreenWeight() > x.getWeight())
                currentNodeData.setX(new ProperCrossing(greenEdge, currentMaxLfringeR));
        }
    }

    private void processRedAndGreenEdgesFromNode(Phylogeny searchTree, List<GraphEdge> edgesFromLeftNode) {
        PhylogenyNode currentSearchTreeNode = searchTree.getRoot();
        // for processing red edges
        GraphEdge previousMaxAncestorG = null;
        GraphEdge previousEdge = null;

        // process red and green edges bottom-up
        for (GraphEdge currentEdge : edgesFromLeftNode) {
            PhylogenyNode rightNode = currentEdge.getRight();
            int rightNodeIndex = getGraphNodeData(rightNode).getIndex();

            int currentSearchTreeNodeLowIndex = getSearchTreeNodeData(currentSearchTreeNode).getLowIndex();
            if(rightNodeIndex < currentSearchTreeNodeLowIndex){
                ProperCrossing maxXSoFar = null;
                GraphEdge maxRSoFar = previousEdge;
                GraphEdge heaviesAddedGreenEdge = previousEdge;
                while (rightNodeIndex < currentSearchTreeNodeLowIndex){
                    PhylogenyNode previousSearchTreeNode = currentSearchTreeNode;
                    currentSearchTreeNode = currentSearchTreeNode.getParent();
                    currentSearchTreeNodeLowIndex = getSearchTreeNodeData(currentSearchTreeNode).getLowIndex();

                    // update nodes for red edge
                    SearchTreeNodeData searchTreeNodeData = getSearchTreeNodeData(currentSearchTreeNode);
                    // set y
                    GraphEdge maxAncestorG = searchTreeNodeData.getMaxAncestorG();
                    GraphEdge r = searchTreeNodeData.getR();
                    if(maxAncestorG != null && r != null){
                        int gRWeight = maxAncestorG.getGreenWeight() + r.getRedWeight();
                        ProperCrossing y = searchTreeNodeData.getY();
                        if(y == null || gRWeight > y.getWeight())
                            searchTreeNodeData.setY(new ProperCrossing(maxAncestorG, r));
                    }
                    // set g
                    PhylogenyNode offPathChild;
                    if(currentSearchTreeNode.getChildNode1() != previousSearchTreeNode){
                        offPathChild = currentSearchTreeNode.getChildNode1();
                    }
                    else {
                        offPathChild = currentSearchTreeNode.getChildNode2();
                    }
                    SearchTreeNodeData offPathChildData = getSearchTreeNodeData(offPathChild);
                    GraphEdge offPathChildG = offPathChildData.getG();
                    if(offPathChildG == null || (maxAncestorG != null && maxAncestorG.getGreenWeight() > offPathChildG.getGreenWeight())){
                        offPathChildData.setG(maxAncestorG);
                    }
                    searchTreeNodeData.setG(null);
                    searchTreeNodeData.setMaxAncestorG(null);
                    // set r
                    if(r == null || maxRSoFar.getRedWeight() > r.getRedWeight())
                        searchTreeNodeData.setR(maxRSoFar);
                    else
                        maxRSoFar = r;

                    // update nodes for green edge
                    GraphEdge leftOffPathChildR = null;
                    // set g
                    if(offPathChild == currentSearchTreeNode.getChildNode1()){
                        offPathChildG = offPathChildData.getG();
                        if(offPathChildG == null || heaviesAddedGreenEdge.getGreenWeight() > offPathChildG.getGreenWeight()){
                            offPathChildData.setG(heaviesAddedGreenEdge);
                        }

                        leftOffPathChildR = offPathChildData.getR();
                    }
                    // set x
                    GraphEdge sideTreeAddedGreenEdge = searchTreeNodeData.getHeaviesAddedGreenEdge();
                    searchTreeNodeData.setHeaviesAddedGreenEdge(null);
                    if(sideTreeAddedGreenEdge != null && sideTreeAddedGreenEdge.getGreenWeight() > heaviesAddedGreenEdge.getGreenWeight()){
                        heaviesAddedGreenEdge = sideTreeAddedGreenEdge;
                    }
                    int leftOffPathChildRWeight = leftOffPathChildR == null ? 0 : leftOffPathChildR.getRedWeight();
                    int maxXSoFarWeight = maxXSoFar == null ? 0 : maxXSoFar.getWeight();
                    ProperCrossing x = searchTreeNodeData.getX();
                    int xWeight = x == null ? 0 : x.getWeight();
                    if(xWeight >= maxXSoFarWeight){
                        maxXSoFar = x;
                        maxXSoFarWeight = maxXSoFar == null ? 0 : maxXSoFar.getWeight();
                    }
                    if(leftOffPathChildRWeight + heaviesAddedGreenEdge.getGreenWeight() > maxXSoFarWeight){
                        maxXSoFar = new ProperCrossing(heaviesAddedGreenEdge, leftOffPathChildR);
                    }
                    searchTreeNodeData.setX(maxXSoFar);
                }
                getSearchTreeNodeData(currentSearchTreeNode).setHeaviesAddedGreenEdge(currentEdge);
                currentSearchTreeNode = currentSearchTreeNode.getChildNode1();
                previousMaxAncestorG = null;
            }

            while (true){
                SearchTreeNodeData searchTreeNodeData = getSearchTreeNodeData(currentSearchTreeNode);

                GraphEdge currentG = searchTreeNodeData.getG();
                int currentGWeight = currentG == null ? 0 : currentG.getGreenWeight();
                if(previousMaxAncestorG == null || currentGWeight > previousMaxAncestorG.getGreenWeight()){
                    previousMaxAncestorG = currentG;
                }
                searchTreeNodeData.setMaxAncestorG(previousMaxAncestorG);

                if(currentSearchTreeNode.isExternal()) break;
                if (rightNodeIndex < searchTreeNodeData.getIndex())
                    currentSearchTreeNode = currentSearchTreeNode.getChildNode1();
                else currentSearchTreeNode = currentSearchTreeNode.getChildNode2();
            }

            // process red edge
            SearchTreeNodeData searchTreeNodeData = getSearchTreeNodeData(currentSearchTreeNode);
            // set y
            GraphEdge maxAncestorG = searchTreeNodeData.getMaxAncestorG();
            GraphEdge r = searchTreeNodeData.getR();
            if(maxAncestorG != null && r != null){
                int gRWeight = maxAncestorG.getGreenWeight() + r.getRedWeight();
                ProperCrossing y = searchTreeNodeData.getY();
                if(y == null || gRWeight > y.getWeight())
                    searchTreeNodeData.setY(new ProperCrossing(maxAncestorG, r));
            }
            // set g
            searchTreeNodeData.setG(null);
            searchTreeNodeData.setMaxAncestorG(null);
            // set r
            if(r == null || currentEdge.getRedWeight() > r.getRedWeight())
                searchTreeNodeData.setR(currentEdge);

            // process green edge
            ProperCrossing x = searchTreeNodeData.getX();
            if(x == null || currentEdge.getGreenWeight() > x.getWeight()){
                searchTreeNodeData.setX(new ProperCrossing(currentEdge, null));
            }

            previousEdge = currentEdge;
        }

        ProperCrossing maxXSoFar = null;
        GraphEdge maxRSoFar = previousEdge;
        GraphEdge heaviesAddedGreenEdge = previousEdge;
        PhylogenyNode searchTreeRoot = searchTree.getRoot();
        while(currentSearchTreeNode != searchTreeRoot){
            PhylogenyNode previousSearchTreeNode = currentSearchTreeNode;
            currentSearchTreeNode = currentSearchTreeNode.getParent();

            // update nodes for red edge
            SearchTreeNodeData searchTreeNodeData = getSearchTreeNodeData(currentSearchTreeNode);
            // set y
            GraphEdge maxAncestorG = searchTreeNodeData.getMaxAncestorG();
            GraphEdge r = searchTreeNodeData.getR();
            if(maxAncestorG != null && r != null){
                int gRWeight = maxAncestorG.getGreenWeight() + r.getRedWeight();
                ProperCrossing y = searchTreeNodeData.getY();
                if(y == null || gRWeight > y.getWeight())
                    searchTreeNodeData.setY(new ProperCrossing(maxAncestorG, r));
            }
            // set g
            PhylogenyNode offPathChild;
            if(currentSearchTreeNode.getChildNode1() != previousSearchTreeNode){
                offPathChild = currentSearchTreeNode.getChildNode1();
            }
            else {
                offPathChild = currentSearchTreeNode.getChildNode2();
            }
            SearchTreeNodeData offPathChildData = getSearchTreeNodeData(offPathChild);
            GraphEdge offPathChildG = offPathChildData.getG();
            if(offPathChildG == null || (maxAncestorG != null && maxAncestorG.getGreenWeight() > offPathChildG.getGreenWeight())){
                offPathChildData.setG(maxAncestorG);
            }
            searchTreeNodeData.setG(null);
            searchTreeNodeData.setMaxAncestorG(null);
            // set r
            if(r == null || maxRSoFar.getRedWeight() > r.getRedWeight())
                searchTreeNodeData.setR(maxRSoFar);
            else
                maxRSoFar = r;

            // update nodes for green edge
            GraphEdge leftOffPathChildR = null;
            // set g
            if(offPathChild == currentSearchTreeNode.getChildNode1()){
                offPathChildG = offPathChildData.getG();
                if(offPathChildG == null || heaviesAddedGreenEdge.getGreenWeight() > offPathChildG.getGreenWeight()){
                    offPathChildData.setG(heaviesAddedGreenEdge);
                }

                leftOffPathChildR = offPathChildData.getR();
            }
            // set x
            GraphEdge sideTreeAddedGreenEdge = searchTreeNodeData.getHeaviesAddedGreenEdge();
            searchTreeNodeData.setHeaviesAddedGreenEdge(null);
            if(sideTreeAddedGreenEdge != null && sideTreeAddedGreenEdge.getGreenWeight() > heaviesAddedGreenEdge.getGreenWeight()){
                heaviesAddedGreenEdge = sideTreeAddedGreenEdge;
            }
            int leftOffPathChildRWeight = leftOffPathChildR == null ? 0 : leftOffPathChildR.getRedWeight();
            int maxXSoFarWeight = maxXSoFar == null ? 0 : maxXSoFar.getWeight();
            ProperCrossing x = searchTreeNodeData.getX();
            int xWeight = x == null ? 0 : x.getWeight();
            if(xWeight >= maxXSoFarWeight){
                maxXSoFar = x;
                maxXSoFarWeight = maxXSoFar == null ? 0 : maxXSoFar.getWeight();
            }
            if(leftOffPathChildRWeight + heaviesAddedGreenEdge.getGreenWeight() > maxXSoFarWeight){
                maxXSoFar = new ProperCrossing(heaviesAddedGreenEdge, leftOffPathChildR);
            }
            searchTreeNodeData.setX(maxXSoFar);
        }
    }

    private void computeLWAMsArticle(Graph graph) {
//        System.out.println("\n\n\n");
        List<PhylogenyNode> leftSet = graph.getLeftSet();
//        System.out.println("Left set:");
//        for (PhylogenyNode node : leftSet){
//            System.out.println(getMASTNodeDataFromNode(node).getPathNumber());
//        }

        List<PhylogenyNode> rightSet = graph.getRightSet();
        double[] weights = setIndexNumbersAndGetWeights(graph, rightSet);

//        System.out.println("Right set:");
//        for (PhylogenyNode node : rightSet){
//            System.out.println(getGraphNodeData(node).getIndex());
//        }

        Phylogeny searchTree = new WeightBalancedBinarySearchTree().constructTree(weights);
        MainFrame application = Archaeopteryx.createApplication(searchTree);

        List<GraphEdge> edges = graph.getEdges();
        PhylogenyNode previousLeftNode = new PhylogenyNode();
        PhylogenyNode previousSearchTreeNode = new PhylogenyNode();
        List<PhylogenyNode> previousAncestors = new ArrayList<>();
        for (int i = edges.size()-1; i >= 0; i--) {
            GraphEdge edge = edges.get(i);
            PhylogenyNode leftNode = edge.getLeft();
            PhylogenyNode rightNode = edge.getRight();
            int rightNodeIndex = getGraphNodeData(rightNode).getIndex();

            // find node in search tree and get ancestors
            List<PhylogenyNode> ancestors = new ArrayList<>();
            PhylogenyNode currentSearchTreeNode;
            if(leftNode == previousLeftNode) {
                currentSearchTreeNode = previousSearchTreeNode;
                ancestors = previousAncestors;

                while (rightNodeIndex < getSearchTreeNodeData(currentSearchTreeNode).getLowIndex()){
                    currentSearchTreeNode = currentSearchTreeNode.getParent();
                    ancestors.remove(ancestors.size()-1);
                }
                currentSearchTreeNode = currentSearchTreeNode.getChildNode1();
            }
            else
                currentSearchTreeNode = searchTree.getRoot();
            while (true){
                ancestors.add(currentSearchTreeNode);
                if(currentSearchTreeNode.isExternal()) break;
                SearchTreeNodeData searchTreeNodeData = getSearchTreeNodeData(currentSearchTreeNode);
                if (rightNodeIndex < searchTreeNodeData.getIndex())
                    currentSearchTreeNode = currentSearchTreeNode.getChildNode1();
                else currentSearchTreeNode = currentSearchTreeNode.getChildNode2();
            }

            processSingleWhiteEdgeFromNode(ancestors, edge);
//            System.out.println();
            processSingleRedEdgeFromNode(ancestors, edge);
//            System.out.println();
            processSingleGreenEdgeFromNode(ancestors, edge);
//            System.out.println();

            previousLeftNode = leftNode;
            previousSearchTreeNode = currentSearchTreeNode;
            previousAncestors = ancestors;
        }
        application.dispose();
    }
    private double[] setIndexNumbersAndGetWeights(Graph graph, List<PhylogenyNode> rightSet) {
        double[] weights = new double[rightSet.size()];

        // Set index numbers and weights
        int rightSetSubtreeSize = rightSet.get(0).getNumberOfExternalNodes();
        for (int j = 0; j < rightSet.size(); j++) {
            PhylogenyNode currentNode = rightSet.get(j);
            GraphNodeData graphNodeData = getGraphNodeData(currentNode);
            graphNodeData.setIndex(j);

            int nj;
            if(currentNode.isExternal()){
                nj = 1;
            }
            else {
                PhylogenyNode firstChild = currentNode.getChildNode1();
                PhylogenyNode secondChild = currentNode.getChildNode2();
                if(firstChild == rightSet.get(j+1)){
                    nj = secondChild.getNumberOfExternalNodes();
                }
                else nj = firstChild.getNumberOfExternalNodes();
            }
            if(graphNodeData.hasNonSingletonEdge()){
                weights[j] = nj + (double)rightSetSubtreeSize/graph.getNsav();
            }
            else weights[j] = nj;

        }
        return weights;
    }
    private GraphNodeData getGraphNodeData(PhylogenyNode node){
        return ((NodeDataReference) node.getNodeData().getReference()).getGraphNodeData();
    }
    private SearchTreeNodeData getSearchTreeNodeData(PhylogenyNode node){
        return (SearchTreeNodeData) node.getNodeData().getReference();
    }

    private List<PhylogenyNode> getFringe(List<PhylogenyNode> ancestors, boolean left) {
        List<PhylogenyNode> fringe = new ArrayList<>();
        for (int i = 0; i < ancestors.size(); i++) {
            PhylogenyNode currentNode = ancestors.get(i);
            if(currentNode.isExternal()) break;
            PhylogenyNode child = left ? currentNode.getChildNode1() : currentNode.getChildNode2();
            if(child != ancestors.get(i+1)) fringe.add(child);
        }
        return fringe;
    }

    // Compute LWAMs
    public void computeLWAMs(Graph graph, Phylogeny searchTree, AgreementMatching[][] lwams){
        List<PhylogenyNode> rightSet = graph.getRightSet();


        // Compute LWAM(T1, T2(v_j))
        Stack<NodeAndMaxGPair> stackItems = new Stack<>();
        PhylogenyNode root = searchTree.getRoot();
        GraphEdge rootG = getSearchTreeNodeData(root).getG();
        stackItems.push(new NodeAndMaxGPair(root.getChildNode1(), rootG));
        stackItems.push(new NodeAndMaxGPair(root.getChildNode2(), rootG));
        int nextLeafIndex = rightSet.size()-1;
        AgreementMatching heaviestMatchingSoFar = null;
        int heaviestMatchingWeightSoFar = 0;
        while (!stackItems.empty()) {
            NodeAndMaxGPair currentPair = stackItems.pop();
            PhylogenyNode currentNode = currentPair.getNode();
            GraphEdge currentMaxG = currentPair.getMaxG();

            SearchTreeNodeData currentNodeData = getSearchTreeNodeData(currentNode);
            GraphEdge currentG = currentNodeData.getG();
            int currentMaxGWeight = currentMaxG == null ? 0 : currentMaxG.getGreenWeight();
            if(currentG != null && currentG.getGreenWeight() > currentMaxGWeight)
                currentMaxG = currentG;

            if(currentNode.isExternal()){
                AgreementMatching currentM = currentNodeData.getM();
                int currentMWeight = currentM == null ? 0 : currentM.getWeight();
                ProperCrossing currentY = currentNodeData.getY();
                int currentYWeight = currentY == null ? 0 : currentY.getWeight();
                GraphEdge currentR = currentNodeData.getR();
                int currentRedWeight = currentR == null ? 0 : currentR.getRedWeight();
                currentMaxGWeight = currentMaxG == null ? 0 : currentMaxG.getGreenWeight();
                int currentGRWeight = currentMaxGWeight + currentRedWeight;
                ProperCrossing currentX = currentNodeData.getX();
                int currentXWeight = currentX.getWeight();

                if(currentMWeight > heaviestMatchingWeightSoFar){
                    heaviestMatchingSoFar = currentM;
                    heaviestMatchingWeightSoFar = currentMWeight;
                }
                if(currentYWeight > heaviestMatchingWeightSoFar){
                    heaviestMatchingSoFar = new AgreementMatching(currentY, null, currentYWeight);
                    heaviestMatchingWeightSoFar = currentYWeight;
                }
                if(currentGRWeight > heaviestMatchingWeightSoFar){
                    heaviestMatchingSoFar = new AgreementMatching(new ProperCrossing(currentMaxG, currentR), null, currentGRWeight);
                    heaviestMatchingWeightSoFar = currentGRWeight;
                }
                if(currentXWeight > heaviestMatchingWeightSoFar){
                    heaviestMatchingSoFar = new AgreementMatching(currentX, null, currentXWeight);
                    heaviestMatchingWeightSoFar = currentXWeight;
                }

                MASTNodeData rightSetNodeData = getMASTNodeDataFromNode(rightSet.get(nextLeafIndex));
                rightSetNodeData.setSubtreeLWAM(new Pair<>(heaviestMatchingSoFar, lwams));
                rightSetNodeData.setSubtreeMASTSize(heaviestMatchingWeightSoFar);
                nextLeafIndex--;
            }
            else {
                stackItems.push(new NodeAndMaxGPair(currentNode.getChildNode1(), currentMaxG));
                stackItems.push(new NodeAndMaxGPair(currentNode.getChildNode2(), currentMaxG));
            }
        }
    }
    private class NodeAndMaxGPair {
        private final PhylogenyNode node;
        private final GraphEdge maxG;

        public NodeAndMaxGPair(PhylogenyNode node, GraphEdge maxG){
            this.node = node;
            this.maxG = maxG;
        }

        public PhylogenyNode getNode() {
            return node;
        }

        public GraphEdge getMaxG() {
            return maxG;
        }
    }

    private TreeAndSizePair createMASTFromMatching(AgreementMatching matching, AgreementMatching[][] lwams){
        WhiteEdgeSequence whiteEdges = matching.getWhiteEdges();
        Phylogeny resultTree = new Phylogeny();
        int resultSize = 0;
        PhylogenyNode u_i = new PhylogenyNode();
        resultTree.setRoot(u_i);
        WhiteEdgeSequence currentWhiteEdges = whiteEdges;
        while (currentWhiteEdges != null) {
            GraphEdge currentEdge = currentWhiteEdges.getFirstEdge();
            PhylogenyNode rightNode = currentEdge.getRight();
            PhylogenyNode mapNode = currentEdge.getMapNode();
            MASTNodeData mapNodeData = getMASTNodeDataFromNode(mapNode);

            Phylogeny subtreeMAST;
            int subtreeMASTSize;
            if(mapNodeData.getT2Node() != rightNode){ // map(i,j) != v_j
                Pair<AgreementMatching, AgreementMatching[][]> lwamPair = mapNodeData.getSubtreeLWAM();
                subtreeMAST = createMASTFromMatching(lwamPair.getLeft(), lwamPair.getRight()).getTree();
                subtreeMASTSize = mapNodeData.getSubtreeMASTSize();
            }
            else {
                PhylogenyNode mapNodeFirstChild = mapNode.getChildNode1();
                PhylogenyNode mapNodeSecondChild = mapNode.getChildNode2();
                PhylogenyNode rightNodeFirstChild = rightNode.getChildNode1();

                // child is not on the same path as rightNode, i.e. root of N_j
                if (rightNodeFirstChild.getLink() != rightNode.getLink()) {
                    MASTNodeData mapNodeFirstChildData = getMASTNodeDataFromNode(mapNodeFirstChild);
                    Pair<AgreementMatching, AgreementMatching[][]> lwamPair = mapNodeFirstChildData.getSubtreeLWAM();
                    subtreeMAST = createMASTFromMatching(lwamPair.getLeft(), lwamPair.getRight()).getTree();
                    subtreeMASTSize = mapNodeFirstChildData.getSubtreeMASTSize();
                } else {
                    MASTNodeData mapNodeSecondChildData = getMASTNodeDataFromNode(mapNodeSecondChild);
                    Pair<AgreementMatching, AgreementMatching[][]> lwamPair = mapNodeSecondChildData.getSubtreeLWAM();
                    subtreeMAST = createMASTFromMatching(lwamPair.getLeft(), lwamPair.getRight()).getTree();
                    subtreeMASTSize = mapNodeSecondChildData.getSubtreeMASTSize();
                }
            }
            PhylogenyNode u_iLeftChild = new PhylogenyNode();
            u_i.setChild1(u_iLeftChild);
            u_i.setChild2(subtreeMAST.getRoot());
            u_i = u_iLeftChild;
            resultSize += subtreeMASTSize;
            currentWhiteEdges = currentWhiteEdges.getPreviousEdge();
        }

        ProperCrossing properCrossing = matching.getProperCrossing();
        GraphEdge greenEdge = properCrossing.getGreenEdge();
        TreeAndSizePair greenEdgeSubtree = null;
        if(greenEdge != null){
            PhylogenyNode leftNode = greenEdge.getLeft();
            if(leftNode.isExternal()){
                greenEdgeSubtree = new TreeAndSizePair(createTreeWithOneNode(leftNode.getName()), 1);
            }
            else {
                PhylogenyNode mapNode = greenEdge.getMapNode();
                MASTNodeData mapNodeData = getMASTNodeDataFromNode(mapNode);
                Pair<AgreementMatching, AgreementMatching[][]> lwamPair = mapNodeData.getSubtreeLWAM();
                greenEdgeSubtree = new TreeAndSizePair(createMASTFromMatching(lwamPair.getLeft(), lwamPair.getRight()).getTree(), mapNodeData.getSubtreeMASTSize());
            }
        }
        GraphEdge redEdge = properCrossing.getRedEdge();
        TreeAndSizePair redEdgeSubtree = null;
        if(redEdge != null){
            int leftNodePathNumber = getMASTNodeDataFromNode(redEdge.getLeft()).getPathNumber();
            PhylogenyNode rightNode = redEdge.getRight();
            PhylogenyNode n_j;
            int n_jPathNumber;
            int rightNodePathNumber = getMASTNodeDataFromNode(rightNode.getLink()).getPathNumber();
            if(rightNode.isExternal()){
                n_j = rightNode;
                n_jPathNumber = rightNodePathNumber;
            }
            else {
                PhylogenyNode leftChild = rightNode.getChildNode1();
                PhylogenyNode rightChild = rightNode.getChildNode2();
                PhylogenyNode leftChildStartOfPath = leftChild.getLink();
                PhylogenyNode rightChildStartOfPath = rightChild.getLink();

                int leftChildPathNumber = leftChildStartOfPath == null ? -1 : getMASTNodeDataFromNode(leftChildStartOfPath).getPathNumber();
                int rightChildPathNumber = rightChildStartOfPath == null ? -1 : getMASTNodeDataFromNode(rightChildStartOfPath).getPathNumber();
                if(leftChildPathNumber != rightNodePathNumber){
                    n_j = leftChild;
                    n_jPathNumber = leftChildPathNumber;
                }
                else {
                    n_j = rightChild;
                    n_jPathNumber = rightChildPathNumber;
                }
            }
            if(n_jPathNumber == -1 || rightNode.isExternal()){ // Child was not on any path
                redEdgeSubtree = new TreeAndSizePair(createTreeWithOneNode(n_j.getName()), 1);
            }
            else {
                TreeAndSizePair redSubtree = createMASTFromMatching(lwams[leftNodePathNumber][n_jPathNumber], lwams);
                redEdgeSubtree = new TreeAndSizePair(redSubtree.getTree(), redSubtree.getSize());
            }
        }
        else {
            if(u_i.isRoot())
                return greenEdgeSubtree;

            u_i.getParent().setChild1(greenEdgeSubtree.getTree().getRoot());
            resultSize += greenEdgeSubtree.getSize();
            return new TreeAndSizePair(resultTree, resultSize);
        }
        if(greenEdge == null){
            if(u_i.isRoot())
                return redEdgeSubtree;

            u_i.getParent().setChild1(redEdgeSubtree.getTree().getRoot());
            resultSize += redEdgeSubtree.getSize();
            return new TreeAndSizePair(resultTree, resultSize);
        }
        else {
            u_i.setChild1(redEdgeSubtree.getTree().getRoot());
            resultSize += redEdgeSubtree.getSize();
            u_i.setChild2(greenEdgeSubtree.getTree().getRoot());
            resultSize += greenEdgeSubtree.getSize();
        }
        return new TreeAndSizePair(resultTree, resultSize);
    }

    // Helper methods
    private MASTNodeData getMASTNodeDataFromNode(PhylogenyNode node){
        return ((NodeDataReference) node.getNodeData().getReference()).getMastNodeData();
    }
    private Phylogeny createTreeWithOneNode(String name){
        PhylogenyNode node = new PhylogenyNode();
        node.setName(name);
        Phylogeny tree = new Phylogeny();
        tree.setRoot(node);
        return tree;
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
    public class TreeAndSizePair {
        private final Phylogeny tree;
        private final int size;

        public TreeAndSizePair(Phylogeny tree, int size){
            this.tree = tree;
            this.size = size;
        }

        public Phylogeny getTree() {
            return tree;
        }

        public int getSize() {
            return size;
        }
    }
    private class LWAMAndLWAMsPair {
        private final AgreementMatching lwam;
        private final AgreementMatching[][] lwams;

        public LWAMAndLWAMsPair(AgreementMatching lwam, AgreementMatching[][] lwams){
            this.lwam = lwam;
            this.lwams = lwams;
        }

        public AgreementMatching getLwam() {
            return lwam;
        }

        public AgreementMatching[][] getLwams() {
            return lwams;
        }
    }
    private class DataForCalculatingLWAM {
        private final List<PhylogenyNode> tree1Decomposition;
        private final List<List<PhylogenyNode>> tree2Decomposition;
        private final List<Phylogeny> siSubtrees;

        public DataForCalculatingLWAM(List<PhylogenyNode> tree1Decomposition, List<List<PhylogenyNode>> tree2Decomposition, List<Phylogeny> siSubtrees){
            this.tree1Decomposition = tree1Decomposition;
            this.tree2Decomposition = tree2Decomposition;
            this.siSubtrees = siSubtrees;
        }

        public List<PhylogenyNode> getTree1Decomposition() {
            return tree1Decomposition;
        }

        public List<List<PhylogenyNode>> getTree2Decomposition() {
            return tree2Decomposition;
        }

        public List<Phylogeny> getSiSubtrees() {
            return siSubtrees;
        }
    }
    private void updateParentReferences(Phylogeny tree) {
        PhylogenyNodeIterator phylogenyNodeIterator = tree.iteratorPreorder();
        while (phylogenyNodeIterator.hasNext()) {
            PhylogenyNode next = phylogenyNodeIterator.next();
            List<PhylogenyNode> allDescendants = next.getAllDescendants();
            if (allDescendants != null) allDescendants.forEach(child -> child.setParent(next));
        }
    }
}
