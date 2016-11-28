package pt.ist.rc.dss;

import pt.ist.rc.dss.analytics.DSSComputation;
import pt.ist.rc.dss.analytics.DSSVertexState;
import pt.ist.rc.paragraph.analytics.ConnectedComponentsComputation;
import pt.ist.rc.paragraph.computation.ComputationConfig;
import pt.ist.rc.paragraph.loader.GraphLoader;
import pt.ist.rc.paragraph.model.Edge;
import pt.ist.rc.paragraph.model.Graph;
import pt.ist.rc.paragraph.model.Vertex;

import java.io.IOException;
import java.util.*;

/**
 * Created by Pedro Joaquim on 14-11-2016
 */
public class DSSimulator {


    public final static double INFECTION_RATE = 0.15;
    public final static double RECOVERY_RATE = 0.4;
    public final static int VACCINES_NUMBER = 0;
    public final static int INITIAL_GROUP_TO_VACCINATE = 0;
    public final static int NUMBER_OF_FRIENDS_TO_VACCINATE = 1;
    public final static int NUMBER_OF_SUPERTEPS_OF_INFECTION = 30;
    public final static int NUMBER_OF_INITIAL_INFECTED = 1;
    public final static int NUMBER_OF_SIMULATIONS = 2;

    public static void main(String[] args) throws IOException {

        long startTime = System.currentTimeMillis();

        Graph<Void, Double> graph = new GraphLoader<Void, Double>().fromFile("D:\\GitHub\\Disease-Spreading-Simulator\\datasets\\soc-pokec-relationships.txt", x -> null, x -> null, "directed");

        int recovered[] = new int[NUMBER_OF_SIMULATIONS];


        for (int i = 0; i < NUMBER_OF_SIMULATIONS ; i++) {

            DSSComputation dssComputation = new DSSComputation(graph, new ComputationConfig().setNumWorkers(4),
                    VACCINES_NUMBER,
                    INITIAL_GROUP_TO_VACCINATE,
                    NUMBER_OF_FRIENDS_TO_VACCINATE,
                    INFECTION_RATE,
                    RECOVERY_RATE,
                    NUMBER_OF_SUPERTEPS_OF_INFECTION,
                    NUMBER_OF_INITIAL_INFECTED);

            dssComputation.execute();

            List<DSSVertexState> vertexComputationalValues = dssComputation.getVertexComputationalValues();

            dssComputation = null;

            int recoveredTMP = 0;

            for (int j = 0; j < graph.getVertices().size(); j++) {
                if(vertexComputationalValues.get(j).isRecovered()){
                    recoveredTMP++;
                }
            }

            recovered[i] = recoveredTMP;
            System.out.println("RECOVERED: " + recoveredTMP + " (" + recoveredTMP/graph.getVertices().size()*100 + "%)");
        }

        System.out.println("Starting Analytics");


        double recoveredAVG = 0;

        for (int i = 0; i < NUMBER_OF_SIMULATIONS; i++) {
            recoveredAVG += recovered[i];
        }

        recoveredAVG = recoveredAVG/NUMBER_OF_SIMULATIONS;

        System.out.println("Recovered AVG = " + recoveredAVG + " (" + recoveredAVG/graph.getVertices().size()*100 +" %)");

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("time = " + elapsedTime);
    }

    //remove
    public static Graph<Void, Void> loadGraph() {
        List<Edge<Void>> vertex0Edges = Arrays.asList(
                new Edge.Builder<Void>().targetIdx(1).build(),
                new Edge.Builder<Void>().targetIdx(2).build()
        );

        List<Edge<Void>> vertex1Edges = Arrays.asList(
                new Edge.Builder<Void>().targetIdx(0).build(),
                new Edge.Builder<Void>().targetIdx(2).build()
        );

        List<Edge<Void>> vertex2Edges = Arrays.asList(
                new Edge.Builder<Void>().targetIdx(0).build(),
                new Edge.Builder<Void>().targetIdx(1).build(),
                new Edge.Builder<Void>().targetIdx(3).build(),
                new Edge.Builder<Void>().targetIdx(4).build()
        );

        List<Edge<Void>> vertex3Edges = Arrays.asList(
                new Edge.Builder<Void>().targetIdx(2).build(),
                new Edge.Builder<Void>().targetIdx(4).build()
        );

        List<Edge<Void>> vertex4Edges = Arrays.asList(
                new Edge.Builder<Void>().targetIdx(3).build(),
                new Edge.Builder<Void>().targetIdx(2).build()
        );


        List<Vertex<Void, Void>> vertices = Arrays.asList(
                new Vertex.Builder<Void, Void>().addAllEdges(vertex0Edges).build(),
                new Vertex.Builder<Void, Void>().addAllEdges(vertex1Edges).build(),
                new Vertex.Builder<Void, Void>().addAllEdges(vertex2Edges).build(),
                new Vertex.Builder<Void, Void>().addAllEdges(vertex3Edges).build(),
                new Vertex.Builder<Void, Void>().addAllEdges(vertex4Edges).build()
        );

        return new Graph.SimpleBuilder<Void, Void>().addVertices(vertices).build();
    }


}
