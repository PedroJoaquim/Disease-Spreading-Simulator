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

public class DSSComputation extends VertexCentricComputation<Object, Object, DSSVertexState, Integer> {

    private double infectionRate;
    private double recoveryRate;

    private int vaccinesNumber;
    private int numberOfFriendsToVaccinate;
    private int vaccinationEndSuperstep;

    private Set<Integer> initialGroupIDs;

    private static final String COMPUTATION_PHASE = "computation";
    private static final String VACCINES_AVAILABLE_PREVIOUS_STEP = "vaccines";

    private static final int VACCINATION_PHASE = 1;
    private static final int INFECTION_PHASE = 2;

    public DSSComputation(Graph<?, ?> graph, ComputationConfig config, int vaccinesNumber, int initialGroupSize, int numberOfFriendsToVaccinate, double infectionRate, double recoveryRate) {
        super(graph, config);

        assert initialGroupSize > 0 && initialGroupSize <= graph.getVertices().size() && initialGroupSize < vaccinesNumber;
        assert vaccinesNumber > 0;
        assert numberOfFriendsToVaccinate > 0;

        this.vaccinesNumber = vaccinesNumber;
        this.numberOfFriendsToVaccinate = numberOfFriendsToVaccinate;
        this.initialGroupIDs = randomSet(graph.getVertices().size(), initialGroupSize);
        this.recoveryRate = recoveryRate;
        this.infectionRate = infectionRate;
    }

    @Override
    protected DSSVertexState initializeValue(int i) {
        return this.initialGroupIDs.contains(i) ? DSSVertexState.IMMUNE : DSSVertexState.SUSCEPTIBLE;
    }

    @Override
    protected void compute(ComputationalVertex<?, ?, DSSVertexState, Integer> computationalVertex) {

        int computationPhase = (Integer) getGlobalValue(COMPUTATION_PHASE);

        if (computationPhase == VACCINATION_PHASE) {
            computeVaccinationPhase(computationalVertex);
        } else {
            computeInfectionPhase(computationalVertex);
        }

    }

    @Override
    protected void masterCompute(List<ComputationalVertex<?, ?, DSSVertexState, Integer>> list, HashMap<String, Object> globalValues) {

        if (getSuperStep() > 0) {

            int computationPhase = (Integer) globalValues.get(COMPUTATION_PHASE);

            //switch phases
            if ((computationPhase == VACCINATION_PHASE && this.vaccinesNumber == 0) ||
                    ((computationPhase == VACCINATION_PHASE)) && ((int) globalValues.get(VACCINES_AVAILABLE_PREVIOUS_STEP) == this.vaccinesNumber)){

                globalValues.put(COMPUTATION_PHASE, INFECTION_PHASE);
                System.out.println("Vaccination phase terminated, " + this.vaccinesNumber + " remaining");

                int rndInfected;

                do{
                    Random rnd = new Random();
                    rndInfected = rnd.nextInt(getNumVertices());
                } while(list.get(rndInfected).getComputationalValue().isImmune());

                list.get(rndInfected).setComputationalValue(DSSVertexState.INFECTED);

                for (ComputationalVertex<?, ?, DSSVertexState, Integer> vertex : list) {
                    sendMessageTo(vertex.getId(), 1);
                }

                this.vaccinationEndSuperstep = getSuperStep();
            }
        }

        globalValues.put(VACCINES_AVAILABLE_PREVIOUS_STEP, this.vaccinesNumber);
    }

    private void computeInfectionPhase(ComputationalVertex<?, ?, DSSVertexState, Integer> computationalVertex) {


        if(getSuperStep() - this.vaccinationEndSuperstep == 3){
            computationalVertex.voteToHalt();

        } else {


            if(computationalVertex.getComputationalValue().isInfected()){
                if(getBooelanWithProbability(this.recoveryRate)){
                    //get better
                    computationalVertex.setComputationalValue(DSSVertexState.SUSCEPTIBLE);
                } else {
                    //infect others

                    Iterator<? extends Edge<?>> outEdgesIterator = computationalVertex.getOutEdgesIterator();

                    while (outEdgesIterator.hasNext()){
                        Edge<?> edge = outEdgesIterator.next();

                        if(getBooelanWithProbability(this.infectionRate)){
                            sendMessageTo(edge.getTargetIdx(), 1);
                        }
                    }
                }


            } else if(computationalVertex.getComputationalValue().isSusceptible() && computationalVertex.getMessages().size() > 1){
                //got infected
                computationalVertex.setComputationalValue(DSSVertexState.INFECTED);
            }
        }
    }

    private void computeVaccinationPhase(ComputationalVertex<?, ?, DSSVertexState, Integer> computationalVertex) {

        if ((getSuperStep() == 0 && computationalVertex.getComputationalValue().isImmune()) ||
                getSuperStep() != 0 && computationalVertex.getComputationalValue().isSusceptible() && computationalVertex.getMessages().size() >= 1) {



            computationalVertex.setComputationalValue(DSSVertexState.IMMUNE);

            Iterator<? extends Edge<?>> outEdgesIterator = computationalVertex.getOutEdgesIterator();

            int numberOutEdges = computationalVertex.getNumberOutEdges();

            int numberFriendsToVaccinate = numberOutEdges <= this.numberOfFriendsToVaccinate ? canDecreaseVaccinesNumber(numberOutEdges) : canDecreaseVaccinesNumber(this.numberOfFriendsToVaccinate);


            Set<Integer> friendsToVaccinate = randomSet(numberOutEdges, numberFriendsToVaccinate);
            int i = 0;

            while (outEdgesIterator.hasNext()){
                Edge<?> edge = outEdgesIterator.next();

                if(friendsToVaccinate.contains(i)){
                    sendMessageTo(edge.getTargetIdx(), computationalVertex.getId());
                }

                i++;
            }
        }

        computationalVertex.voteToHalt();
    }



    @Override
    protected void initializeGlobalObjects(HashMap<String, Object> globalValues) {
        globalValues.put(COMPUTATION_PHASE, VACCINATION_PHASE);
        globalValues.put(VACCINES_AVAILABLE_PREVIOUS_STEP, this.vaccinesNumber);
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
