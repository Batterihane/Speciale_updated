import Utilities.ForesterNewickParser;
import nlogn.MAST;
import org.forester.archaeopteryx.Archaeopteryx;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Runner {
    private static final Logger log = LoggerFactory.getLogger(Runner.class);

    public static final String NLOGN_ALG_STRING = "Currently selected algorithm is O(nlogn)\n";
    public static final String NSQUARED_ALG_STRING = "Currently selected algorithm is O(n^2)\n";
    public static final String INSTRUCTIONS = "type:\n\t" +
            "\"mast_size\" to compute the mast size of the input trees\n\t" +
            "\"mast\" to compute and display the mast of the input trees\n\t" +
            "\"show_trees\" to display the two input trees\n\t" +
            "\"swap\" to swap the selected algorithm\n\t" +
            "\"q\" to quit\n\t";

    public static void main(String[] args) {
        prompt();
    }

    private static void prompt(){
        log.info("test");
        String currentAlgorithmString = NLOGN_ALG_STRING;
        System.out.println(currentAlgorithmString + INSTRUCTIONS);

        String applicationInput = System.getProperty("applicationInput");
        applicationInput = applicationInput != null ? applicationInput : "src/main/inputTrees";
        String tree1Path = applicationInput + "/T1.new";
        String tree2Path = applicationInput + "/T2.new";
        boolean nlogn = true;
        ForesterNewickParser foresterNewickParser = new ForesterNewickParser();
        Phylogeny tree1, tree2;

        Scanner scanner = new Scanner(System.in);
        String input = "";
        while(!(input.equals("Q"))){
            input = scanner.next().toUpperCase();
            Phylogeny result;
            int resultSize;
            switch (input) {
                case "MAST_SIZE":
                    System.out.println("Constructing the MAST...");
                    tree1 = foresterNewickParser.parseNewickFileSingleTree(tree1Path);
                    tree2 = foresterNewickParser.parseNewickFileSingleTree(tree2Path);
                    resultSize = nlogn ? getMASTNLogN(tree1, tree2).getSize() : getMASTNSquared(tree1, tree2).getNumberOfExternalNodes();
                    System.out.println("MAST size is " + resultSize);
                    break;
                case "MAST":
                    System.out.println("Constructing the MAST...");
                    tree1 = foresterNewickParser.parseNewickFileSingleTree(tree1Path);
                    tree2 = foresterNewickParser.parseNewickFileSingleTree(tree2Path);
                    result = nlogn ? getMASTNLogN(tree1, tree2).getTree() : getMASTNSquared(tree1, tree2);
                    Archaeopteryx.createApplication(result);
                    break;
                case "SHOW_TREES":
                    tree1 = foresterNewickParser.parseNewickFileSingleTree(tree1Path);
                    tree2 = foresterNewickParser.parseNewickFileSingleTree(tree2Path);
                    Archaeopteryx.createApplication(tree1);
                    Archaeopteryx.createApplication(tree2);
                    break;
                case "SWAP":
                    nlogn = !nlogn;
                    currentAlgorithmString = nlogn ? NLOGN_ALG_STRING : NSQUARED_ALG_STRING;
                    System.out.println(currentAlgorithmString);
                case "Q":
                    break;
                default:
                    System.out.println("Invalid command!\n"+currentAlgorithmString + INSTRUCTIONS);
            }
        }
    }

    private static MAST.TreeAndSizePair getMASTNLogN(Phylogeny tree1, Phylogeny tree2){

        Map<String, Integer> namesToNumbers = new HashMap<>();
        List<String> numbersToNames = new ArrayList<>();

        // Change names to numbers
        PhylogenyNodeIterator tree2LeafIterator = tree2.iteratorExternalForward();
        for (int i = 0 ; tree2LeafIterator.hasNext() ; i++){
            PhylogenyNode currentLeaf = tree2LeafIterator.next();
            String name = currentLeaf.getName();
            namesToNumbers.put(name, i);
            numbersToNames.add(name);
            currentLeaf.setName(i + "");
        }
        PhylogenyNodeIterator tree1LeafIterator = tree1.iteratorExternalForward();
        while (tree1LeafIterator.hasNext()){
            PhylogenyNode currentLeaf = tree1LeafIterator.next();
            String name = currentLeaf.getName();
            currentLeaf.setName(namesToNumbers.get(name) + "");
        }

        MAST mastFinder = new MAST();
        MAST.TreeAndSizePair mastPair = mastFinder.getMAST(tree1, tree2, false);

        Phylogeny mast = mastPair.getTree();
        PhylogenyNodeIterator mastLeafIterator = mast.iteratorExternalForward();
        while (mastLeafIterator.hasNext()){
            PhylogenyNode currentLeaf = mastLeafIterator.next();
            int number = Integer.parseInt(currentLeaf.getName());
            currentLeaf.setName(numbersToNames.get(number));
        }

        return mastPair;
    }

    private static Phylogeny getMASTNSquared(Phylogeny tree1, Phylogeny tree2){
        n_squared.MAST mastFinder = new n_squared.MAST();
        return mastFinder.getMAST(tree1, tree2);
    }
}
