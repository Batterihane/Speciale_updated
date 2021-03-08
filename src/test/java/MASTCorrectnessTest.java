import Utilities.ForesterNewickParser;
import Utilities.Pair;
import Utilities.PhylogenyGenerator;
import Utilities.PhylogenyParser;
import nlogn.MAST;
import org.forester.archaeopteryx.Archaeopteryx;
import org.forester.archaeopteryx.MainFrame;
import org.forester.phylogeny.Phylogeny;

/**
 * Created by Thomas on 24-03-2016.
 */
public class MASTCorrectnessTest {
    public static void main(String[] args) {
        nLognVsNSquared();
//        nLognVsNSquaredTestTrees();
//        lisVsNSquared();
//        nSquaredVsNaive();
    }

    private static void nLognVsNSquared() {
        for (int i = 100; i < 10000; i+= 100) {
            Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(i, false);

            MAST nLogNMastFinder = new MAST();
            n_squared.MAST nSquaredMastFinder = new n_squared.MAST();
            MAST.TreeAndSizePair nLogNTreeAndSize = nLogNMastFinder.getMAST(tree1, tree2, false);
            Phylogeny nLogNMast = nLogNTreeAndSize.getTree();
            int nLogNMastSize = nLogNTreeAndSize.getSize();
//            int nLogNMastSize = nLogNMastFinder.getMAST(tree1, tree2).getNumberOfExternalNodes();
            Phylogeny nSquaredMast = nSquaredMastFinder.getMAST(tree1, tree2);
            int nSquaredMastSize = nSquaredMast.getNumberOfExternalNodes();

            if(nLogNMastSize != nSquaredMastSize){
                System.out.println(i + ": Failure - nlogn(" + nLogNMastSize + "), nsquared(" + nSquaredMastSize + ")");
                new PhylogenyParser().toNewick(tree1, "aaaa", false);
                new PhylogenyParser().toNewick(tree2, "bbbb", false);
                Archaeopteryx.createApplication(tree1);
                Archaeopteryx.createApplication(tree2);
                Archaeopteryx.createApplication(nLogNMast);
                Archaeopteryx.createApplication(nSquaredMast);

                return;
            }

//            MainFrame application = Archaeopteryx.createApplication(nLogNMast);
//            application.dispose();

            System.out.println(i + ": Success!");

        }
    }

    private static void nLognVsNSquaredTestTrees() {
        for (int i = 200; i < 10000; i+= 200) {
            Pair<Phylogeny, Phylogeny> trees = new ForesterNewickParser().parseNewickFileTwoTrees("testTrees\\randomTrees\\" + i + ".new");
            Phylogeny tree1 = trees.getLeft();
            Phylogeny tree2 = trees.getRight();

            MAST nLogNMastFinder = new MAST();
            n_squared.MAST nSquaredMastFinder = new n_squared.MAST();
            MAST.TreeAndSizePair nLogNTreeAndSize = nLogNMastFinder.getMAST(tree1, tree2, false);
            Phylogeny nLogNMast = nLogNTreeAndSize.getTree();
            int nLogNMastSize = nLogNTreeAndSize.getSize();
//            int nLogNMastSize = nLogNMastFinder.getMAST(tree1, tree2).getNumberOfExternalNodes();
            Phylogeny nSquaredMast = nSquaredMastFinder.getMAST(tree1, tree2);
            int nSquaredMastSize = nSquaredMast.getNumberOfExternalNodes();

            if(nLogNMastSize != nSquaredMastSize){
                System.out.println(i + ": Failure - nlogn(" + nLogNMastSize + "), nsquared(" + nSquaredMastSize + ")");
                Archaeopteryx.createApplication(tree1);
                Archaeopteryx.createApplication(tree2);
                Archaeopteryx.createApplication(nLogNMast);
                Archaeopteryx.createApplication(nSquaredMast);

                return;
            }

//            MainFrame application = Archaeopteryx.createApplication(nLogNMast);
//            application.dispose();

            System.out.println(i + ": Success!");

        }
    }

    private static void nLognVsNSquaredConstantSize(int size) {
        while (true) {
            Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(size, true);
            Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(size, false);

            MAST nLogNMastFinder = new MAST();
            n_squared.MAST nSquaredMastFinder = new n_squared.MAST();
            MAST.TreeAndSizePair nLogNTreeAndSize = nLogNMastFinder.getMAST(tree1, tree2, false);
            Phylogeny nLogNMast = nLogNTreeAndSize.getTree();
            int nLogNMastSize = nLogNTreeAndSize.getSize();
//            int nLogNMastSize = nLogNMastFinder.getMAST(tree1, tree2).getNumberOfExternalNodes();
            Phylogeny nSquaredMast = nSquaredMastFinder.getMAST(tree1, tree2);
            int nSquaredMastSize = nSquaredMast.getNumberOfExternalNodes();

            if(nLogNMastSize != nSquaredMastSize){
                Archaeopteryx.createApplication(tree1);
                Archaeopteryx.createApplication(tree2);
                Archaeopteryx.createApplication(nLogNMast);
                Archaeopteryx.createApplication(nSquaredMast);

                System.out.println( "Failure - nlogn(" + nLogNMastSize + "), nsquared(" + nSquaredMastSize + ")");
                return;
            }

            MainFrame application = Archaeopteryx.createApplication(nLogNMast);
            application.dispose();

            System.out.println("Success!");

        }
    }

    private static void lisVsNSquared() {
        for (int i = 100; i < 10000; i+= 100) {
            Phylogeny tree1 = PhylogenyGenerator.generateBaseCaseTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generateBaseCaseTree(i, false);

            MAST nLogNMastFinder = new MAST();
            n_squared.MAST nSquaredMastFinder = new n_squared.MAST();
            Phylogeny lisMAST = nLogNMastFinder.getMASTUsingMLIS(tree1, tree2);
            int lisNMastSize = lisMAST.getNumberOfExternalNodes();
            Phylogeny nSquaredMast = nSquaredMastFinder.getMAST(tree1, tree2);
            int nSquaredMastSize = nSquaredMast.getNumberOfExternalNodes();

            if(lisNMastSize != nSquaredMastSize){
                Archaeopteryx.createApplication(tree1);
                Archaeopteryx.createApplication(tree2);
                Archaeopteryx.createApplication(lisMAST);
                Archaeopteryx.createApplication(nSquaredMast);

                System.out.println(i + ": Failure - nlogn(" + lisNMastSize + "), nsquared(" + nSquaredMastSize + ")");
                return;
            }

//            MainFrame application = Archaeopteryx.createApplication(nLogNMast);
//            application.dispose();

            System.out.println(i + ": Success!");

        }
    }

    private static void nSquaredVsNaive() {
        for (int i = 100; i < 10000; i+= 100) {
            Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(i, true);
            Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(i, false);

            naive.MAST naiveMastFinder = new naive.MAST();
            n_squared.MAST nSquaredMastFinder = new n_squared.MAST();
            Phylogeny naiveMast = naiveMastFinder.getMAST(tree1, tree2);
            int naiveMastSize = naiveMast.getNumberOfExternalNodes();
            Phylogeny nSquaredMast = nSquaredMastFinder.getMastBackTrack(tree1, tree2);
            int nSquaredMastSize = nSquaredMast.getNumberOfExternalNodes();

            if(naiveMastSize != nSquaredMastSize){
                System.out.println(i + ": Failure - naive(" + naiveMastSize + "), nsquared(" + nSquaredMastSize + ")");
                new PhylogenyParser().toNewick(tree1, "aaaa", false);
                new PhylogenyParser().toNewick(tree2, "bbbb", false);
                Archaeopteryx.createApplication(tree1);
                Archaeopteryx.createApplication(tree2);
                Archaeopteryx.createApplication(naiveMast);
                Archaeopteryx.createApplication(nSquaredMast);

                return;
            }

//            MainFrame application = Archaeopteryx.createApplication(naiveMast);
//            application.dispose();

            System.out.println(i + ": Success!");

        }
    }

}
