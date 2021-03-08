import Utilities.ConstantTimeLCA;
import Utilities.DataObjects.NodeDataReference;
import Utilities.PhylogenyGenerator;
import nlogn.MAST;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

import java.util.Arrays;

/**
 * Created by Thomas on 09-03-2016.
 */
public class LCARuntimeTest {
    public static void main(String[] args) {
        testGCOnProprocess();
    }

    private static void testPreprocessRuntime() {
        initialRuns();

        System.out.println("Test:");
        for (int i = 100; i < 100000; i+= 100) {
            Phylogeny tree = PhylogenyGenerator.generateRandomTree(i, true);
            addNodeDataReferences(tree);
            long[] runtimes = new long[5];
            runtimes[0] = getTimePreprocessing(tree);
            runtimes[1] = getTimePreprocessing(tree);
            runtimes[2] = getTimePreprocessing(tree);
            runtimes[3] = getTimePreprocessing(tree);
            runtimes[4] = getTimePreprocessing(tree);
            System.out.println(i + "\t" + median(runtimes)/i);
        }
    }

    private static void testGCOnProprocess() {
        initialRuns();

        System.out.println("Test:");
        GCMonitor gcMonitor = new GCMonitor();
        for (int i = 100; i < 100000; i+= 100) {
            Phylogeny tree = PhylogenyGenerator.generateRandomTree(i, true);
            addNodeDataReferences(tree);
            long[] runtimes = new long[5];
            runtimes[0] = timeGCPreprocess(tree, gcMonitor);
            runtimes[1] = timeGCPreprocess(tree, gcMonitor);
            runtimes[2] = timeGCPreprocess(tree, gcMonitor);
            runtimes[3] = timeGCPreprocess(tree, gcMonitor);
            runtimes[4] = timeGCPreprocess(tree, gcMonitor);
            System.out.println(i + "\t" + median(runtimes));
        }
    }

    private static void testRuntime() {
        initialRuns();

        System.out.println("Test:");
        for (int i = 100; i < 100000; i+= 100) {
            Phylogeny tree = PhylogenyGenerator.generateRandomTree(i, true);
            addNodeDataReferences(tree);
            ConstantTimeLCA lcaFinder = new ConstantTimeLCA(tree);
            PhylogenyNode node1 = tree.getNode("0");
            PhylogenyNode node2 = tree.getNode((i-1) + "");


//            PhylogenyNode node1test = tree.getNode("10");
//            PhylogenyNode node2test = tree.getNode("11");
//            timeGetLCA(lcaFinder, node1, node2);
//            timeGetLCA(lcaFinder, node1test, node2test);
            System.out.println(i + "\t" + timeGetLCA(lcaFinder, node1, node2));
//            long[] runtimes = new long[5];
//            runtimes[0] = timeGetLCA(lcaFinder, node1, node2);
//            runtimes[1] = timeGetLCA(lcaFinder, node1, node2);
//            runtimes[2] = timeGetLCA(lcaFinder, node1, node2);
//            runtimes[3] = timeGetLCA(lcaFinder, node1, node2);
//            runtimes[4] = timeGetLCA(lcaFinder, node1, node2);
//
//            System.out.println(i + "\t" + median(runtimes));
        }
    }

    private static long timeGetLCA(ConstantTimeLCA lcaFinder, PhylogenyNode node1, PhylogenyNode node2){
        long time = System.nanoTime();
        lcaFinder.getLCA(node1, node2);
        return System.nanoTime() - time;
    }

    private static long getTimePreprocessing(Phylogeny tree){
        long time = System.nanoTime();
        new ConstantTimeLCA(tree);
        return System.nanoTime() - time;
    }

    private static long timeGCPreprocess(Phylogeny tree, GCMonitor gcMonitor) {
        new ConstantTimeLCA(tree);
        return gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement();
    }

    private static void addNodeDataReferences(Phylogeny tree) {
        PhylogenyNodeIterator iterator = tree.iteratorPostorder();
        while (iterator.hasNext()){
            PhylogenyNode currentNode = iterator.next();
            currentNode.getNodeData().addReference(new NodeDataReference());
        }
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
