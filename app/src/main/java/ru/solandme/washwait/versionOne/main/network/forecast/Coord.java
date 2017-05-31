
package ru.solandme.washwait.versionOne.main.network.forecast;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Coord {

    @SerializedName("lon")
    @Expose
    private float lon;
    @SerializedName("lat")
    @Expose
    private float lat;

    /**
     * 
     * @return
     *     The lon
     */
    public float getLon() {
        return lon;
    }

    /**
     * 
     * @param lon
     *     The lon
     */
    public void setLon(float lon) {
        this.lon = lon;
    }

    /**
     * 
     * @return
     *     The lat
     */
    public float getLat() {
        return lat;
    }

    /**
     * 
     * @param lat
     *     The lat
     */
    public void setLat(float lat) {
        this.lat = lat;
    }

}
