import Utilities.PhylogenyGenerator;
import n_squared.MAST;
import org.forester.phylogeny.Phylogeny;

import java.util.Arrays;

public class MASTSquaredRuntimeTest {
    public static void main(String[] args) {
//        testRandomTrees(50000);
        testGCOnRandomTrees(50000);
    }

    private static void testRandomTrees(int maxSize) {
        initialRuns();

        System.out.println("Test:");
        for (int i = 100; i <= maxSize; i+= 100) {
            long[] runtimes = new long[5];
            runtimes[0] = timeGetMAST(i);
            runtimes[1] = timeGetMAST(i);
            runtimes[2] = timeGetMAST(i);
            runtimes[3] = timeGetMAST(i);
            runtimes[4] = timeGetMAST(i);
            long medianTime = median(runtimes);
            System.out.println(i + "\t" + ((int)(medianTime/(i*i))));
        }
    }

    private static void testGCOnRandomTrees(int maxSize) {
        initialRuns();

        System.out.println("Test:");
        GCMonitor gcMonitor = new GCMonitor();
        for (int i = 100; i <= maxSize; i+= 100) {
            long[] runtimes = new long[5];
            runtimes[0] = timeGCGetMAST(i, gcMonitor);
            runtimes[1] = timeGCGetMAST(i, gcMonitor);
            runtimes[2] = timeGCGetMAST(i, gcMonitor);
            runtimes[3] = timeGCGetMAST(i, gcMonitor);
            runtimes[4] = timeGCGetMAST(i, gcMonitor);
            long medianTime = median(runtimes);
            System.out.println(i + "\t" + ((int)(medianTime)));
        }
    }

    private static long timeGetMAST(int size) {
        Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(size, true);
        Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(size, false);
        MAST mast = new MAST();
        long time = System.nanoTime();
        mast.getMAST(tree1, tree2);
        return System.nanoTime() - time;
    }

    private static long timeGCGetMAST(int size, GCMonitor gcMonitor) {
        Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(size, true);
        Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(size, false);
        MAST mast = new MAST();
        mast.getMAST(tree1, tree2);
        return gcMonitor.getTimeUsedOnGarbageCollectingSinceLastMeasurement();
    }

    private static void initialRuns() {
        System.out.println("Initial:");
        for (int i = 20; i >= 0; i--) {
            Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(1000, true);
            Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(1000, false);
            MAST mast = new MAST();
            mast.getMAST(tree1, tree2);
            System.out.println(i);
        }
    }

    private static long median(long[] numbers){
        Arrays.sort(numbers);
        return numbers[2];
    }
}
