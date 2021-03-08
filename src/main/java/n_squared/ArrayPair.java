package n_squared;

/**
 * Created by Nikolaj on 20-04-2016.
 */
public class ArrayPair {
    private int[][] sizeTable;
    private int[] traversalNumbers;

    public ArrayPair(int[][] sizeTable, int[] traversalNumbers) {
        this.sizeTable = sizeTable;
        this.traversalNumbers = traversalNumbers;
    }

    public int[] getTraversalNumbers() {
        return traversalNumbers;
    }

    public void setTraversalNumbers(int[] traversalNumbers) {
        this.traversalNumbers = traversalNumbers;
    }

    public int[][] getSizeTable() {
        return sizeTable;
    }

    public void setSizeTable(int[][] sizeTable) {
        this.sizeTable = sizeTable;
    }



}
