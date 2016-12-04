package pt.ist.rc.dss.analytics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Pedro Joaquim on 03-12-2016
 */
public class DSSVertexInfo {


    private DSSVertexState state;
    private int degree;
    private List<Integer[]>  neighbors;

    public DSSVertexInfo(DSSVertexState state, int degree) {
        this.state = state;
        this.degree = degree;
        this.neighbors = new ArrayList<>();
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public DSSVertexState getState() {
        return state;
    }

    public void setState(DSSVertexState state) {
        this.state = state;
    }

    public void addNeighborInfo(String id, String degree) {
        neighbors.add(new Integer[] {Integer.valueOf(id), Integer.valueOf(degree)});
    }

    public List<Integer[]> getIdOfMostPopularNeighbor(){

        Comparator<Integer[]> comparator = new Comparator<Integer[]>() {
            @Override
            public int compare(Integer[] left, Integer[] right) {
                return right[1] - left[1];
            }
        };

        Collections.sort(neighbors, comparator);

        return neighbors;

    }
}
