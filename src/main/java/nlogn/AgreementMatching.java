package nlogn;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Thomas on 07-04-2016.
 */
public class AgreementMatching {
    ProperCrossing properCrossing;
    WhiteEdgeSequence whiteEdges; // top to bottom order
    int weight;
    GraphEdge topmostEdge;

    public AgreementMatching(ProperCrossing properCrossing, WhiteEdgeSequence whiteEdges, int weight) {
        this.properCrossing = properCrossing;
        this.whiteEdges = whiteEdges;
        this.weight = weight;
        if(whiteEdges == null)
            topmostEdge = properCrossing.getGreenEdge();
        else
            topmostEdge = whiteEdges.getFirstEdge();
    }

    public ProperCrossing getProperCrossing() {
        return properCrossing;
    }

    public WhiteEdgeSequence getWhiteEdges() {
        return whiteEdges;
    }

    public int getWeight() {
        return weight;
    }

//    public void addWhiteEdge(GraphEdge whiteEdge) {
//        whiteEdges.add(whiteEdge);
//        weight += whiteEdge.getWhiteWeight();
//        topmostEdge = whiteEdge;
//    }

    public GraphEdge getTopmostEdge(){
        return topmostEdge;
    }

    @Override
    public String toString() {
        String result = "White edges: ";
        WhiteEdgeSequence currentWhiteEdges = whiteEdges;
        while (currentWhiteEdges != null){
            result += currentWhiteEdges.getFirstEdge().toString() + ", ";
            currentWhiteEdges = currentWhiteEdges.getPreviousEdge();
        }
        result += " Proper crossing: " + properCrossing.toString();
        return result;
    }

//    @Override
//    public Iterator<GraphEdge> iterator() {
//        return new WhiteEdgeIterator();
//    }
//
//    public class WhiteEdgeIterator implements Iterator<GraphEdge> {
//        int currentIndex = -1;
//
//        public boolean hasNext() {
//            return currentIndex < numOfWhiteEdges - 1;
//        }
//
//        public GraphEdge next() {
//            currentIndex++;
//            return whiteEdges.get(currentIndex);
//        }
//
//        public void remove() {
//            throw new UnsupportedOperationException();
//        }
//    }
}

