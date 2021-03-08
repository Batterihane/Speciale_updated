package Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 23-03-2016.
 */
public class LongestIncreasingSubsequence {
    private int[] lisLengths;

    public static void main(String[] args) {
        int[] a = new int[9];
        a[0] = 2;
        a[1] = 6;
        a[2] = 3;
        a[3] = 4;
        a[4] = 1;
        a[5] = 2;
        a[6] = 9;
        a[7] = 5;
        a[8] = 8;
        LongestIncreasingSubsequence lis = new LongestIncreasingSubsequence();
        findLIS(a);

        /*
        for (int i : lis.findLISModified(a)){
            System.out.print(i + " ");
        }

        System.out.println();
        for (int i : lis.getLisLengths()){
            System.out.print(i + " ");
        }
        */
    }

    public static int[] findLIS(int[] numbers) {
        int[] parents = new int[numbers.length];
        List<Integer> increasingSequence= new ArrayList<>();
        List<Integer> increasingSequenceIndices = new ArrayList<>();
        increasingSequence.add(numbers[0]);
        increasingSequenceIndices.add(0);

        for (int i = 1; i < numbers.length; i++) {
            int currentNumber = numbers[i];
            int lastElementIndex = increasingSequenceIndices.size()-1;
            if(currentNumber > increasingSequence.get(lastElementIndex)){
                increasingSequence.add(currentNumber);
                increasingSequenceIndices.add(i);
                parents[i] = increasingSequenceIndices.get(lastElementIndex);
            }
            else {
                int currentNumberIndex = binarySearch(increasingSequence, currentNumber);
                increasingSequence.set(currentNumberIndex, currentNumber);
                increasingSequenceIndices.set(currentNumberIndex, i);
                if(currentNumberIndex > 0)
                    parents[i] = increasingSequenceIndices.get(currentNumberIndex - 1);
                else parents[i] = -1;
            }
        }

        int resultSize = increasingSequence.size();
        int[] result = new int[resultSize];
        Integer index = increasingSequenceIndices.get(resultSize - 1);
        result[resultSize-1] = numbers[index];
        for (int i = resultSize-2; i >= 0; i--) {
            index = parents[index];
            result[i] = numbers[index];
        }

        return result;
    }

    private static int binarySearch(List<Integer> a, int key) {
        int low = 0;
        int high = a.size() - 2;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            int midVal = a.get(mid);

            if (midVal < key)
                low = mid + 1;
            else if (midVal > key)
                high = mid - 1;
            else
                return mid; // key found
        }
        return low;  // key not found.
    }

    public int[] findLISModified(int[] numbers) {
        int[] parents = new int[numbers.length];
        int[] beforeParents = new int[numbers.length]; // necessary for when last element is moved
        lisLengths = new int[numbers.length];
        List<Integer> increasingSequence= new ArrayList<>();
        List<Integer> increasingSequenceIndices = new ArrayList<>();
        increasingSequence.add(numbers[0]);
        increasingSequenceIndices.add(0);
        increasingSequence.add(numbers[1]);
        increasingSequenceIndices.add(1);
        parents[1] = 0;
        lisLengths[0] = 1;
        lisLengths[1] = 2;

        for (int i = 2; i < numbers.length; i++) {
            int currentNumber = numbers[i];
            int lastElementIndex = increasingSequenceIndices.size()-1;
            Integer parent = increasingSequence.get(lastElementIndex);
            int grandParentIndex = parents[increasingSequenceIndices.get(lastElementIndex)];
            int grandParent = numbers[grandParentIndex];
            if(parent > grandParent){
                if(currentNumber > grandParent){
                    increasingSequence.add(currentNumber);
                    increasingSequenceIndices.add(i);
                    parents[i] = increasingSequenceIndices.get(lastElementIndex);
                    beforeParents[i] = increasingSequenceIndices.get(lastElementIndex-1);
                }
                else {
                    int currentNumberIndex = binarySearch(increasingSequence, currentNumber);
                    increasingSequence.set(currentNumberIndex, currentNumber);
                    increasingSequenceIndices.set(currentNumberIndex, i);
                    if(currentNumberIndex > 0)
                        parents[i] = increasingSequenceIndices.get(currentNumberIndex - 1);
                    else parents[i] = -1;
                }
            }
            else if(currentNumber > increasingSequence.get(lastElementIndex-1) || currentNumber > parent) {
                if(parent < increasingSequence.get(lastElementIndex-1)){
                    increasingSequence.set(lastElementIndex-1, parent);
                    Integer parentIndex = increasingSequenceIndices.get(lastElementIndex);
                    increasingSequenceIndices.set(lastElementIndex-1, parentIndex);
                    parents[parentIndex] = beforeParents[parentIndex];
                }

                increasingSequence.set(lastElementIndex, currentNumber);
                increasingSequenceIndices.set(lastElementIndex, i);
                parents[i] = increasingSequenceIndices.get(lastElementIndex-1);
                if(lastElementIndex > 1)
                    beforeParents[i] = increasingSequenceIndices.get(lastElementIndex-2);
            }
            else {
                int currentNumberIndex = binarySearch(increasingSequence, currentNumber);
                increasingSequence.set(currentNumberIndex, currentNumber);
                increasingSequenceIndices.set(currentNumberIndex, i);
                if(currentNumberIndex > 0)
                    parents[i] = increasingSequenceIndices.get(currentNumberIndex - 1);
                else parents[i] = -1;
            }
            lisLengths[i] = increasingSequence.size();
        }

        int resultSize = increasingSequence.size();
        int[] result = new int[resultSize];
        Integer index = increasingSequenceIndices.get(resultSize - 1);
        result[resultSize-1] = numbers[index];
        for (int i = resultSize-2; i >= 0; i--) {
            index = parents[index];
            result[i] = numbers[index];
        }

        return result;
    }

    public int[] getLisLengths(){
        return lisLengths;
    }
}
