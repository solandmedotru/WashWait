
package ru.solandme.washwait.map.pojo.map;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Location {

    @SerializedName("lat")
    @Expose
    private float lat;
    @SerializedName("lng")
    @Expose
    private float lng;

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

    /**
     * 
     * @return
     *     The lng
     */
    public float getLng() {
        return lng;
    }

    /**
     * 
     * @param lng
     *     The lng
     */
    public void setLng(float lng) {
        this.lng = lng;
    }

}
