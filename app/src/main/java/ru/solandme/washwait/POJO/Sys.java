
package ru.solandme.washwait.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sys {

    @SerializedName("type")
    @Expose
    private int type;
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("message")
    @Expose
    private double message;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("sunrise")
    @Expose
    private int sunrise;
    @SerializedName("sunset")
    @Expose
    private int sunset;

    /**
     *
     * @return
     *     The type
     */
    public int getType() {
        return type;
    }

    /**
     *
     * @param type
     *     The type
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     *
     * @return
     *     The id
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @param id
     *     The id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return
     *     The message
     */
    public double getMessage() {
        return message;
    }

    /**
     *
     * @param message
     *     The message
     */
    public void setMessage(double message) {
        this.message = message;
    }

    /**
     *
     * @return
     *     The country
     */
    public String getCountry() {
        return country;
    }

    /**
     *
     * @param country
     *     The country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     *
     * @return
     *     The sunrise
     */
    public int getSunrise() {
        return sunrise;
    }

    /**
     *
     * @param sunrise
     *     The sunrise
     */
    public void setSunrise(int sunrise) {
        this.sunrise = sunrise;
    }

    /**
     *
     * @return
     *     The sunset
     */
    public int getSunset() {
        return sunset;
    }

    /**
     *
     * @param sunset
     *     The sunset
     */
    public void setSunset(int sunset) {
        this.sunset = sunset;
    }

}
