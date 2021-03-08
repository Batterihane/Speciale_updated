package n_squared;

import Utilities.ForesterNewickParser;
import Utilities.PhylogenyGenerator;
import org.forester.archaeopteryx.Archaeopteryx;
import org.forester.archaeopteryx.MainFrame;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

public class Runner {
    public static void main(String[] args) {

    }

    private static void initialRunsNSquared() {
        System.out.println("Initial:");
        for (int i = 100; i >= 0; i--) {
            Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(1000, true);
            Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(1000, false);
            n_squared.MAST mast = new n_squared.MAST();
            mast.getMAST(tree1, tree2);
            System.out.println(i);
        }
    }

    private static void runRandomTrees() {
        initialRunsNSquared();

        System.out.println("Test:");
        for (int i = 100 ; i < 50000 ; i+= 100) {
            Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(i, false);
            MAST mast = new MAST();
            mast.getMAST(tree1, tree2);
        }
    }

    private static void backTrackTest() {
        for (int i = 10; i < 10000; i+= 10) {
            Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(i, false);

            n_squared.MAST nSquaredMastFinder = new n_squared.MAST();
//            int nLogNMastSize = nLogNMastFinder.getMAST(tree1, tree2).getNumberOfExternalNodes();
            Phylogeny nSquaredMast = nSquaredMastFinder.getMAST(tree1, tree2);
            int nSquaredMastSize = nSquaredMast.getNumberOfExternalNodes();

            Phylogeny backTrackMast = nSquaredMastFinder.getMastBackTrack(tree1, tree2);
            int backTrackSize = backTrackMast.getNumberOfExternalNodes();

            if(backTrackSize != nSquaredMastSize){
                Archaeopteryx.createApplication(tree1);
                Archaeopteryx.createApplication(tree2);
                Archaeopteryx.createApplication(backTrackMast);
                Archaeopteryx.createApplication(nSquaredMast);

                System.out.println(i + ": Failure - BackTrack(" + backTrackSize + "), nsquared(" + nSquaredMastSize + ")");
                return;
            }

            MainFrame application = Archaeopteryx.createApplication(backTrackMast);
            application.dispose();

            System.out.println(i + ": Success!");

        }
    }
}
