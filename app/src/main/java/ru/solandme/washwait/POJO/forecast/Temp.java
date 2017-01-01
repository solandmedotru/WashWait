
package ru.solandme.washwait.POJO.forecast;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Temp {

    @SerializedName("day")
    @Expose
    private double day;
    @SerializedName("min")
    @Expose
    private double min;
    @SerializedName("max")
    @Expose
    private double max;
    @SerializedName("night")
    @Expose
    private double night;
    @SerializedName("eve")
    @Expose
    private double eve;
    @SerializedName("morn")
    @Expose
    private double morn;

    /**
     * 
     * @return
     *     The day
     */
    public double getDay() {
        return day;
    }

    /**
     * 
     * @param day
     *     The day
     */
    public void setDay(double day) {
        this.day = day;
    }

    /**
     * 
     * @return
     *     The min
     */
    public double getMin() {
        return min;
    }

    /**
     * 
     * @param min
     *     The min
     */
    public void setMin(double min) {
        this.min = min;
    }

    /**
     * 
     * @return
     *     The max
     */
    public double getMax() {
        return max;
    }

    /**
     * 
     * @param max
     *     The max
     */
    public void setMax(double max) {
        this.max = max;
    }

    /**
     * 
     * @return
     *     The night
     */
    public double getNight() {
        return night;
    }

    /**
     * 
     * @param night
     *     The night
     */
    public void setNight(double night) {
        this.night = night;
    }

    /**
     * 
     * @return
     *     The eve
     */
    public double getEve() {
        return eve;
    }

    /**
     * 
     * @param eve
     *     The eve
     */
    public void setEve(double eve) {
        this.eve = eve;
    }

    /**
     * 
     * @return
     *     The morn
     */
    public double getMorn() {
        return morn;
    }

    /**
     * 
     * @param morn
     *     The morn
     */
    public void setMorn(double morn) {
        this.morn = morn;
    }

}
