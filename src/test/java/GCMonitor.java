import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

public class GCMonitor {
    private long previousTotalGarbageCollections = 0;
    private long previousTotalGarbageCollectingTime = 0;

    public GCMonitor(){
        getNumberOfGarbageCollectionsSinceLastMeasurement();
        getTimeUsedOnGarbageCollectingSinceLastMeasurement();
    }

    public long getNumberOfGarbageCollectionsSinceLastMeasurement() {
        long totalGarbageCollections = 0;
        long garbageCollectionTime = 0;

        for(GarbageCollectorMXBean gc :
                ManagementFactory.getGarbageCollectorMXBeans()) {

            long count = gc.getCollectionCount();

            if(count >= 0) {
                totalGarbageCollections += count;
            }

            long time = gc.getCollectionTime();

            if(time >= 0) {
                garbageCollectionTime += time;
            }
        }

        long result = totalGarbageCollections - previousTotalGarbageCollections;
        previousTotalGarbageCollections = totalGarbageCollections;
        return result;
//            System.out.println(totalGarbageCollections);
//        System.out.println("Total Garbage Collection Time (ms): "
//                + garbageCollectionTime);
    }

    public long getTimeUsedOnGarbageCollectingSinceLastMeasurement() { // in ms
        long totalGarbageCollectingTime = 0;

        for(GarbageCollectorMXBean gc :
                ManagementFactory.getGarbageCollectorMXBeans()) {

            long time = gc.getCollectionTime();

            if(time >= 0) {
                totalGarbageCollectingTime += time;
            }
        }

        long result = totalGarbageCollectingTime - previousTotalGarbageCollectingTime;
        previousTotalGarbageCollectingTime = totalGarbageCollectingTime;
        return result;
    }
}
