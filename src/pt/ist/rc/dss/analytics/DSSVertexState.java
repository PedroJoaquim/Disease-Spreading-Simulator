package pt.ist.rc.dss.analytics;

/**
 * Created by Pedro Joaquim on 14-11-2016
 */
public enum  DSSVertexState {

    IMMUNE, SUSCEPTIBLE, INFECTED, RECOVERED;

    public boolean isImmune(){
        return this == IMMUNE;
    }

    public boolean isSusceptible(){
        return this == SUSCEPTIBLE;
    }

    public boolean isInfected(){
        return this == INFECTED;
    }

    public boolean isRecovered(){
        return this == RECOVERED;
    }
}
