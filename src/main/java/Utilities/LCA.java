package Utilities;

import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;

/**
 * Created by Thomas on 19-02-2016.
 */
public class LCA {
    private final Phylogeny tree;

    public LCA(Phylogeny tree){
        this.tree = tree;
    }

    public PhylogenyNode getLCA(PhylogenyNode node1, PhylogenyNode node2){
        return getLCARecursive(tree.getRoot(), node1, node2);
    }

    private PhylogenyNode getLCARecursive(PhylogenyNode root, PhylogenyNode node1, PhylogenyNode node2){
        if(root.getId() == node1.getId() || root.getId() == node2.getId()) return root;
        if(root.isExternal()) return null;
        PhylogenyNode left = getLCARecursive(root.getChildNode1(), node1, node2);
        PhylogenyNode right = getLCARecursive(root.getChildNode2(), node1, node2);
        if(left != null){
            if(right != null) return root;
            else return left;
        }
        else return right;
    }
}
