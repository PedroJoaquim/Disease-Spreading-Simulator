package pt.ist.rc.dss;

import pt.ist.rc.dss.analytics.DSSComputation;
import pt.ist.rc.dss.analytics.DSSVertexInfo;
import pt.ist.rc.paragraph.computation.ComputationConfig;
import pt.ist.rc.paragraph.loader.GraphLoader;
import pt.ist.rc.paragraph.model.Graph;


import java.io.IOException;
import java.util.*;

/**
 * Created by Pedro Joaquim on 14-11-2016
 */
public class DSSimulator {


    public final static double[] INFECTION_RATE_ARRAY = {.005, .01, .015, .02, .025, .03, .035, .04, .045, .05,  .055, .06, .065, .07, .075, .08, .085, .09, .095, .1, .12, .16, .2, .25, .3, .4, .5, .6, .7, .8, .9, 1};
    public final static double RECOVERY_RATE = 0.40;
    public final static int VACCINES_NUMBER = 350000;
    public final static int INITIAL_GROUP_TO_VACCINATE = 100000;
    public final static int NUMBER_OF_INITIAL_INFECTED = 1;
    public final static int NUMBER_OF_SIMULATIONS = 50;

    public static void main(String[] args) throws IOException {

        Graph<Void, Double> graph = new GraphLoader<Void, Double>().fromFile("D:\\Documents\\GitHub\\Disease-Spreading-Simulator\\datasets\\soc-pokec-relationships.txt", x -> null, x -> null, "directed");

        int recovered[] = new int[NUMBER_OF_SIMULATIONS];

        for (int y = 0; y < INFECTION_RATE_ARRAY.length; y++) {

            System.out.println("SIMULATING WITH INFECTION RATE = " + INFECTION_RATE_ARRAY[y]);

            for (int i = 0; i < NUMBER_OF_SIMULATIONS ; i++) {

                DSSComputation dssComputation = new DSSComputation(graph, new ComputationConfig().setNumWorkers(4),
                        VACCINES_NUMBER,
                        INITIAL_GROUP_TO_VACCINATE,
                        INFECTION_RATE_ARRAY[y],
                        RECOVERY_RATE,
                        NUMBER_OF_INITIAL_INFECTED);

                dssComputation.execute();

                List<DSSVertexInfo> vertexComputationalValues = dssComputation.getVertexComputationalValues();

                int recoveredTMP = 0;

                for (int j = 0; j < graph.getVertices().size(); j++) {
                    if(vertexComputationalValues.get(j).getState().isRecovered()){
                        recoveredTMP++;
                    }
                }

                recovered[i] = recoveredTMP;
            }

            double recoveredAVG = 0;

            for (int i = 0; i < NUMBER_OF_SIMULATIONS; i++) {
                recoveredAVG += recovered[i];
            }

            recoveredAVG /= NUMBER_OF_SIMULATIONS;

            System.out.println("********************************************************************************************");
            System.out.println("Infection Rate = " + INFECTION_RATE_ARRAY[y]);
            System.out.println("Recovered AVG = " + recoveredAVG + " (" + recoveredAVG/graph.getVertices().size()*100 +" %)");
            System.out.println("point: " + INFECTION_RATE_ARRAY[y] + ", " + recoveredAVG/graph.getVertices().size()*100);
            System.out.println("********************************************************************************************");
        }
    }
}
