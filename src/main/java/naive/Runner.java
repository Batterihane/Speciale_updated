package naive;

import Utilities.ForesterNewickParser;
import Utilities.PhylogenyGenerator;
import org.forester.phylogeny.Phylogeny;

/**
 * Created by Thomas on 01-03-2016.
 */
public class Runner {

    public static void main(String[] args) {
        ForesterNewickParser foresterNewickParser = new ForesterNewickParser();

        Phylogeny tree1 = PhylogenyGenerator.generateRandomTree(10, true);
        Phylogeny tree2 = PhylogenyGenerator.generateRandomTree(10, false);
        foresterNewickParser.displayPhylogeny(tree1);
        foresterNewickParser.displayPhylogeny(tree2);


        MAST mast = new MAST();
        foresterNewickParser.displayPhylogeny(mast.getMAST(tree1, tree2));

//        for (int i = 1; i <= (int)Math.pow(2, 5) - 1; i++) {
//            System.out.print(Integer.toBinaryString(i));
//            BitSet bitSet = BitSet.valueOf(new long[] { i });
//            System.out.println(":   " + bitSet);
//        }

//        List<PhylogenyNode> leaves = tree1.getExternalNodes();
//        ArrayList<String> leavesToRemove = new ArrayList<>();
//        leavesToRemove.add(leaves.get(0).getName());
//        leavesToRemove.add(leaves.get(2).getName());
//        leavesToRemove.add(leaves.get(4).getName());
//        leavesToRemove.add(leaves.get(6).getName());
//        leavesToRemove.add(leaves.get(8).getName());


    }

}
