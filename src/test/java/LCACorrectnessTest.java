import Utilities.ConstantTimeLCA;
import Utilities.DataObjects.NodeDataReference;
import Utilities.LCA;
import Utilities.PhylogenyGenerator;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by Thomas on 10-03-2016.
 */
public class LCACorrectnessTest {

    @Test
    public void testGetLCA() {
        for (int i = 0; i < 1000; i++) {
            Phylogeny tree = PhylogenyGenerator.generateRandomTree(1000, true);
            addNodeDataReferences(tree);

            PhylogenyNode node1 = tree.getNode("0");
            PhylogenyNode node2 = tree.getNode("10");
            ConstantTimeLCA lcaFinder1 = new ConstantTimeLCA(tree);
            LCA lcaFinder2 = new LCA(tree);
            PhylogenyNode result1 = lcaFinder1.getLCA(node1, node2);
            PhylogenyNode result2 = lcaFinder2.getLCA(node1, node2);
            assertEquals(result1, result2, "Results not matching on run " + i);
        }
    }

    private static void addNodeDataReferences(Phylogeny tree) {
        PhylogenyNodeIterator iterator = tree.iteratorPostorder();
        while (iterator.hasNext()){
            PhylogenyNode currentNode = iterator.next();
            currentNode.getNodeData().addReference(new NodeDataReference());
        }
    }
}
