
package ru.solandme.washwait.POJO.places;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Location {

    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("lng")
    @Expose
    private double lng;

    /**
     * 
     * @return
     *     The lat
     */
    public double getLat() {
        return lat;
    }

    /**
     * 
     * @param lat
     *     The lat
     */
    public void setLat(double lat) {
        this.lat = lat;
    }

    /**
     * 
     * @return
     *     The lng
     */
    public double getLng() {
        return lng;
    }

    /**
     * 
     * @param lng
     *     The lng
     */
    public void setLng(double lng) {
        this.lng = lng;
    }

}
