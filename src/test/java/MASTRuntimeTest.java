import Utilities.DataObjects.MASTNodeData;
import Utilities.DataObjects.NodeDataReference;
import Utilities.ForesterNewickParser;
import Utilities.Pair;
import Utilities.PhylogenyGenerator;
import Utilities.PhylogenyParser;
import nlogn.MAST;
import org.forester.archaeopteryx.Archaeopteryx;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * Created by Thomas on 11-03-2016.
 */
public class MASTRuntimeTest {

    public static void main(String[] args) {
//        writeRandomTreesToNewick();
//        writeBestCaseTreesToNewick();
//        writeBestCaseTreesToNewick2();
//        writeCompleteTreesToNewick();
//        writeIdenticalCompleteTreesToNewick();

//        testNSquared(10000, "testTrees\\randomTrees\\");
//        testNSquared(10000, "testTrees\\bestCaseTrees2\\");
//        testNSquared(10000, "testTrees\\completeTrees\\");
//        testNSquared(10000, "testTrees\\completeTrees2\\"); // with backtrack

//        testNLogN(80000, false, "testTrees\\bestCaseTrees\\"); // identical
//        testNLogNIdenticalTrees(80000, false, "testTrees\\completeTrees\\");

//        testNaive(10000, "testTrees\\completeTrees2\\");
        testNSquaredVsNlogn();
//        runRandomTrees();


//        testNSquaredSingleRuns(10000, "testTrees\\completeTrees2\\");
//        testNLogN(10000, false, "testTrees\\completeTrees2\\");
//        testNLogN(80000, false, "testTrees\\randomTrees\\"); // with 1000GB and 2000GB allocated memory

//        testRandomTreesGCSubtracted(80000, false);
//        testPerfectTrees(80000, false);
//        testGCOnRandomTrees(80000, false);
//        testIdenticalBaseCaseTrees(80000, false);
//        testNonSimilarBaseCaseTrees(80000, false);
//        testPerfectTreesGCSubtracted(80000, false);
//        testNonSimilarBaseCaseTreesMLIS(80000);
    }


    private static void testNLogN(int maxSize, boolean recursive, String path) {
        initialRuns();

        System.out.println("Test:");
        GCMonitor gcMonitor = new GCMonitor();
        long currentRuntime;
        long currentGcTime;
        for (int i = 200; i <= maxSize; i+= 200) {
            long[] runtime = new long[5];
            long[] gctime = new long[5];
            long[] gcSubtractedTime = new long[5];
            Pair<Phylogeny, Phylogeny> trees = getTreesFromNewick(path, i);
            Phylogeny tree1 = trees.getLeft();
            Phylogeny tree2 = trees.getRight();

            currentRuntime = timeGetMAST(tree1, tree2, recursive);
            currentGcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtime[0] = currentRuntime;
            gctime[0] = currentGcTime;
            gcSubtractedTime[0] = currentRuntime - currentGcTime;

            currentRuntime = timeGetMAST(tree1, tree2, recursive);
            currentGcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtime[1] = currentRuntime;
            gctime[1] = currentGcTime;
            gcSubtractedTime[1] = currentRuntime - currentGcTime;

            currentRuntime = timeGetMAST(tree1, tree2, recursive);
            currentGcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtime[2] = currentRuntime;
            gctime[2] = currentGcTime;
            gcSubtractedTime[2] = currentRuntime - currentGcTime;

            currentRuntime = timeGetMAST(tree1, tree2, recursive);
            currentGcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtime[3] = currentRuntime;
            gctime[3] = currentGcTime;
            gcSubtractedTime[3] = currentRuntime - currentGcTime;

            currentRuntime = timeGetMAST(tree1, tree2, recursive);
            currentGcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtime[4] = currentRuntime;
            gctime[4] = currentGcTime;
            gcSubtractedTime[4] = currentRuntime - currentGcTime;

            long medianRuntime = median(runtime);
            long medianGctime = median(gctime);
            long medianGcSubtractedTime = median(gcSubtractedTime);
            System.out.println(i + "\t" + medianRuntime + "\t" + medianGctime + "\t" + medianGcSubtractedTime);
        }
    }

    private static void testNLogNIdenticalTrees(int maxSize, boolean recursive, String path) {
        initialRuns();

        System.out.println("Test:");
        GCMonitor gcMonitor = new GCMonitor();
        long currentRuntime;
        long currentGcTime;
        for (int i = 200; i <= maxSize; i+= 200) {
            long[] runtime = new long[5];
            long[] gctime = new long[5];
            long[] gcSubtractedTime = new long[5];
            Pair<Phylogeny, Phylogeny> trees = getTreesFromNewick(path, i);
            Phylogeny tree1 = trees.getRight();
            Phylogeny tree2 = copyTree(tree1);

            currentRuntime = timeGetMAST(tree1, tree2, recursive);
            currentGcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtime[0] = currentRuntime;
            gctime[0] = currentGcTime;
            gcSubtractedTime[0] = currentRuntime - currentGcTime;

            currentRuntime = timeGetMAST(tree1, tree2, recursive);
            currentGcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtime[1] = currentRuntime;
            gctime[1] = currentGcTime;
            gcSubtractedTime[1] = currentRuntime - currentGcTime;

            currentRuntime = timeGetMAST(tree1, tree2, recursive);
            currentGcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtime[2] = currentRuntime;
            gctime[2] = currentGcTime;
            gcSubtractedTime[2] = currentRuntime - currentGcTime;

            currentRuntime = timeGetMAST(tree1, tree2, recursive);
            currentGcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtime[3] = currentRuntime;
            gctime[3] = currentGcTime;
            gcSubtractedTime[3] = currentRuntime - currentGcTime;

            currentRuntime = timeGetMAST(tree1, tree2, recursive);
            currentGcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtime[4] = currentRuntime;
            gctime[4] = currentGcTime;
            gcSubtractedTime[4] = currentRuntime - currentGcTime;

            long medianRuntime = median(runtime);
            long medianGctime = median(gctime);
            long medianGcSubtractedTime = median(gcSubtractedTime);
            System.out.println(i + "\t" + medianRuntime + "\t" + medianGctime + "\t" + medianGcSubtractedTime);
        }
    }

    private static void testNSquared(int maxSize, String path) {
        initialRunsNSquared();

        System.out.println("Test:");
        GCMonitor gcMonitor = new GCMonitor();
        long currentRuntime;
        long currentGcTime;
        for (int i = 50; i <= maxSize; i+= 50) {
            long[] runtime = new long[5];
            long[] gctime = new long[5];
            long[] gcSubtractedTime = new long[5];
            Pair<Phylogeny, Phylogeny> trees = getTreesFromNewick(path, i);
            Phylogeny tree1 = trees.getLeft();
            Phylogeny tree2 = trees.getRight();

            currentRuntime = timeGetMASTNSquared(tree1, tree2);
            currentGcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtime[0] = currentRuntime;
            gctime[0] = currentGcTime;
            gcSubtractedTime[0] = currentRuntime - currentGcTime;

            currentRuntime = timeGetMASTNSquared(tree1, tree2);
            currentGcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtime[1] = currentRuntime;
            gctime[1] = currentGcTime;
            gcSubtractedTime[1] = currentRuntime - currentGcTime;

            currentRuntime = timeGetMASTNSquared(tree1, tree2);
            currentGcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtime[2] = currentRuntime;
            gctime[2] = currentGcTime;
            gcSubtractedTime[2] = currentRuntime - currentGcTime;

            currentRuntime = timeGetMASTNSquared(tree1, tree2);
            currentGcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtime[3] = currentRuntime;
            gctime[3] = currentGcTime;
            gcSubtractedTime[3] = currentRuntime - currentGcTime;

            currentRuntime = timeGetMASTNSquared(tree1, tree2);
            currentGcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtime[4] = currentRuntime;
            gctime[4] = currentGcTime;
            gcSubtractedTime[4] = currentRuntime - currentGcTime;

            long medianRuntime = median(runtime);
            long medianGctime = median(gctime);
            long medianGcSubtractedTime = median(gcSubtractedTime);
            System.out.println(i + "\t" + medianRuntime + "\t" + medianGctime + "\t" + medianGcSubtractedTime);
        }
    }

    private static void testNaive(int maxSize, String path) {
        initialRunsNaive();

        System.out.println("Test:");
        GCMonitor gcMonitor = new GCMonitor();
        long currentRuntime;
        long currentGcTime;
        for (int i = 50; i <= maxSize; i+= 50) {
            long[] runtime = new long[5];
            long[] gctime = new long[5];
            long[] gcSubtractedTime = new long[5];
            Pair<Phylogeny, Phylogeny> trees = getTreesFromNewick(path, i);
            Phylogeny tree1 = trees.getLeft();
            Phylogeny tree2 = trees.getRight();

            currentRuntime = timeGetMASTNaive(tree1, tree2);
            currentGcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtime[0] = currentRuntime;
            gctime[0] = currentGcTime;
            gcSubtractedTime[0] = currentRuntime - currentGcTime;

            currentRuntime = timeGetMASTNaive(tree1, tree2);
            currentGcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtime[1] = currentRuntime;
            gctime[1] = currentGcTime;
            gcSubtractedTime[1] = currentRuntime - currentGcTime;

            currentRuntime = timeGetMASTNaive(tree1, tree2);
            currentGcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtime[2] = currentRuntime;
            gctime[2] = currentGcTime;
            gcSubtractedTime[2] = currentRuntime - currentGcTime;

            currentRuntime = timeGetMASTNaive(tree1, tree2);
            currentGcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtime[3] = currentRuntime;
            gctime[3] = currentGcTime;
            gcSubtractedTime[3] = currentRuntime - currentGcTime;

            currentRuntime = timeGetMASTNaive(tree1, tree2);
            currentGcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtime[4] = currentRuntime;
            gctime[4] = currentGcTime;
            gcSubtractedTime[4] = currentRuntime - currentGcTime;

            long medianRuntime = median(runtime);
            long medianGctime = median(gctime);
            long medianGcSubtractedTime = median(gcSubtractedTime);
            System.out.println(i + "\t" + medianRuntime + "\t" + medianGctime + "\t" + medianGcSubtractedTime);
        }
    }

    private static void testNSquaredSingleRuns(int maxSize, String path) {
        initialRunsNSquared();

        System.out.println("Test:");
        GCMonitor gcMonitor = new GCMonitor();
        long runtime;
        long gctime;
        long gcSubtractedTime;
        for (int i = 50; i <= maxSize; i+= 50) {
            Pair<Phylogeny, Phylogeny> trees = getTreesFromNewick(path, i);
            Phylogeny tree1 = trees.getLeft();
            Phylogeny tree2 = trees.getRight();

            runtime = timeGetMASTNSquared(tree1, tree2);
            gctime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            gcSubtractedTime = runtime - gctime;
            System.out.println(i + "\t" + runtime + "\t" + gctime + "\t" + gcSubtractedTime);
        }
    }


    private static void testNSquaredVsNlogn(){
        initialRuns();
        initialRunsNSquared();

        long[] nlognRuntime = new long[5];
        long[] nsquaredRuntime = new long[5];
        GCMonitor gcMonitor = new GCMonitor();

        for (int i = 1; i <= 200; i++) {
            Phylogeny tree1 = PhylogenyGenerator.generatePerfectTree(i, false);
            Phylogeny tree2 = PhylogenyGenerator.generatePerfectTree(i, false);

            nlognRuntime[0] = timeGetMAST(tree1, tree2, false) - gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement();
            nlognRuntime[1] = timeGetMAST(tree1, tree2, false) - gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement();
            nlognRuntime[2] = timeGetMAST(tree1, tree2, false) - gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement();
            nlognRuntime[3] = timeGetMAST(tree1, tree2, false) - gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement();
            nlognRuntime[4] = timeGetMAST(tree1, tree2, false) - gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement();
            nsquaredRuntime[0] = timeGetMASTNSquared(tree1, tree2) - gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement();
            nsquaredRuntime[1] = timeGetMASTNSquared(tree1, tree2) - gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement();
            nsquaredRuntime[2] = timeGetMASTNSquared(tree1, tree2) - gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement();
            nsquaredRuntime[3] = timeGetMASTNSquared(tree1, tree2) - gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement();
            nsquaredRuntime[4] = timeGetMASTNSquared(tree1, tree2) - gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement();

            System.out.println(i + "\t" + median(nsquaredRuntime) + "\t" + median(nlognRuntime));
        }
    }


    private static void testNSquaredRandomTreesOld(int maxSize) {
        initialRunsNSquared();

        System.out.println("Test:");
        for (int i = 100; i <= maxSize; i+= 100) {
            long[] runtimes = new long[5];
            Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(i, false);
            runtimes[0] = timeGetMASTNSquared(tree1, tree2);
            runtimes[1] = timeGetMASTNSquared(tree1, tree2);
            runtimes[2] = timeGetMASTNSquared(tree1, tree2);
            runtimes[3] = timeGetMASTNSquared(tree1, tree2);
            runtimes[4] = timeGetMASTNSquared(tree1, tree2);
            long medianTime = median(runtimes);
            System.out.println(i + "\t" + medianTime);
        }
    }

    private static long timeGetMASTNSquared(Phylogeny tree1, Phylogeny tree2) {
        n_squared.MAST mast = new n_squared.MAST();
        long time = System.nanoTime();
        mast.getMAST(tree1, tree2);
        return System.nanoTime() - time;
    }

    private static long timeGetMASTNaive(Phylogeny tree1, Phylogeny tree2) {
        naive.MAST mast = new naive.MAST();
        long time = System.nanoTime();
        mast.getMAST(tree1, tree2);
        return System.nanoTime() - time;
    }

    private static void initialRunsNSquared() {
        System.out.println("Initial:");
        for (int i = 100; i >= 0; i--) {
            Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(100, true);
            Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(100, false);
            n_squared.MAST mast = new n_squared.MAST();
            mast.getMAST(tree1, tree2);
            System.out.println(i);
        }
    }

    private static void initialRunsNaive() {
        System.out.println("Initial:");
        for (int i = 100; i >= 0; i--) {
            Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(10, true);
            Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(10, false);
            naive.MAST mast = new naive.MAST();
            mast.getMAST(tree1, tree2);
            System.out.println(i);
        }
    }

    private static void runRandomTrees() {
//        initialRuns();

        System.out.println("Test:");
        for (int i = 100 ; i < 50000 ; i+= 100) { // GC overhead limit at size 42300
            Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(i, false);
            MAST mast = new MAST();
            mast.getMAST(tree1, tree2, false);
        }
    }

    private static void runRandomIdenticalTrees() {
        initialRuns();

        System.out.println("Test:");
        for (int i = 100 ; i < 50000 ; i+= 100) {
            Pair<Phylogeny, Phylogeny> trees = PhylogenyGenerator.generateIdenticalRandomTrees(i, false);
            Phylogeny tree1 = trees.getLeft();
            Phylogeny tree2 = trees.getRight();
            MAST mast = new MAST();
            mast.getMAST(tree1, tree2, false);
        }
    }

    private static void runBaseCaseTrees() {
//        initialRuns();

        System.out.println("Test:");
        for (int i = 100 ; i < 50000 ; i+= 100) { // GC overhead limit at size 42300
            Phylogeny tree1 = PhylogenyGenerator.generateBaseCaseTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generateBaseCaseTree(i, false);
            MAST mast = new MAST();
            mast.getMAST(tree1, tree2, false);
        }
    }

    private static void testGCOnRandomTrees(int maxsize, boolean recursive) {
        initialRuns();

        System.out.println("Test:");
        GCMonitor gcMonitor = new GCMonitor();
        for (int i = 100 ; i <= maxsize ; i+= 100) {
            long[] gcTimes = new long[5];
            Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(i, false);
            gcTimes[0] = timeGCGetMAST(tree1, tree2, recursive, gcMonitor);
            gcTimes[1] = timeGCGetMAST(tree1, tree2, recursive, gcMonitor);
            gcTimes[2] = timeGCGetMAST(tree1, tree2, recursive, gcMonitor);
            gcTimes[3] = timeGCGetMAST(tree1, tree2, recursive, gcMonitor);
            gcTimes[4] = timeGCGetMAST(tree1, tree2, recursive, gcMonitor);
            System.out.println(i + "\t" + median(gcTimes));
        }
    }

    private static void testGCOnBaseCaseTrees(int maxsize, boolean recursive) {
        initialRuns();

        System.out.println("Test:");
        GCMonitor gcMonitor = new GCMonitor();
        for (int i = 100 ; i <= maxsize ; i+= 100) {
            long[] gcTimes = new long[5];
            gcTimes[0] = timeGCGetMASTBaseCase(i, recursive, gcMonitor);
            gcTimes[1] = timeGCGetMASTBaseCase(i, recursive, gcMonitor);
            gcTimes[2] = timeGCGetMASTBaseCase(i, recursive, gcMonitor);
            gcTimes[3] = timeGCGetMASTBaseCase(i, recursive, gcMonitor);
            gcTimes[4] = timeGCGetMASTBaseCase(i, recursive, gcMonitor);
            System.out.println(i + "\t" + median(gcTimes));
        }
    }

    private static void testIterativeAndRecursive(int maxsize){
        System.out.println("Iterative:");
        try {
            testNLogN(maxsize, false, "testTrees\\randomTrees\\");
        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            System.out.println("Recursive:");
            testNLogN(maxsize, true, "testTrees\\randomTrees\\");
        }
    }

    private static void testIdenticalBaseCaseTrees(int size, boolean recursive){
        initialRuns();

        System.out.println("Test:");
        for (int i = 100; i <= size; i+= 100) {
            long[] runtimes = new long[5];
            Phylogeny tree1 = PhylogenyGenerator.generateBaseCaseTree(i, false);
            Phylogeny tree2 = PhylogenyGenerator.generateBaseCaseTree(i, false);
            runtimes[0] = timeGetMAST(tree1, tree2, recursive);
            runtimes[1] = timeGetMAST(tree1, tree2, recursive);
            runtimes[2] = timeGetMAST(tree1, tree2, recursive);
            runtimes[3] = timeGetMAST(tree1, tree2, recursive);
            runtimes[4] = timeGetMAST(tree1, tree2, recursive);
            long medianTime = median(runtimes);
            System.out.println(i + "\t" + medianTime);
        }
    }

    private static void testNonSimilarBaseCaseTrees(int size, boolean recursive){
        initialRuns();

        System.out.println("Test:");
        for (int i = 100; i <= size; i+= 100) {
            Phylogeny tree1 = PhylogenyGenerator.generateBaseCaseTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generateBaseCaseTree(i, false);
            PhylogenyGenerator.renameTreeLeavesRightToLeft(tree1);
            long[] runtimes = new long[5];
            runtimes[0] = timeGetMAST(tree1, tree2, recursive);
            runtimes[1] = timeGetMAST(tree1, tree2, recursive);
            runtimes[2] = timeGetMAST(tree1, tree2, recursive);
            runtimes[3] = timeGetMAST(tree1, tree2, recursive);
            runtimes[4] = timeGetMAST(tree1, tree2, recursive);
            long medianTime = median(runtimes);
//            System.out.println(i + "\t" + ((int)(medianTime/nLogN(i))));
            System.out.println(i + "\t" + medianTime);
        }
    }

    private static void testNonSimilarBaseCaseTreesMLIS(int size){
        initialRuns();

        System.out.println("Test:");
        for (int i = 100; i <= size; i+= 100) {
            Phylogeny tree1 = PhylogenyGenerator.generateBaseCaseTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generateBaseCaseTree(i, false);
            PhylogenyGenerator.renameTreeLeavesRightToLeft(tree1);
            long[] runtimes = new long[5];
            runtimes[0] = timeGetMASTUsingMLIS(tree1, tree2);
            runtimes[1] = timeGetMASTUsingMLIS(tree1, tree2);
            runtimes[2] = timeGetMASTUsingMLIS(tree1, tree2);
            runtimes[3] = timeGetMASTUsingMLIS(tree1, tree2);
            runtimes[4] = timeGetMASTUsingMLIS(tree1, tree2);
            long medianTime = median(runtimes);
//            System.out.println(i + "\t" + ((int)(medianTime/nLogN(i))));
            System.out.println(i + "\t" + medianTime);
        }
    }

    private static void testRandomTreesOld(int maxSize, boolean recursive) {
        initialRuns();

        System.out.println("Test:");
        for (int i = 100; i <= maxSize; i+= 100) { // GC overhead limit at size 42300
            long[] runtimes = new long[5];
            runtimes[0] = timeGetMASTRandomTrees(i, recursive);
            runtimes[1] = timeGetMASTRandomTrees(i, recursive);
            runtimes[2] = timeGetMASTRandomTrees(i, recursive);
            runtimes[3] = timeGetMASTRandomTrees(i, recursive);
            runtimes[4] = timeGetMASTRandomTrees(i, recursive);
            long medianTime = median(runtimes);
//            System.out.println(i + "\t" + ((int)(medianTime/nLogN(i))));
            System.out.println(i + "\t" + medianTime);
        }
    }

    private static void testRandomTreesGCSubtractedOld(int maxSize, boolean recursive) {
        initialRuns();

        System.out.println("Test:");
        GCMonitor gcMonitor = new GCMonitor();
        long runtime;
        long gcTime;
        for (int i = 100; i <= maxSize; i+= 100) {
            long[] runtimes = new long[5];
            runtime = timeGetMASTRandomTrees(i, recursive);
            gcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtimes[0] = runtime - gcTime;
            runtime = timeGetMASTRandomTrees(i, recursive);
            gcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtimes[1] = runtime - gcTime;
            runtime = timeGetMASTRandomTrees(i, recursive);
            gcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtimes[2] = runtime - gcTime;
            runtime = timeGetMASTRandomTrees(i, recursive);
            gcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtimes[3] = runtime - gcTime;
            runtime = timeGetMASTRandomTrees(i, recursive);
            gcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtimes[4] = runtime - gcTime;
            long medianTime = median(runtimes);
            System.out.println(i + "\t" + medianTime);
        }
    }

    private static void testRandomTreesGCSubtracted(int maxSize, boolean recursive) {
        initialRuns();

        System.out.println("Test:");
        GCMonitor gcMonitor = new GCMonitor();
        long runtime;
        long gcTime;
        for (int i = 100; i <= maxSize; i+= 100) {
            long[] runtimes = new long[5];
            Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(i, false);
            runtime = timeGetMAST(tree1, tree2, recursive);
            gcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtimes[0] = runtime - gcTime;
            runtime = timeGetMAST(tree1, tree2, recursive);
            gcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtimes[1] = runtime - gcTime;
            runtime = timeGetMAST(tree1, tree2, recursive);
            gcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtimes[2] = runtime - gcTime;
            runtime = timeGetMAST(tree1, tree2, recursive);
            gcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtimes[3] = runtime - gcTime;
            runtime = timeGetMAST(tree1, tree2, recursive);
            gcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtimes[4] = runtime - gcTime;
            long medianTime = median(runtimes);
            System.out.println(i + "\t" + medianTime);
        }
    }

    private static void testPerfectTrees(int maxSize, boolean recursive) {
        initialRuns();

        System.out.println("Test:");
        for (int i = 100; i <= maxSize; i+= 100) {
            Phylogeny tree1 = PhylogenyGenerator.generatePerfectTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generatePerfectTree(i, false);
            long[] runtimes = new long[5];
            runtimes[0] = timeGetMAST(tree1, tree2, recursive);
            runtimes[1] = timeGetMAST(tree1, tree2, recursive);
            runtimes[2] = timeGetMAST(tree1, tree2, recursive);
            runtimes[3] = timeGetMAST(tree1, tree2, recursive);
            runtimes[4] = timeGetMAST(tree1, tree2, recursive);
            long medianTime = median(runtimes);
//            System.out.println(i + "\t" + ((int)(medianTime/nLogN(i))));
            System.out.println(i + "\t" + medianTime);
        }
    }

    private static void testPerfectTreesGCSubtracted(int maxSize, boolean recursive) {
        initialRuns();

        System.out.println("Test:");
        GCMonitor gcMonitor = new GCMonitor();
        long runtime;
        long gcTime;
        for (int i = 100; i <= maxSize; i+= 100) {
            Phylogeny tree1 = PhylogenyGenerator.generatePerfectTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generatePerfectTree(i, false);
            long[] runtimes = new long[5];
            runtime = timeGetMAST(tree1, tree2, recursive);
            gcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtimes[0] = runtime - gcTime;
            runtime = timeGetMAST(tree1, tree2, recursive);
            gcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtimes[1] = runtime - gcTime;
            runtime = timeGetMAST(tree1, tree2, recursive);
            gcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtimes[2] = runtime - gcTime;
            runtime = timeGetMAST(tree1, tree2, recursive);
            gcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtimes[3] = runtime - gcTime;
            runtime = timeGetMAST(tree1, tree2, recursive);
            gcTime = gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement() * 1000000;
            runtimes[4] = runtime - gcTime;
            long medianTime = median(runtimes);
//            System.out.println(i + "\t" + ((int)(medianTime/nLogN(i))));
            System.out.println(i + "\t" + medianTime);
        }
    }

    private static void testGCOnPerfectTrees(int maxSize, boolean recursive) {
        initialRuns();

        System.out.println("Test:");
        GCMonitor gcMonitor = new GCMonitor();
        for (int i = 100; i <= maxSize; i+= 100) {
            Phylogeny tree1 = PhylogenyGenerator.generatePerfectTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generatePerfectTree(i, false);
            long[] runtimes = new long[5];
            runtimes[0] = timeGCGetMAST(tree1, tree2, recursive, gcMonitor);
            runtimes[1] = timeGCGetMAST(tree1, tree2, recursive, gcMonitor);
            runtimes[2] = timeGCGetMAST(tree1, tree2, recursive, gcMonitor);
            runtimes[3] = timeGCGetMAST(tree1, tree2, recursive, gcMonitor);
            runtimes[4] = timeGCGetMAST(tree1, tree2, recursive, gcMonitor);
            long medianTime = median(runtimes);
//            System.out.println(i + "\t" + ((int)(medianTime/nLogN(i))));
            System.out.println(i + "\t" + medianTime);
        }
    }

    private static void testInduceSubtrees() {
        for (int i = 10; i < 50000; i+= 50) {
            Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(i, false);
            tree1.recalculateNumberOfExternalDescendants(true);
            tree2.recalculateNumberOfExternalDescendants(true);
            addNodeDataReferences(tree1);
            addNodeDataReferences(tree2);

            MAST mastFinder = new MAST();
            mastFinder.setTwins(tree1, tree2);
            List<PhylogenyNode> tree1Decomposition = mastFinder.computeFirstDecomposition(tree1);

            long time = System.nanoTime();
            mastFinder.induceSubtrees(tree1Decomposition, tree1, tree2);
            System.out.println(i + "\t" + (System.nanoTime() - time)/i);
        }
    }

    private static void initialRuns() {
        System.out.println("Initial:");
        for (int i = 100; i >= 0; i--) {
            Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(1000, true);
            Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(1000, false);
            MAST mast = new MAST();
            mast.getMAST(tree1, tree2, false);
            System.out.println(i);
        }
    }

    private static long timeGetMASTRandomTrees(int size, boolean recursive) {
        Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(size, true);
        Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(size, false);
        MAST mast = new MAST();
        long time = System.nanoTime();
        mast.getMAST(tree1, tree2, recursive);
        return System.nanoTime() - time;
    }

    private static long timeGetMAST(Phylogeny tree1, Phylogeny tree2, boolean recursive) {
        MAST mast = new MAST();
        long time = System.nanoTime();
        mast.getMAST(tree1, tree2, recursive);
        return System.nanoTime() - time;
    }

    private static long timeGetMASTUsingMLIS(Phylogeny tree1, Phylogeny tree2) {
        MAST mast = new MAST();
        long time = System.nanoTime();
        mast.getMASTUsingMLIS(tree1, tree2);
        return System.nanoTime() - time;
    }

    private static long timeGCGetMASTRandomTrees(int size, boolean recursive, GCMonitor gcMonitor) {
        Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(size, true);
        Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(size, false);
        MAST mast = new MAST();
        mast.getMAST(tree1, tree2, recursive);
        return gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement();
    }

    private static long timeGCGetMAST(Phylogeny tree1, Phylogeny tree2, boolean recursive, GCMonitor gcMonitor) {
        MAST mast = new MAST();
        mast.getMAST(tree1, tree2, recursive);
        return gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement();
    }

    private static long timeGCGetMASTBaseCase(int size, boolean recursive, GCMonitor gcMonitor) {
        Phylogeny tree1 = PhylogenyGenerator.generateBaseCaseTree(size, true);
        Phylogeny tree2 = PhylogenyGenerator.generateBaseCaseTree(size, false);
        MAST mast = new MAST();
        mast.getMAST(tree1, tree2, recursive);
        return gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement();
    }

    private static double nLogN(int n){
        return n * (Math.log(n) / Math.log(2));
    }

    private static double nLogNCube(int n){
        double logn = Math.log(n) / Math.log(2);
        return n * logn * logn * logn;
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

    private static long median(long[] numbers){
        Arrays.sort(numbers);
        return numbers[2];
    }

    private static void writeRandomTreesToNewick(){
        String path = "testTrees\\randomTrees\\";
        PhylogenyParser parser = new PhylogenyParser();
        for (int i = 200; i <= 80000; i+= 200) {
            Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(i, false);
            parser.toNewick(tree1, tree2, path + i, false);
            System.out.println(i);
        }
    }

    private static void writeRandomTreesToNewick2(){
        String path = "testTrees\\randomTrees2\\";
        PhylogenyParser parser = new PhylogenyParser();
        for (int i = 50; i <= 10000; i+= 50) {
            Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(i, false);
            parser.toNewick(tree1, tree2, path + i, false);
            System.out.println(i);
        }
    }

    private static void writeCompleteTreesToNewick(){
        String path = "testTrees\\completeTrees\\";
        PhylogenyParser parser = new PhylogenyParser();
        for (int i = 200; i <= 80000; i+= 200) {
            Phylogeny tree1 = PhylogenyGenerator.generatePerfectTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generatePerfectTree(i, false);
            parser.toNewick(tree1, tree2, path + i, false);
            System.out.println(i);
        }
    }

    private static void writeIdenticalCompleteTreesToNewick(){
        String path = "testTrees\\completeTrees2\\";
        PhylogenyParser parser = new PhylogenyParser();
        for (int i = 50; i <= 10000; i+= 50) {
            Phylogeny tree1 = PhylogenyGenerator.generatePerfectTree(i, false);
            Phylogeny tree2 = PhylogenyGenerator.generatePerfectTree(i, false);
            parser.toNewick(tree1, tree2, path + i, false);
            System.out.println(i);
        }
    }

    private static void writeBestCaseTreesToNewick(){
        String path = "testTrees\\bestCaseTrees\\";
        PhylogenyParser parser = new PhylogenyParser();
        for (int i = 200; i <= 80000; i+= 200) {
            Phylogeny tree1 = PhylogenyGenerator.generateBaseCaseTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generateBaseCaseTree(i, false);
            parser.toNewick(tree1, tree2, path + i, false);
            System.out.println(i);
        }
    }

    private static void writeBestCaseTreesToNewick2(){
        String path = "testTrees\\bestCaseTrees2\\";
        PhylogenyParser parser = new PhylogenyParser();
        for (int i = 50; i <= 10000; i+= 50) {
            Phylogeny tree1 = PhylogenyGenerator.generateBaseCaseTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generateBaseCaseTree(i, false);
            parser.toNewick(tree1, tree2, path + i, false);
            System.out.println(i);
        }
    }

    private static Pair<Phylogeny, Phylogeny> getTreesFromNewick(String path, int treeSize){
        return new ForesterNewickParser().parseNewickFileTwoTrees(path + treeSize + ".new");
    }

    private static Phylogeny copyTree(Phylogeny tree){
        Phylogeny result = new Phylogeny();

        Stack<Pair> remainingNodes = new Stack<>();
        PhylogenyNode root = tree.getRoot();
        PhylogenyNode newRoot = new PhylogenyNode();
        Pair<PhylogenyNode, PhylogenyNode> rootPair = new Pair<>(root, newRoot);
        remainingNodes.push(rootPair);

        while(!remainingNodes.isEmpty()){
            Pair<PhylogenyNode, PhylogenyNode> nodePair = remainingNodes.pop();
            PhylogenyNode oldNode = nodePair.getLeft();
            PhylogenyNode newNode = nodePair.getRight();
            if(oldNode.isExternal()){
                newNode.setName(oldNode.getName());
                continue;
            }
            PhylogenyNode newChild1 = new PhylogenyNode();
            PhylogenyNode newChild2 = new PhylogenyNode();
            newNode.setChild1(newChild1);
            newNode.setChild2(newChild2);
            remainingNodes.push(new Pair<>(oldNode.getChildNode1(), newChild1));
            remainingNodes.push(new Pair<>(oldNode.getChildNode2(), newChild2));
        }
        result.setRoot(newRoot);
        return result;
    }
}
