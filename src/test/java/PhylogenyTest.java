import Utilities.PhylogenyGenerator;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

/**
 * Created by Thomas on 10-03-2016.
 */
public class PhylogenyTest {
    public static void main(String[] args) {
        iteratorPreorderTest();
    }

    private static void getAllExternalDescendantsRuntimeTest() {
        for (int i = 100; i < 50000; i+= 100) {
            Phylogeny tree = PhylogenyGenerator.generateRandomTree(i, true);
            PhylogenyNode root = tree.getRoot();
            long time = System.nanoTime();
            root.getAllExternalDescendants();
            time = System.nanoTime() - time;
            time = time/i;
            System.out.println(time);
        }
    }

    private static void iteratorPreorderTest() {
        for (int i = 100; i < 50000; i+= 100) {
            Phylogeny tree = PhylogenyGenerator.generateRandomTree(i, true);
            PhylogenyNodeIterator iterator = tree.iteratorPreorder();

            long time = System.nanoTime();
            while (iterator.hasNext()){
                PhylogenyNode next = iterator.next();
            }
            time = System.nanoTime() - time;
            time = time/i;
            System.out.println(time);
        }
    }

    private static void addChildRuntimeTest() {
        PhylogenyNode root = new PhylogenyNode();
        PhylogenyNode currentNode = root;
        for (int i = 0; i < 10000; i++) {
            PhylogenyNode newNode = new PhylogenyNode();
            long time = System.nanoTime();
            currentNode.setChild1(newNode);
            time = System.nanoTime() - time;
            System.out.println(time);

            PhylogenyNode newNode2 = new PhylogenyNode();
            currentNode.setChild2(newNode2);

            currentNode = newNode;
        }
//        Phylogeny tree = new Phylogeny();
//        tree.setRoot(root);
//        tree.recalculateNumberOfExternalDescendants(true);
//
//        System.out.println("Root has " + root.getNumberOfExternalNodes() + " external nodes");
//
//        Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(10);
//        System.out.println("Root2 has " + tree2.getRoot().getNumberOfExternalNodes() + " external nodes");
//
//        ForesterNewickParser foresterNewickParser = new ForesterNewickParser();
//        foresterNewickParser.displayPhylogeny(tree2);
    }
}
