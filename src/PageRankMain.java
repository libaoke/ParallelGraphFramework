import algorithm.pagerank.original.PageRankDriver;
import graph.Graph;
import graph.GraphUtil;
import graph.sharedData.PageRankSharedData;

import java.util.concurrent.BrokenBarrierException;

public class PageRankMain
{
    /**
     * USER : Set the PageRank Configuration
     **/
    public static void main(String[] args) throws InterruptedException, BrokenBarrierException
    {
        final boolean isDirected = true;
        final boolean isWeighted = false;
        final boolean isInDegreeSorted = true;

        String inputFile = args[0];
        int numThreads = Integer.parseInt(args[1]);
        int asyncThreshold = Integer.parseInt(args[2]);
        int expOfTaskSize = Integer.parseInt(args[3]);
        String outputFile = args[4];
        int iteration = Integer.parseInt(args[5]);

        double dampingFactor = 0.85;

        Graph<PageRankSharedData> graph = Graph.getInstance(expOfTaskSize, isDirected, isWeighted);

        long start = System.currentTimeMillis();
        System.err.println("[DEBUG] Graph Loading... ");
        GraphUtil.load(graph, inputFile);
        graph.loadFinalize(asyncThreshold, PageRankSharedData.class, isInDegreeSorted);
        System.err.println("[DEBUG] Loading Time : " + (System.currentTimeMillis() - start) / 1000.0);

        PageRankDriver driver = new PageRankDriver(graph, dampingFactor, iteration, numThreads);

        /**     PageRank Start      **/
        double timeSum = 0;

        System.err.println("[DEBUG] PageRank Running ... ");
        final int numRun = 15;
        final int jitRun = 5;
        long[] elapsedTime = new long[numRun];
        for (int i = 0; i < numRun; i++) {
            driver.reset();
            start = System.currentTimeMillis();
            driver.run();
            elapsedTime[i] = System.currentTimeMillis() - start;
            System.err.println("[DEBUG] elapsed time for iteration" + i + " : " + ((elapsedTime[i]) / (1000.0)));

            if (i >= jitRun) {
                timeSum += (elapsedTime[i] / 1000.0);
            }
        }
//        driver.writePageRanks(outputFile);
        System.err.println("[DEBUG] PageRank Complete ");
        String averageTime = String.format("%.3f", (timeSum / (numRun - jitRun)));
        System.out.println(averageTime);

        System.exit(1);
    }
}
