import Utilities.PhylogenyGenerator;
import nlogn.MAST;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;

import java.util.List;

public class MatchingGraphsRuntimeTest {
    public static void main(String[] args) {
        int maxTreeSize = 80000;

        for (int i = 100; i <= maxTreeSize; i+=100) {
            Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(i, false);

            MAST mastFinder = new MAST();
            mastFinder.addNodeDataReferences(tree1);
            mastFinder.addNodeDataReferences(tree2);

            tree1.recalculateNumberOfExternalDescendants(true);
            tree2.recalculateNumberOfExternalDescendants(true);

            mastFinder.setTwins(tree1, tree2);

            List<PhylogenyNode> tree1Decomposition = mastFinder.computeFirstDecomposition(tree1);
            List<List<PhylogenyNode>> tree2Decomposition = mastFinder.computeSecondDecompositionAndAddLWAMsToLeaves(tree2);

            List<Phylogeny> siSubtrees = mastFinder.induceSubtrees(tree1Decomposition, tree1, tree2);

            long time = System.nanoTime();
            mastFinder.findAndAddGraphEdges(tree1Decomposition, tree2Decomposition, siSubtrees);
            System.out.println(i + "\t" + (System.nanoTime() - time));
        }
    }
}
