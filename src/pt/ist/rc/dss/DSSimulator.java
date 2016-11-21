package pt.ist.rc.dss;

import pt.ist.rc.dss.analytics.DSSComputation;
import pt.ist.rc.dss.analytics.DSSVertexState;
import pt.ist.rc.paragraph.computation.ComputationConfig;
import pt.ist.rc.paragraph.loader.GraphLoader;
import pt.ist.rc.paragraph.model.Edge;
import pt.ist.rc.paragraph.model.Graph;
import pt.ist.rc.paragraph.model.Vertex;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Pedro Joaquim on 14-11-2016
 */
public class DSSimulator {


    public static void main(String[] args) throws IOException {


        Graph<Void, Double> graph = new GraphLoader<Void, Double>().fromFile("datasets/huge.gml", x -> null, x -> null);

        DSSComputation dssComputation = new DSSComputation(graph, new ComputationConfig().setNumWorkers(4), 500, 5, 4, 0.5, 0.0);
        dssComputation.execute();

        double numInfected = 0;
        double numVertices = dssComputation.getVertexComputationalValues().size();

        for (DSSVertexState state:
             dssComputation.getVertexComputationalValues()) {

            if(state.isInfected()){
                numInfected++;
            }
        }

        System.out.println("Total Number of Vertices: " + numVertices);
        System.out.println("Total Number of Infected: " +  numInfected + " (" + numInfected/numVertices*100 + "%)");
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
