package Utilities;

/**
 * Created by Thomas on 21-04-2016.
 */
public class Pair<T, S> {
    private final T left;
    private final S right;

    public Pair(T left, S right){
        this.left = left;
        this.right = right;
    }

    public T getLeft() {
        return left;
    }

    public S getRight() {
        return right;
    }
}
