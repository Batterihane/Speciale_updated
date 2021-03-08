package Utilities;

import org.forester.archaeopteryx.Archaeopteryx;
import org.forester.io.parsers.PhylogenyParser;
import org.forester.io.parsers.util.ParserUtils;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyMethods;

import java.io.File;
import java.io.IOException;

/**
 * Created by Nikolaj on 14-11-2015.
 */
public class ForesterNewickParser {

    public static void main(String[] args) throws IOException {
        ForesterNewickParser foresterNewickParser = new ForesterNewickParser();

        Phylogeny tree = foresterNewickParser.parseNewickFileSingleTree("treess\\test.new");
        foresterNewickParser.displayPhylogeny(tree);
    }

    public void displayPhylogeny(Phylogeny tree)
    {
        Archaeopteryx.createApplication(tree);
    }

    public Phylogeny parseNewickFileSingleTree(String filePath)
    {
        PhylogenyParser parserDependingOnFileType;
        File treeFile;

        try {
            treeFile = new File(filePath);
            parserDependingOnFileType = ParserUtils.createParserDependingOnFileType(treeFile, true);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Phylogeny[] phys;
        Phylogeny tree;
        try {
            phys = PhylogenyMethods.readPhylogenies(parserDependingOnFileType, treeFile);
            tree = phys[0];
        }
        catch ( final IOException e ) {
            e.printStackTrace();
            return null;
        }

        return tree;
    }


    public Pair<Phylogeny, Phylogeny> parseNewickFileTwoTrees(String filePath)
    {
        PhylogenyParser parserDependingOnFileType;
        File treeFile;

        try {
            treeFile = new File(filePath);
            parserDependingOnFileType = ParserUtils.createParserDependingOnFileType(treeFile, true);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Phylogeny[] phys;
        Pair<Phylogeny, Phylogeny> trees;
        try {
            phys = PhylogenyMethods.readPhylogenies(parserDependingOnFileType, treeFile);

            trees = new Pair<>(phys[0], phys[1]);
        }
        catch ( final IOException e ) {
            e.printStackTrace();
            return null;
        }

        return trees;
    }


    //Random shit
        /*
        String nhx = "(mammal,(turtle,rayfinfish,(frog,salamander)))";
        Phylogeny phylogeny = new Phylogeny();

        final File treefile = new File("C:\\Users\\Nikolaj\\BioTreeAndSeq\\BioTrees\\trees\\quickTree\\testnewick.new");
        PhylogenyParser parserDependingOnFileType = ParserUtils.createParserDependingOnFileType(treefile, true);

        Phylogeny[] phys = null;
        try {
            phys = PhylogenyMethods.readPhylogenies(parserDependingOnFileType, treefile);
        }
        catch ( final IOException e ) {
            e.printStackTrace();
        }
        //org.forester.phylogeny.Phylogeny.class.newInstance().
        //Phylogeny ph = org.forester.phylogeny.Phylogeny.class.newInstance().
        Phylogeny tree = phys[0];
        PhylogenyNode root = tree.getRoot();
        System.out.println(tree.isRerootable());
        //tree.reRoot(tree.getNode(0));
        //tree.reRoot(tree.getNode("turtle"));
        PhylogenyNodeIterator phylogenyNodeIterator = tree.iteratorPreorder();

            while(phylogenyNodeIterator.hasNext()) {
            System.out.println(phylogenyNodeIterator.next().toString());
        }


        List<PhylogenyNode> allDescendants = tree.getNode(0).getAllDescendants();
        allDescendants.forEach(node -> System.out.println(node.getName()));
        System.out.println(allDescendants.size());
        MainFrame application = Archaeopteryx.createApplication(tree);
      */
}
