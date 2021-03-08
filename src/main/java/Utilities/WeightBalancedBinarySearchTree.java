package Utilities;

import Utilities.DataObjects.SearchTreeNodeData;
import org.forester.archaeopteryx.Archaeopteryx;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;

import java.util.Stack;

/**
 * Created by Thomas on 18-03-2016.
 */
public class WeightBalancedBinarySearchTree {
    private int numberOfCallsToFindIndexOfCut = 0;

    public static void main(String[] args) {
        double[] a = new double[18];
        a[0] = 1;
        a[1] = 1;
        a[2] = 1;
        a[3] = 1;
        a[4] = 1;
        a[5] = 1;
        a[6] = 1;
        a[7] = 1;
        a[8] = 1;
        a[9] = 1;
        a[10] = 1;
        a[11] = 1;
        a[12] = 1;
        a[13] = 1;
        a[14] = 1;
        a[15] = 1;
        a[16] = 1;
        a[17] = 1;

        WeightBalancedBinarySearchTree weightBalancedBinarySearchTree = new WeightBalancedBinarySearchTree();
        Phylogeny tree = weightBalancedBinarySearchTree.constructTree(a);
        Archaeopteryx.createApplication(tree);
    }

    private static void findRootTest() {
        double[] a = new double[5];
        a[0] = 1;
        a[1] = 3;
        a[2] = 2;

        a[3] = 3;
        a[4] = 3;

        WeightBalancedBinarySearchTree weightBalancedBinarySearchTree = new WeightBalancedBinarySearchTree();
        double[] l = weightBalancedBinarySearchTree.computeLFromWeights(a);
        double[] r = weightBalancedBinarySearchTree.computeRFromWeights(a);
        System.out.println(weightBalancedBinarySearchTree.findRootIndex(l, r, 0, 4));
    }

    private static void findRootBinarySearchTest() {
        double[] a = new double[4];
        a[0] = 1;
        a[1] = 3;
        a[2] = 2;
        a[3] = 3;

        WeightBalancedBinarySearchTree weightBalancedBinarySearchTree = new WeightBalancedBinarySearchTree();
        double[] l = weightBalancedBinarySearchTree.computeLFromWeights(a);
        double[] r = weightBalancedBinarySearchTree.computeRFromWeights(a);
        System.out.println(weightBalancedBinarySearchTree.findRootIndexBinarySearch(l, r, 0, 4));
    }

    public Phylogeny constructTree(double[] weights){
        int treeSize = weights.length;
        Phylogeny tree = new Phylogeny();
        double[] l = computeLFromWeights(weights);
        double[] r = computeRFromWeights(weights);

        Stack<StackItem> stack = new Stack<>();
        stack.push(new StackItem(0, weights.length-1, null, true));

        while(!stack.isEmpty()){
            StackItem stackItem = stack.pop();
            int i = stackItem.getFrom();
            int j = stackItem.getTo();
            PhylogenyNode parentNode = stackItem.getParentNode();
            boolean isLeftChild = stackItem.isLeftChild();

            if(j == i + 1){
                PhylogenyNode newNode = createAndAddNode(j, j + "", parentNode, isLeftChild, treeSize);
                createAndAddNode(i, i + ": " + weights[i], newNode, true, treeSize); // left child
                createAndAddNode(j, j + ": " + weights[j], newNode, false, treeSize); // right child
                if(parentNode == null){
                    tree.setRoot(newNode);
                }
                continue;
            }

            int indexOfCut = findRootIndex(l, r, i, j);

            PhylogenyNode newNode = createAndAddNode(indexOfCut, indexOfCut + "", parentNode, isLeftChild, treeSize);
            PhylogenyNode dummyNode = new PhylogenyNode();
            newNode.setChild1(dummyNode);
            newNode.setChild2(dummyNode);
            if(parentNode == null){
                tree.setRoot(newNode);
            }

            if(indexOfCut != i+1){
                int leftFrom = i;
                int leftTo = indexOfCut - 1;
                stack.push(new StackItem(leftFrom, leftTo, newNode, true));
            }
            else {
                createAndAddNode(i, i + ": " + weights[i], newNode, true, treeSize); // left child
            }

            if(indexOfCut != j){
                int rightFrom = indexOfCut;
                int rightTo = j;
                stack.push(new StackItem(rightFrom, rightTo, newNode, false));
            }
            else {
                createAndAddNode(j, j + ": " + weights[j], newNode, false, treeSize); // right child
            }
        }
        return tree;
    }

    private PhylogenyNode createAndAddNode(int index, String name, PhylogenyNode parent, boolean isLeftChild, int treeSize){
        PhylogenyNode newNode = new PhylogenyNode();
        int lowIndex, maxIndex;
        if(parent == null){
            lowIndex = 0;
            maxIndex = treeSize-1;
        }
        else {
            SearchTreeNodeData parentData = (SearchTreeNodeData) parent.getNodeData().getReference();
            if(isLeftChild) {
                parent.setChild1(newNode);
                lowIndex = parentData.getLowIndex();
                maxIndex = parentData.getIndex() - 1;
            }
            else {
                parent.setChild2(newNode);
                lowIndex = parentData.getIndex();
                maxIndex = parentData.getMaxIndex();
            }

        }
        newNode.getNodeData().addReference(new SearchTreeNodeData(index, lowIndex, maxIndex));
        newNode.setName(name);
        return newNode;
    }

    private double[] computeLFromWeights(double[] weights){
        double[] result = new double[weights.length + 1];

        result[0] = 0;
        for (int i = 1; i < result.length; i++) {
            result[i] = result[i-1] + weights[i-1];
        }
        return result;
    }

    private double[] computeRFromWeights(double[] weights){
        double[] result = new double[weights.length + 1];

        result[result.length-1] = 0;
        for (int i = result.length-2; i >= 0; i--) {
            result[i] = result[i+1] + weights[i];
        }
        return result;
    }

    public int findRootIndex(double[] l, double[] r, int i, int j){
        numberOfCallsToFindIndexOfCut++;
        if(j == i + 1 || j == i) return j;

        int middleIndex = i + (j - i + 1) / 2;
        double lMiddleIndex = l[middleIndex] - l[i];
        double rMiddleIndex = r[middleIndex] - r[j + 1];
        if(lMiddleIndex == rMiddleIndex) return middleIndex;

        int from,to;
        boolean fromLeft;
        if(lMiddleIndex < rMiddleIndex){
            from = middleIndex + 1;
            to = j;
            fromLeft = false;
        }
        else {
            from = i;
            to = middleIndex - 1;
            fromLeft = true;
        }

        int k = 0;
        int kIndex = fromLeft ? from + 1 : to;
        int previousKIndex = fromLeft ? from + 1 : to;
        while(true){
            if(kIndex > to){
                kIndex = to;
                break;
            }
            if(kIndex < from){
                kIndex = from;
                break;
            }

            double lKIndex = l[kIndex] - l[i];
            double rKIndex = r[kIndex] - r[j+1];
            if(lKIndex == rKIndex) return kIndex;

            if(fromLeft && lKIndex > rKIndex) break;
            if(!fromLeft && lKIndex < rKIndex) break;

            k = (k + 1)*2 - 1;
            previousKIndex = kIndex;
            kIndex = fromLeft ? from + k + 1 : to - k;
        }

        if(fromLeft) return findRootIndexBinarySearch(l, r, previousKIndex, kIndex);
        else return findRootIndexBinarySearch(l, r, kIndex, previousKIndex);
    }

    private int findRootIndexBinarySearch(double[] l, double[] r, int fromIndex, int toIndex) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            double midLVal = l[mid];
            double midRVal = r[mid];

            if (midLVal < midRVal)
                low = mid + 1;
            else if (midLVal > midRVal)
                high = mid - 1;
            else
                return mid; // root found
        }
        return low;
    }


    private static void findIndexOfCutTest() {
        int[] a = new int[5];
        a[0] = 0;
        a[1] = 1;
        a[2] = 4;
        a[3] = 6;
        a[4] = 9;
        WeightBalancedBinarySearchTree weightBalancedBinarySearchTree = new WeightBalancedBinarySearchTree();
        System.out.println(weightBalancedBinarySearchTree.findIndexOfCut(a, 1, 0, 4));
    }

    public void tester(int[] weights, int cut, int from, int to){
        if(to == from + 1 || to == from) return;

        int indexOfCut = binarySearch(weights, cut, from, to);
        if(indexOfCut != from){
            int leftFrom = from;
            int leftTo = indexOfCut - 1;
//            int leftCut =  leftFrom + (((leftTo - leftFrom) + 1) / 2 - 1);
            int leftCut = leftFrom + 1;
            tester(weights, leftCut, leftFrom, leftTo);
        }

        if(indexOfCut != to){
            int rightFrom = indexOfCut + 1;
            int rightTo = to;
//            int rightCut =  rightFrom + (((rightTo - rightFrom) + 1) / 2 - 1);
            int rightCut = rightFrom + 1;
            tester(weights, rightCut, rightFrom, rightTo);
        }

    }

    public void testerIterative(int[] weights, int firstCut, int from, int to){

        Stack<IntegerTriplet> stack = new Stack<>();
        stack.push(new IntegerTriplet(firstCut, from, to));

        while(!stack.isEmpty()){
            IntegerTriplet triplet = stack.pop();
            int cut = triplet.getFirst();
            int i = triplet.getSecond();
            int j = triplet.getThird();

            if(j == i + 1 || j == i) continue;

            int indexOfCut = findIndexOfCut(weights, cut, i, j);
            if(indexOfCut != i+1){
                int leftFrom = i;
                int leftTo = indexOfCut - 1;
                int leftCut =  leftFrom + (((leftTo - leftFrom) + 1) / 2 - 1);
//                int leftCut = leftFrom;
                stack.push(new IntegerTriplet(leftCut, leftFrom, leftTo));
            }

            if(indexOfCut != j){
                int rightFrom = indexOfCut;
                int rightTo = j;
                int rightCut =  rightFrom + (((rightTo - rightFrom) + 1) / 2 - 1);
//                int rightCut = rightFrom;
                stack.push(new IntegerTriplet(rightCut, rightFrom, rightTo));
            }
        }
    }

    public int findIndexOfCut(int[] weights, int cut, int i, int j){
        numberOfCallsToFindIndexOfCut++;
        if(j == i + 1 || j == i) return j;

        int middleIndex = i + (j - i + 1) / 2;
        if(weights[middleIndex] == cut) return middleIndex;

        int from,to;
        boolean fromLeft;
        if(weights[middleIndex] < cut){
            from = middleIndex + 1;
            to = j;
            fromLeft = false;
        }
        else {
            from = i;
            to = middleIndex - 1;
            fromLeft = true;
        }

        int k = 0;
        int kIndex = fromLeft ? from + 1 : to;
        int previousKIndex = fromLeft ? from + 1 : to;
        while(true){
            if(kIndex > to){
                kIndex = to;
                break;
            }
            if(kIndex < from){
                kIndex = from;
                break;
            }

            if(weights[kIndex] == cut) return kIndex;

            if(fromLeft && weights[kIndex] > cut) break;
            if(!fromLeft && weights[kIndex] < cut) break;

            k = (k + 1)*2 - 1;
            previousKIndex = kIndex;
            kIndex = fromLeft ? from + k + 1 : to - k;
        }

        if(fromLeft) return binarySearch(weights, cut, previousKIndex, kIndex);
        else return binarySearch(weights, cut, kIndex, previousKIndex);
    }

    private int binarySearch(int[] a, int key, int fromIndex, int toIndex) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            int midVal = a[mid];

            if (midVal < key)
                low = mid + 1;
            else if (midVal > key)
                high = mid - 1;
            else
                return mid; // key found
        }
        return low;  // key not found.
    }

    public int getNumberOfCallsToFindIndexOfCut() {
        return numberOfCallsToFindIndexOfCut;
    }

    private class IntegerTriplet {
        private int first;
        private int second;
        private int third;

        public IntegerTriplet(int first, int second, int third){
            this.first = first;
            this.second = second;
            this.third = third;
        }

        public int getFirst() {
            return first;
        }

        public void setFirst(int first) {
            this.first = first;
        }

        public int getSecond() {
            return second;
        }

        public void setSecond(int second) {
            this.second = second;
        }

        public int getThird() {
            return third;
        }

        public void setThird(int third) {
            this.third = third;
        }
    }

    private class StackItem {
        private int from;
        private int to;
        private final PhylogenyNode parentNode;
        private final boolean isLeftChild;

        public StackItem(int first, int second, PhylogenyNode parentNode, boolean isLeftChild){
            this.from = first;
            this.to = second;
            this.parentNode = parentNode;
            this.isLeftChild = isLeftChild;
        }

        public int getFrom() {
            return from;
        }

        public void setFrom(int from) {
            this.from = from;
        }

        public int getTo() {
            return to;
        }

        public void setTo(int to) {
            this.to = to;
        }

        public PhylogenyNode getParentNode() {
            return parentNode;
        }

        public boolean isLeftChild() {
            return isLeftChild;
        }
    }
}
