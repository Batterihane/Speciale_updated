import Utilities.DataObjects.MASTNodeData;
import Utilities.DataObjects.NodeDataReference;
import Utilities.PhylogenyGenerator;
import Utilities.SubtreeProcessor;
import nlogn.MAST;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Thomas on 11-03-2016.
 */
public class SubtreeProcessorRuntimeTest {

    public static void main(String[] args) {
//        testPreprocessing(100000);
//        testInduceSubtreeConstantNumberOfLeaves(100000, 1000);
        testInduceSubtreeConstantTreeSize(100000);
    }

    private static void testInduceSubtreeConstantNumberOfLeaves(int maxTreeSize, int numberOfLeaves) {
        initialRuns();

        System.out.println("Test:");
        for (int i = 1000; i <= maxTreeSize; i+= 100) {
            Phylogeny tree = PhylogenyGenerator.generateRandomTree(i, true);
            addNodeDataReferences(tree);


            List<PhylogenyNode> allLeaves = tree.getRoot().getAllExternalDescendants();
            List<PhylogenyNode> leavesForTest = new ArrayList<>();
            Random random = new Random();
            for (int j = 0 ; j < numberOfLeaves ; j++){
                int index = random.nextInt(allLeaves.size());
                leavesForTest.add(allLeaves.get(index));
                allLeaves.remove(index);
            }

            SubtreeProcessor subtreeProcessor = new SubtreeProcessor(tree);
//            for (PhylogenyNode node : leaves){
//                List<PhylogenyNode> leaf = new ArrayList<>();
//                leaf.add(node);
//                subtreeProcessor.induceSubtree(leaf);
//            }
            long[] runtimes = new long[5];
            runtimes[0] = timeInduceSubtree(subtreeProcessor, leavesForTest);
            runtimes[1] = timeInduceSubtree(subtreeProcessor, leavesForTest);
            runtimes[2] = timeInduceSubtree(subtreeProcessor, leavesForTest);
            runtimes[3] = timeInduceSubtree(subtreeProcessor, leavesForTest);
            runtimes[4] = timeInduceSubtree(subtreeProcessor, leavesForTest);
            System.out.println(i + "\t" + median(runtimes));
        }
    }

    private static void testInduceSubtreeConstantTreeSize(int treeSize){
        initialRuns();

        System.out.println("Test:");
        Phylogeny tree = PhylogenyGenerator.generateRandomTree(treeSize, true);
        addNodeDataReferences(tree);
        SubtreeProcessor subtreeProcessor = new SubtreeProcessor(tree);

        List<PhylogenyNode> allLeaves = tree.getRoot().getAllExternalDescendants();
        List<PhylogenyNode> leavesForTest = new ArrayList<>();
        Random random = new Random();
        for (int i = 100; i <= treeSize; i+= 100) {
            for (int j = 0 ; j < 100 ; j++){
                int index = random.nextInt(allLeaves.size());
                leavesForTest.add(allLeaves.get(index));
                allLeaves.remove(index);
            }

            long[] runtimes = new long[5];
            runtimes[0] = timeInduceSubtree(subtreeProcessor, leavesForTest);
            runtimes[1] = timeInduceSubtree(subtreeProcessor, leavesForTest);
            runtimes[2] = timeInduceSubtree(subtreeProcessor, leavesForTest);
            runtimes[3] = timeInduceSubtree(subtreeProcessor, leavesForTest);
            runtimes[4] = timeInduceSubtree(subtreeProcessor, leavesForTest);
            System.out.println(i + "\t" + median(runtimes)/i);
        }
    }

    private static void testPreprocessing(int maxTreeSize){
        initialRuns();

        System.out.println("Test:");
        for (int i = 100; i <= maxTreeSize; i+= 100) {
            Phylogeny tree = PhylogenyGenerator.generateRandomTree(i, true);
            addNodeDataReferences(tree);

            long[] runtimes = new long[5];
            runtimes[0] = timePreprocessing(tree);
            runtimes[1] = timePreprocessing(tree);
            runtimes[2] = timePreprocessing(tree);
            runtimes[3] = timePreprocessing(tree);
            runtimes[4] = timePreprocessing(tree);
            System.out.println(i + "\t" + median(runtimes)/i);
        }
    }

    private static void addNodeDataReferences(Phylogeny tree) {
        PhylogenyNodeIterator iterator = tree.iteratorPostorder();
        while (iterator.hasNext()){
            PhylogenyNode currentNode = iterator.next();
            NodeDataReference nodeDataReference = new NodeDataReference();
            MASTNodeData mastNodeData = new MASTNodeData();
            nodeDataReference.setMastNodeData(mastNodeData);
            currentNode.getNodeData().addReference(nodeDataReference);

        }
    }

    private static long timeInduceSubtree(SubtreeProcessor subtreeProcessor, List<PhylogenyNode> leaves){
        long time = System.nanoTime();
        subtreeProcessor.induceSubtree(leaves);
        return System.nanoTime() - time;
    }

    private static long timePreprocessing(Phylogeny tree){
        long time = System.nanoTime();
        new SubtreeProcessor(tree);
        return System.nanoTime() - time;
    }

    private static void initialRuns() {
        System.out.println("Initial:");
        for (int i = 20; i >= 0; i--) {
            Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(1000, true);
            Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(1000, false);
            MAST mast = new MAST();
            mast.getMAST(tree1, tree2, false);
            System.out.println(i);
        }
    }

    private static long median(long[] numbers){
        Arrays.sort(numbers);
        return numbers[2];
    }

}
