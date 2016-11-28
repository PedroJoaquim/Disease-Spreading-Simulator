package pt.ist.rc.dss;

import pt.ist.rc.dss.analytics.DSSComputation;
import pt.ist.rc.dss.analytics.DSSVertexState;
import pt.ist.rc.paragraph.computation.ComputationConfig;
import pt.ist.rc.paragraph.model.Graph;

import java.util.List;

/**
 * Created by Pedro Joaquim on 26-11-2016
 */
public class DSSimulatorThread implements Runnable {


    public final static double INFECTION_RATE = 0.5;
    public final static double RECOVERY_RATE = 0.05;
    public final static int VACCINES_NUMBER = 0;
    public final static int INITIAL_GROUP_TO_VACCINATE = 0;
    public final static int NUMBER_OF_FRIENDS_TO_VACCINATE = 1;
    public final static int NUMBER_OF_SUPERTEPS_OF_INFECTION = 30;
    public final static int NUMBER_OF_INITIAL_INFECTED = 1;


    private final DSSVertexState[][] results;
    private final int from;
    private final int to;
    private final Graph<Void, Double> graph;

    public DSSimulatorThread(Graph<Void, Double> graph, DSSVertexState[][]results, int from, int to) {
        this.results = results;
        this.from = from;
        this.to = to;
        this.graph = graph;
    }

    @Override
    public void run() {


    }
}
