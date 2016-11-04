
package ru.solandme.washwait.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sys {

    @SerializedName("population")
    @Expose
    private long population;

    /**
     * 
     * @return
     *     The population
     */
    public long getPopulation() {
        return population;
    }

    /**
     * 
     * @param population
     *     The population
     */
    public void setPopulation(long population) {
        this.population = population;
    }

}
