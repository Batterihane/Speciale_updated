package Utilities.DataObjects;

import org.forester.phylogeny.data.Reference;

/**
 * Created by Thomas on 24-03-2016.
 */
public class NSquaredMASTNodeData extends Reference {

    private int id;

    public NSquaredMASTNodeData(int id){
        super("");
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
