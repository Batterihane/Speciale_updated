import Utilities.WeightBalancedBinarySearchTree;

import java.util.Arrays;

public class WeightBalancedBinarySearchTreeTest {
    public static void main(String[] args) {
        testConstructTree(100000);
    }

    private static void testConstructTree(int size) {
        initialRuns();

        System.out.println("Test:");
        for (int i = 100; i <= size; i+=100) {
            double[] weights = new double[i];
            for (int j = 0; j < i; j++) {
                weights[j] = 1;
            }

            long[] runtimes = new long[5];
            runtimes[0] = timeConstructTree(weights);
            runtimes[1] = timeConstructTree(weights);
            runtimes[2] = timeConstructTree(weights);
            runtimes[3] = timeConstructTree(weights);
            runtimes[4] = timeConstructTree(weights);

            System.out.println(i + "\t" + median(runtimes));
        }
    }

    private static long timeConstructTree(double[] weights) {
        long time = System.nanoTime();
        new WeightBalancedBinarySearchTree().constructTree(weights);
        return System.nanoTime() - time;
    }

    private static void testWorstCase() {
        int size = 10000000;
        WeightBalancedBinarySearchTree weightBalancedBinarySearchTree = new WeightBalancedBinarySearchTree();

        int[] weights = new int[size];
        for (int i = 0; i < size; i++) {
            weights[i] = i;
        }

////        int leftOfMiddleIndex = (size + 1) / 2 - 1;
//        int leftOfMiddleIndex = 0;
//        weightBalancedBinarySearchTree.testerIterative(weights, leftOfMiddleIndex, 0, size-1);
//        System.out.println(weightBalancedBinarySearchTree.getNumberOfCallsToFindIndexOfCut());

        for (int i = 10000; i < size; i+=10000) {
            int leftOfMiddleIndex = (i + 1) / 2 - 1;
//            int leftOfMiddleIndex = 1;
            long time = System.nanoTime();
            weightBalancedBinarySearchTree.testerIterative(weights, leftOfMiddleIndex, 0, i-1);
            System.out.println((System.nanoTime() - time)/i);
        }
    }

    private static void initialRuns() {
        System.out.println("Initial:");
        double[] weights = new double[10000];
        for (int j = 0; j < 10000; j++) {
            weights[j] = 1;
        }
        for (int i = 20; i >= 0; i--) {
            new WeightBalancedBinarySearchTree().constructTree(weights);
            System.out.println(i);
        }
    }

    private static long median(long[] numbers){
        Arrays.sort(numbers);
        return numbers[2];
    }
}
