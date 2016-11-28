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


    public final static double INFECTION_RATE = 0.5;
    public final static double RECOVERY_RATE = 0.05;
    public final static int VACCINES_NUMBER = 0;
    public final static int INITIAL_GROUP_TO_VACCINATE = 0;
    public final static int NUMBER_OF_FRIENDS_TO_VACCINATE = 1;
    public final static int NUMBER_OF_SUPERTEPS_OF_INFECTION = 30;
    public final static int NUMBER_OF_INITIAL_INFECTED = 1;
    public final static int NUMBER_OF_SIMULATIONS = 15;

    public static void main(String[] args) throws IOException {

        long startTime = System.currentTimeMillis();

        Graph<Void, Double> graph = new GraphLoader<Void, Double>().fromFile("datasets/soc-pokec-relationships.txt", x -> null, x -> null, "directed");

        DSSVertexState[][] results = new DSSVertexState[graph.getVertices().size()][NUMBER_OF_SIMULATIONS];


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

            for (int j = 0; j < graph.getVertices().size(); j++) {
                results[j][i] = vertexComputationalValues.get(j);
            }
        }

        System.out.println("Starting Analytics");

        double[] resultProbability = new double[graph.getVertices().size()];

        for (int i = 0; i < graph.getVertices().size(); i++) {

            double numInfectedStates = 0;

            for (int j = 0; j < NUMBER_OF_SIMULATIONS; j++) {

                if(results[i][j].isInfected() || results[i][j].isRecovered()){
                    numInfectedStates++;
                }
            }

            resultProbability[i] = numInfectedStates/NUMBER_OF_SIMULATIONS;
        }

        int[] resultsPerClass = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        for (double p : resultProbability) {

            double prob = p * 100;
            int index = 0;

            if(prob >= 0 && prob < 10){
                index = 0;
            } else if(prob < 20) {
                index = 1;
            } else if(prob < 30){
                index = 2;
            } else if(prob < 40){
                index = 3;
            } else if(prob < 50){
                index = 4;
            } else if(prob < 60){
                index = 5;
            } else if(prob < 70){
                index = 6;
            } else if(prob < 80){
                index = 7;
            } else if(prob < 90){
                index = 8;
            } else if(prob <= 100){
                index = 9;
            }

            resultsPerClass[index]++;

        }

        double numVertices = graph.getVertices().size();

        System.out.println("[0%, 10%[ ---> " + resultsPerClass[0] + " (" + ((double)resultsPerClass[0]/numVertices) * 100.0 + " %)");
        System.out.println("[10%, 20%[ ---> " + resultsPerClass[1] + " (" + ((double)resultsPerClass[1]/numVertices) * 100.0 + " %)");
        System.out.println("[20%, 30%[ ---> " + resultsPerClass[2] + " (" + ((double)resultsPerClass[2]/numVertices) * 100.0 + " %)");
        System.out.println("[30%, 40%[ ---> " + resultsPerClass[3] + " (" + ((double)resultsPerClass[3]/numVertices) * 100.0 + " %)");
        System.out.println("[40%, 50%[ ---> " + resultsPerClass[4] + " (" + ((double)resultsPerClass[4]/numVertices) * 100.0 + " %)");
        System.out.println("[50%, 60%[ ---> " + resultsPerClass[5] + " (" + ((double)resultsPerClass[5]/numVertices) * 100.0 + " %)");
        System.out.println("[60%, 70%[ ---> " + resultsPerClass[6] + " (" + ((double)resultsPerClass[6]/numVertices) * 100.0 + " %)");
        System.out.println("[70%, 80%[ ---> " + resultsPerClass[7] + " (" + ((double)resultsPerClass[7]/numVertices) * 100.0 + " %)");
        System.out.println("[80%, 90%[ ---> " + resultsPerClass[8] + " (" + ((double)resultsPerClass[8]/numVertices) * 100.0 + " %)");
        System.out.println("[90%, 100%] ---> " + resultsPerClass[9] + " (" + ((double)resultsPerClass[9]/numVertices) * 100.0 + " %)");


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
