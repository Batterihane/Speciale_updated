package n_squared;

import org.forester.phylogeny.PhylogenyNode;

/**
 * Created by Thomas on 10-02-2016.
 */
public class MASTPair {
    private int size;
    private PhylogenyNode mast;

    public MASTPair(){
        size = 0;
    }

    public MASTPair(PhylogenyNode mast, int size){
        this.mast = mast;
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public PhylogenyNode getMast() {
        return mast;
    }
}
