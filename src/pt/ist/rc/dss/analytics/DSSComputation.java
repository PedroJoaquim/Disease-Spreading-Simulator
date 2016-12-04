package pt.ist.rc.dss.analytics;

import pt.ist.rc.paragraph.computation.ComputationConfig;
import pt.ist.rc.paragraph.computation.ComputationalVertex;
import pt.ist.rc.paragraph.computation.VertexCentricComputation;
import pt.ist.rc.paragraph.model.Edge;
import pt.ist.rc.paragraph.model.Graph;

import java.util.*;

/**
 * Created by Pedro Joaquim on 14-11-2016
 */

public class DSSComputation extends VertexCentricComputation<Object, Object, DSSVertexInfo, String> {

    private int numberOfInitialInfected;
    private double infectionRate;
    private double recoveryRate;
    private int vaccinesNumber;
    private int vaccinesLastStep;

    private Set<Integer> initialGroupIDs;

    private static final String COMPUTATION_PHASE = "computation";

    private static final int VACCINATION_PHASE = 1;
    private static final int INFECTION_PHASE = 2;

    public DSSComputation(Graph<?, ?> graph, ComputationConfig config,
                          int vaccinesNumber,
                          int initialGroupSize,
                          double infectionRate,
                          double recoveryRate,
                          int numberOfInitialInfected) {

        super(graph, config);

        assert initialGroupSize > 0 && initialGroupSize <= graph.getVertices().size() && initialGroupSize < vaccinesNumber;
        assert vaccinesNumber > 0;
        assert numberOfInitialInfected > 0;

        /*for random and most popular friend*/
        this.vaccinesNumber = vaccinesNumber - initialGroupSize;
        this.vaccinesLastStep = vaccinesNumber - initialGroupSize;

        /*for acquaintance*/
        //this.vaccinesNumber = vaccinesNumber;
        //this.vaccinesLastStep = vaccinesNumber;

        this.initialGroupIDs = randomSet(graph.getVertices().size(), initialGroupSize); //random group of vaccinated people
        this.recoveryRate = recoveryRate;
        this.infectionRate = infectionRate;
        this.numberOfInitialInfected = numberOfInitialInfected;
    }

    @Override
    protected DSSVertexInfo initializeValue(int i) {
        /* for random and most popular friend*/
        return this.initialGroupIDs.contains(i) ? new DSSVertexInfo(DSSVertexState.IMMUNE, 0) : new DSSVertexInfo(DSSVertexState.SUSCEPTIBLE, 0);

        /*for acquaintance*/
        //return  new DSSVertexInfo(DSSVertexState.SUSCEPTIBLE, 0);
    }

    @Override
    protected void compute(ComputationalVertex<?, ?, DSSVertexInfo, String> computationalVertex) {

        int computationPhase = (Integer) getGlobalValue(COMPUTATION_PHASE);

        if (computationPhase == VACCINATION_PHASE) {
            computeFriendsWithMostConnectionsVaccinationPhase(computationalVertex);
            //computeAcquaitanceVaccinationPhase(computationalVertex);
        } else {
            computeInfectionPhase(computationalVertex);
        }

    }

    @Override
    protected void masterCompute(List<ComputationalVertex<?, ?, DSSVertexInfo, String>> list, HashMap<String, Object> globalValues) {


        int computationPhase = (Integer) globalValues.get(COMPUTATION_PHASE);

        if(computationPhase == VACCINATION_PHASE){


            //cant vaccinate more
            if(getSuperStep() > 10 && this.vaccinesLastStep == this.vaccinesNumber){
                System.out.println("GIVING RANDOM VACCINES: " + vaccinesNumber);

                while (this.vaccinesNumber > 0){
                    vaccinateRandomNode(list);
                    this.vaccinesNumber--;
                }
            }

            //vaccines ended
            if(this.vaccinesNumber == 0) {

                globalValues.put(COMPUTATION_PHASE, INFECTION_PHASE);

                while (this.numberOfInitialInfected > 0){
                    infectRandomNode(list);
                    this.numberOfInitialInfected--;
                }

                for (ComputationalVertex<?, ?, DSSVertexInfo, String> vertex : list) {
                    if(!vertex.getComputationalValue().getState().isInfected()){
                        vertex.voteToHalt();
                    }
                    else{
                        sendMessageTo(vertex.getId(), "1");
                    }
                }

            }

            this.vaccinesLastStep = this.vaccinesNumber;


        }
    }

    private void vaccinateRandomNode(List<ComputationalVertex<?, ?, DSSVertexInfo, String>> list){
        int rndVaccinated;
        ComputationalVertex<?, ?, DSSVertexInfo, String> cVertex;

        do{
            Random rnd = new Random();
            rndVaccinated = rnd.nextInt(getNumVertices());
            cVertex = list.get(rndVaccinated);
        } while(!cVertex.getComputationalValue().getState().isSusceptible());

        cVertex.getComputationalValue().setState(DSSVertexState.IMMUNE);
    }

    private void infectRandomNode(List<ComputationalVertex<?, ?, DSSVertexInfo, String>> list){

        int rndInfected;
        ComputationalVertex<?, ?, DSSVertexInfo, String> cVertex;

        do{
            Random rnd = new Random();
            rndInfected = rnd.nextInt(getNumVertices());
            cVertex = list.get(rndInfected);
        } while(!cVertex.getComputationalValue().getState().isSusceptible() || cVertex.getNumberOutEdges() < 5);

        cVertex.getComputationalValue().setState(DSSVertexState.INFECTED);
        sendMessageTo(cVertex.getId(), "1");

    }

    private void computeInfectionPhase(ComputationalVertex<?, ?, DSSVertexInfo, String> computationalVertex) {


        if(computationalVertex.getComputationalValue().getState().isInfected()){

            //infect others
            Iterator<? extends Edge<?>> outEdgesIterator = computationalVertex.getOutEdgesIterator();

            while (outEdgesIterator.hasNext()){
                Edge<?> edge = outEdgesIterator.next();

                if(getBooelanWithProbability(this.infectionRate)){
                    sendMessageTo(edge.getTargetIdx(), "2");
                }
            }

            if(getBooelanWithProbability(this.recoveryRate)){
                //get better
                computationalVertex.getComputationalValue().setState(DSSVertexState.RECOVERED);
                computationalVertex.voteToHalt();

            }

        } else if(computationalVertex.getComputationalValue().getState().isSusceptible() && computationalVertex.getMessages().size() >= 1){
            //got infected

            computationalVertex.getComputationalValue().setState(DSSVertexState.INFECTED);

        } else {
            computationalVertex.voteToHalt();
        }

    }

    private void computeAcquaitanceVaccinationPhase(ComputationalVertex<?, ?, DSSVertexInfo, String> computationalVertex) {

        if(this.initialGroupIDs.contains(computationalVertex.getId()) && computationalVertex.getNumberOutEdges() > 0){

            Iterator<? extends Edge<?>> outEdgesIterator = computationalVertex.getOutEdgesIterator();

            Random rnd = new Random();
            int rndVaccinated = rnd.nextInt(computationalVertex.getNumberOutEdges());
            int i = 0;

            while (outEdgesIterator.hasNext()){
                Edge<?> next = outEdgesIterator.next();

                if(i == rndVaccinated){
                    sendMessageTo(next.getTargetIdx(), "1");
                }

                i++;
            }
        }

        if(computationalVertex.getMessages().size() > 0 && canDecreaseVaccinesNumber(1) == 1){
            computationalVertex.getComputationalValue().setState(DSSVertexState.IMMUNE);
        }
    }

    private void computeFriendsWithMostConnectionsVaccinationPhase(ComputationalVertex<?, ?, DSSVertexInfo, String> computationalVertex) {

        if(getSuperStep() == 0){
            sendMessageToAllOutNeighbors(computationalVertex, computationalVertex.getNumberOutEdges() + "#" + computationalVertex.getId());

        }
        if(getSuperStep() == 1){

            int largest = 0;

            for (String msg : computationalVertex.getMessages()) {
                String[] splited = msg.split("#");

                int current = Integer.valueOf(splited[0]);

                if(current > largest){
                    computationalVertex.getComputationalValue().addNeighborInfo(splited[1], splited[0]);
                    largest = current;
                }
            }
        }
        else{

            if(this.initialGroupIDs.contains(computationalVertex.getId())){

                List<Integer[]> idOfMostPopularNeighbor = computationalVertex.getComputationalValue().getIdOfMostPopularNeighbor();

                if(idOfMostPopularNeighbor.size() > 0){
                    sendMessageTo(idOfMostPopularNeighbor.get(0)[0], "1");
                }
            }
            else if(computationalVertex.getMessages().size() > 0){

                if(computationalVertex.getComputationalValue().getState().isSusceptible()){
                    if(canDecreaseVaccinesNumber(1) == 1) {
                        computationalVertex.getComputationalValue().setState(DSSVertexState.IMMUNE);
                    }
                }

                List<Integer[]> idOfMostPopularNeighbor = computationalVertex.getComputationalValue().getIdOfMostPopularNeighbor();

                if(idOfMostPopularNeighbor.size() > 0){
                    sendMessageTo(idOfMostPopularNeighbor.get(0)[0], "1");
                }
            }
            else{
                computationalVertex.voteToHalt();
            }
        }

    }


    @Override
    protected void initializeGlobalObjects(HashMap<String, Object> globalValues) {
        globalValues.put(COMPUTATION_PHASE, VACCINATION_PHASE);
    }

    /*
     * Function that workers will invoke in order to check if they can use a requested number of vaccines
     */

    private synchronized int canDecreaseVaccinesNumber(int n) {

        this.vaccinesNumber = Math.max(0, this.vaccinesNumber - n);

        return this.vaccinesNumber >= n ? n : this.vaccinesNumber;
    }

    /*
     * generate a set of random ints between 0 (inclusive) and max (exclusive) with size n
     */

    private Set<Integer> randomSet(int max, int n){

        int generatedNumbers = 0;
        Random rnd = new Random();
        Set<Integer> result = new HashSet<>();

        while (generatedNumbers < n) {
            int randomInt = rnd.nextInt(max);
            if (!result.contains(randomInt)) {
                result.add(randomInt);
                generatedNumbers++;
            }
        }

        return result;
    }

    private boolean getBooelanWithProbability(double probabilityTrue){
        return Math.random() >= 1.0 - probabilityTrue;
    }
}
