package Utilities;

import org.forester.io.writers.PhylogenyWriter;
import org.forester.phylogeny.Phylogeny;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Nikolaj on 10-02-2016.
 */
public class PhylogenyParser {

    public void toNewick(Phylogeny phylogeny, String filePath, boolean writeDistancesToParent) {
        final PhylogenyWriter writer = new PhylogenyWriter();
        final File outfile = new File(filePath+".new");
        try {
            writer.toNewHampshire(phylogeny, writeDistancesToParent, true, outfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toNewick(Phylogeny phylogeny1, Phylogeny phylogeny2, String filePath, boolean writeDistancesToParent) {
        final PhylogenyWriter writer = new PhylogenyWriter();
        final File outfile = new File(filePath+".new");
        ArrayList<Phylogeny> trees = new ArrayList<>();
        trees.add(phylogeny1);
        trees.add(phylogeny2);
        try {
            writer.toNewHampshire(trees, writeDistancesToParent, true, outfile, "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ForesterNewickParser foresterNewickParser = new ForesterNewickParser();
        PhylogenyParser phylogenyParser = new PhylogenyParser();

        Phylogeny phylogeny1 = PhylogenyGenerator.generateRandomTree(2000, true);
        phylogenyParser.toNewick(phylogeny1, "bigOne", true);

        //Phylogeny phylogeny2 = foresterNewickParser.parseNewickFileSingleTree("trees\\random\\something.new");
        //foresterNewickParser.displayPhylogeny(phylogeny2);
    }
}
